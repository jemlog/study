KafkaProducer는 비동기적으로 send()를 호출한다. 이때 send()를 호출한 후 메세지가 Batch에 적재되면 즉시 return을 수행한다. 즉, 반환값이 나오는 시점은 Broker에 데이터가 보내진 시점이 아닌, Batch에 적재된 시점이라는 것이다. 이 방식을 사용하면 메세지 단건 마다 전송을 위해 Network Blocking 되는 현상을 막을 수 있다.

완전 비동기로 실행되기 때문에 Callback을 사용해서 사후 처리를 할 수 있다. 이때 **하나의 파티션**으로 들어가는 메세지의 콜백 간에는 **순서가 명확히 보장**된다.

Callback의 경우 `kafka-producer-network-thread`가 수행한다. 이 스레드는 Sender 쓰레드로서 프로듀서에 하나만 존재한다. 따라서 Callback에 Blocking 작업이나 수행 시간이 긴 작업을 넣으면 Sender가 메세지를 전송하는 작업이 영향을 받는다. 따라서 시간이 오래 걸리는 작업을 수행해야 한다면 Executors를 통해 별도의 쓰레드 풀을 사용해야 한다.

### Record Accumulator의 구조

![](https://i.imgur.com/69V0zQz.png)


ProducerRecord는 각각의 Record Batch에 차례대로 적재된다. 이때 목적지 파티션별로 배치들이 나눠져서 적재된다. Producer는 복수개의 Batch를 한꺼번에 전송한다. 메세지는 내부 메모리가 `buffer.memory` 설정값 만큼 최대한 적재될 수 있다. buffer.memory는 Record Accumulator의 전체 사이즈 제한이다.

Record Accumulator는 내부적으로 Batches라는 ConcurrentMap<TopicPartition, Deque< batches 가지고 있다.

즉, 토픽의 파티션별로 Batch를 보관하는 Deque를 가지고 있는 것이다.

먼저 partition 정보를 기반으로 deque를 map에서 조회하는데, 기존에 해당 deque가 없다면 생성 후 map에 등록하는 과정을 거친다


![](https://i.imgur.com/TZe1bww.png)

1. Deque에서는 가장 마지막의 Batch를 가져온다
2. 만약 배치가 존재한다면 그 배치에 메세지를 넣기를 시도한다
3. 배치에 넣었다면 바로 RecordAppendResult를 반환한다


## Sender
Sender가 데이터를 읽는 단위는 **배치 단위**다. 이때 Sender Thread가 무조건 배치가 가득 차야 가져가는게 아니라 가득 차지 않아도 그냥 가져갈 수 있다.

Sender Thread는 하나의 배치만 가져갈수도 있고, 여러개의 배치를 가져갈 수도 있다. 배치에 메세지가 다 안차도 가져갈 수 있다.

Sender Thread는 한번 배치를 모두 읽어서 네트워크 전송을 통해서 데이터를 브로커로 전송하고 ACK를 받아온 후, 바로 다음 배치를 읽어온다. 이 작업을 조금만 더 텀을 길게 하기 위해서 linger.ms를 사용하고 이를 통해 배치 내부에 메세지가 조금 더 쌓이게 만드는 것이다.

Record Accumulator에서 배치를 가져올때 `max.inflight.requests.per.connection` 설정 가능. 만약 2로 설정했다면 각각의 파티션마다 2개의 배치를 한번에 가져온다.

![](https://i.imgur.com/AfgHcHl.png)


### Config
- linger.ms : Sender Thread로 메세지를 보내기전 배치로 메시지를 만들어서 보내기 위한 최대 대기 시간
- buffer.memory : Record Accumulator의 전체 메모리 사이즈
- batch.size : 단일 배치의 사이즈

linger.ms는 0보다 크게 할 필요 없다. 만약 Producer와 Broker 간의 전송이 느리다면 linger.ms를 20ms 이하로 설정해서 메세지가 매치로 적용되도록 만들기. 만약 전송이 빠르고 Producer에서 적재되는것도 잘 된다면 0으로 해도 된다.

만약 sync 방식으로 처리하면 어떻게 될까? sync는 메세지 하나마다 응답을 받아야 한다. 따라서 전송 자체는 batch 단위로 되지만 메세지는 오직 하나만 들어간다.


## 재전송 시간 파라미터 이해

- delivery.timeout.ms : Sender Thread가 메세지를 재전송 하는 과정을 전체적으로 얼마나 기다려줄지를 설정
    - delivery.timeout.ms >= linger.ms (보통 0) + request.timeout.ms
- request.timeout.ms : 메세지를 보내고 응답이 없을때 기다리는 타임 아웃. 에러는 그냥 바로 retry 하면 된다.
- max.block.ms : send() 호출 후, recordAccumulator가 꽉 찼을때 비기를 기다리는 시간. 이게 경과하면 예외 던진다.
- retry.backoff.ms : 전송 재시도를 위한 대기 시간
- retries : 재전송 횟수 조절
- 보통 retries 무한대 주고 delivery.timeout.ms를 조정한다. 기본은 2분이다.

브로커에 배치 단위로 전송하면, 각각의 배치별로 ACK를 받는다. 만약 ACK가 오지 않는 배치가 있다면 프로듀서가 재전송 한다. 순서 다르게 저장 가능. 사실 순서 다르게 저장되고, 메세지 내에 순서를 알 수 있는 데이터가 있다면 비즈니스 로직 상에서 순서 맞추면 된다. 재전송으로 인한 순서 변경까지 다 고려한다면 최적의 성능을 낼 수 없다.


### 장애가 발생 할 수 있는 상황

#### RecordAccumulator에 레코드를 추가할 수 없을 때

Sender가 Broker로 메세지를 전송할때 문제가 생겨서 RecordAccumulator에 데이터가 지속적으로 쌓여서 포화 상태가 될 수 있다. 이때 Producer는 `max.block.ms` 만큼 기다리다가 타임아웃이 발생하면 바로 예외를 반환한다

#### 프로듀서에서 브로커로 메세지를 보낸 후 응답이 없을 때

Sender는 메세지를 전송한 후, `request.timeout.ms` 만큼 응답을 기다린다. 타임아웃이 발생하면 `retry.backoff.ms` 만큼 대기한 후 메세지를 재전송한다.

이때 이미 브로커에 메세지가 정상적으로 저장됐는데, ACK의 응답이 느려서 타임아웃이 발생할 수 있다. 이 경우를 대비해서 멱등성을 보장해줘야 한다. **Idempotence Producer** 사용하면 해결 가능
