## Kafka Commit 

커밋 종류
- Auto Commit
- Sync Manual Commit
- Async Manual Commit

### Consumer_offsets

브로커에는 **consumer_offsets**라는 토픽이 있다. 이 토픽은 컨슈머 그룹 별로 어떤 파티션이 어느 오프셋 까지 읽혔는지에 대한 정보를 가지고 있다.

> Ex. 컨슈머 그룹 1에서 Topic A의 1번 파티션에서 offset: 3까지 읽어옴

즉, 어떤 컨슈머가 어떤 파티션에 할당되었는지는 중요하지 않다. 컨슈머는 consumer_offsets에서 자신이 할당 받은 컨슈머가 어디까지 읽혀있는지만 확인하면 된다.

컨슈머가 어떤 파티션을 할당받았는지에 대한 구체 정보는 Group Coordinator나 Consumer Group이 가지고 있다.

### Commit과 관련된 장애 상황

카프카는 분산 시스템이기에 장애가 발생할 수 있다. 만약 컨슈머가 브로커로부터 데이터를 읽어와서 DB에 저장한 뒤, 다시 브로커의 commit_offsets에 커밋하기 전에 컨슈머가 장애로 다운되는 상황을 가정해보자. 
그렇다면 다음 컨슈머는 어디부터 읽어야 할까.

아직 consumer_offsets에는 이전 컨슈머가 읽어간 최신 오프셋이 업데이트 되지 않았기 때문에 데이터를 중복으로 읽어올 가능성이 있다. 이를 완전히 방지할 수는 없기 때문에 카프카 컨슈머와 DB 저장 로직이 묶여있을 시, DB에서 멱등한 처리를 해줘야 한다.

#### DB의 멱등성 처리 방법

- 유니크 키를 사용한 중복 체크
- 유니크 인덱스, 클러스터링 인덱스 유일성 제약 조건
    - 데이터 Insert 속도를 위해 유일성 제약 조건 체크나 Index 비활성화 가능도 함

### Auto Commit

`auto.enable.commit=true` 옵션을 사용해서 메세지를 브로커에 바로 commit 하지 않고, `auto.commit.interval.ms`에 정해진 주기 마다 consumer가 자동으로 commit을 수행한다

그렇다고 막 랜덤한 타이밍에 하는게 아니라, poll() 할때 수행이 되기는 하는데, 그 주기가 5초마다 실행되는 poll()과 함께 실행되는 것이다.

> [!NOTE]
> 1번째 poll()이 3초, 2번째 poll()이 3초가 걸렸다면 3번째 poll()이 시작될때 2번째 poll()까지의 offset이 커밋된다.
>

만약 next poll()이 못 돌아서 오프셋 오토 커밋을 못 했더라도, close()를 명시적으로 잘 호출하면 읽어온 부분까지는 커밋이 찍힌다.

컨슈머는 데이터 가져온 **배치 만큼 한번에 커밋**한다.

카프카는 기본적으로 auto commit을 사용하지 않는다. kafkaListener를 사용하면 kafkaMessageListenerContainer 내부에서 Manual Commit을 처리한다.

### Manual Commit

#### CommitSync

- 메세지 배치를 poll()에서 읽어온 후에 해당 메세지의 마지막 오프셋을 브로커에 commit
- 브로커에 커밋 적용이 성공적으로 될때까지 블로킹 된다
- 만약 브로커에서 커밋 실패가 오면 commitSync는 성공할때까지 계속 시도한다. 다음 poll() 호출 안함
- 비동기 방식 대비 더 느린 수행 시간

#### CommitAsync

- 메세지 배치 읽어온 뒤에 메세지 마지막 오프셋 브로커에 커밋 요청
- 일단 요청 후에는 신경 안쓴다
- 만약 커밋 정상 반영 안되면 다시 커밋 시도 안한다
- 그렇기 때문에 만약 컨슈머 장애 나거나 리밸런스 되면 중복으로 읽어올 수 있다.