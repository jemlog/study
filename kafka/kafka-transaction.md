# 카프카 트랜잭션

## 카프카 트랜잭션 예제 분석


![](https://i.imgur.com/1NeoL2i.png)

앨리스가 밥에게 10달러를 전송하는 시나리오이다.

1. transfer 이벤트는 transfer topic으로 들어간다.
2. transfer 앱은 해당 토픽의 메세지를 컨슘한다.
3. transfer 앱에서 Alice와 Bob 각각의 Balance를 10$씩 증감하는 메세지를 프로듀싱한다. 이때 Key를 유저 이름으로 설정하면 파티션이 알아서 나눠져서 들어갈 것이다.
4. 마지막으로 transfer 토픽의 파티션을 어느 offset까지 읽어왔는지가 기록된다.

결국, 초기 컨슘 후, 프로듀싱 작업을 모두 완료한 뒤에 컨슈머가 offset 커밋을 진행하는 것이다.

![](https://i.imgur.com/hMyn5J1.png)

이때 만약 Alice에게 보내는건 정상적으로 완료됐으나, Bob한테 프로듀싱 하는데 장애가 났다면 어떻게 될까. 카프카 트랜잭션을 사용하면 이미 보내진 Alice의 토픽 파티션에 Abort 표시를 한다.

![](https://i.imgur.com/BpWU06I.png)

만약 transfer 앱이 transfer 토픽에서 메세지를 컨슘하고, Alice에게는 프로듀싱을 완료한 뒤, 어플리케이션이 죽었다고 생각해보자.

그러면 새로운 어플리케이션이 올라올 것이고, 이 컨슈머 어플리케이션은 어디부터 다시 읽어야 하는지 consumer_offset을 가지고 판단할 것이다. 근데 아직 커밋 안했기 때문에 이전에 읽었던 데이터를 다시 읽게 되고, 똑같은 데이터 다시 Alice의 balance 토픽에 프로듀싱 하게 된다.

마지막에 consumer offset을 커밋하게 될 것이고, balance topic에 대한 컨슈머는 결국 Alice의 메세지 두개를 중복으로 컨슘하게 된다.


![](https://i.imgur.com/X4PWumI.png)

만약 프로듀서에 transactional.id를 설정하게 되면, 브로커에 transaction coordinator가 생성된다. 이 coordinator는 트랜잭션 메타데이터 관리하고 전체 트랜잭션 플로우를 관리한다.

The transaction coordinator is chosen in a similar fashion to the consumer group coordinator, but instead of a hash of the group.id, we take a hash of the transactional.id and use that to determine the partition of the __transaction_state topic. The broker that hosts the leader replica of that partition is the transaction coordinator.

트랜잭션 코디네이터는 컨슈머 그룹 코디네이터와 비슷하게 선택된다. 이때 컨슈머 그룹 코디네이터는 group.id를 해싱하지만, 트랜잭션 코디네이터는 transactional.id를 해시해서 transaction_state topic의 파티션을 결정하는데 사용한다. 해당 파티션의 리더 브로커가 transaction coordinator가 된다.

1. 먼저 어플리케이션이 transactional.id와 함께 요청을 coordinator에게 보낸다. 그리고 그 id는 PID와 트랜잭션 epoch와 함께 매핑되서 다시 어플리케이션에 반환된다.
2. 다음으로 transfer 이벤트는 어플리케이션에 의해 컨슘되고, coordinator에게 새로운 트랜잭션이 시작되었음을 알린다.
3. 프로듀서가 balance 토픽에 이벤트 쓰기 전에 그것은 코디네이터에게 쓸꺼라고 알려준다.
4. 코디네이터가 transaction_state에 해당 정보를 적고 나면, 메세지를 실제 전송한다

### 만약 여기서 실패 한다면?

![](https://i.imgur.com/gsWCm2S.png)


1. 새로운 인스턴스가 비슷한 방법으로 시작될 것이다. transactional.id를 처음 보내주지만 해당 transactional.id로 pending된 트랜잭션이 있는걸 발견한다.
2. 이 경우에는 transaction epoch를 증가시키고 abort 마커를 이전 트랜잭션으로 인해 영향 받은 모든 파티션에 표시한다. 이거는 transaction_static_tpic에도 표시된다.
3. 이것은 효과적으로 실패한 인스턴스를 묶어둔다.
4. 새로운 인스턴스는 PID와 Epoch를 새로 받고 새 작업을 이어나간다.
5. 다운스트림 서버는 isolation.level을 가지고 있어서 read_commited로 설종되면 모든 aborted event를 무시한다.

만약 정상적으로 작업이 다 수행되면, 컨슈머는 오프셋 커밋을 진행한다. 이때 오프셋 커밋을 컨슈머가 아니라 프로듀서가 진행한다는게 중요하다.
그 다음으로 트랜잭션 코디네이터에게 작업이 완료되었음을 알리고 transaction state 에는 지금까지 작업한 파티션(consumer_offsets 포함) 그 다음에 C를 찍는다.
트랜잭션 컨슈머는 작업한 모든 파티션에 Commit marker를 찍는다.
다 찍고 나면 read_commited한 컨슈머에게 데이터를 읽어올 수 있음을 알려준다. 즉, 커밋 마커가 무조건 찍혀있어야 데이터 땡겨올 수 있다.
아하 일단 무조건 C나 A가 찍혀있어야 데이터 긁어올 수 있고, 만약 컨슈머가 데이터 가져왔는데, 메타데이터에 Abort 되어 있으면 그냥 컨슈머 레벨에서 무시 해버린다. 끝에 C나 A 안찍혀있으면 그냥 무시 해버리는 것이다.

## 카프카와 DB 트랜잭션 연계



### @KafkaListener 내부에서 DB 트랜잭션과 함께 사용하는 상황

@KafkaListener를 사용해서 KafkaMessageListenerContainer를 사용하면 자동으로 카프카 트랜잭션은 시작된다.

이때 DB 트랜잭션을 함께 사용하고 싶으면 메서드 상단에 @Transactional 붙여주면 된다.

적용 순서
1. DB 트랜잭션이 먼저 커밋
2. 이후 Kafka 트랜잭션이 커밋

만약 DB 트랜잭션은 성공했지만, Kafka 트랜잭션이 실패해서 재시도를 진행하면 DB에는 데이터 중복 적재 가능성 있다. 데이터 저장의 멱등성 보장 필요

메세지 보낼때 DB의 유니크 키를 사용해서 DB 차원에서 유일성 제약 조건 검사하도록 구성 가능. 만약 타임스탬프 값이 있다면 해당 값을 유니크 키로 설정 가능

데이터 특성에 따라서 Upsert를 사용하는 것도 방법

## Producer에서 메세지를 보낼때 DB 트랜잭션과 묶는 상황

@Transactional 어노테이션 붙이고 설정값이 transactional.id 값 넣어주면 DB 트랜잭션과 묶어서 사용 가능

DB 트랜잭션이 정상 커밋되면 카프카 트랜잭션도 커밋된다.

순서 바꿔서 카프카 트랜잭션을 먼저 커밋하고 싶으면 nested 형태로 사용 가능

```java
@Transactional("dstm") 
public void someMethod(String in) {
    this.jdbcTemplate.execute("insert into mytable (data) values ('" + in + "')"); 
    sendToKafka(in); 
} 
@Transactional("kafkaTransactionManager") 
public void sendToKafka(String in){ 
    this.kafkaTemplate.send("topic2", in.toUpperCase()); 
}
```

