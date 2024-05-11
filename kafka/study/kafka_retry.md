만약 메세지 처리에 실패하면 메세지는 특정한 backoff와 함께 retry 토픽으로 들어간다. retry 토픽 컨슈머는 타임스탬프를 체크하고 아직 시간이 안됐다면
토픽 파티션에 대한 소비를 멈춘다. 만약 파티션 소비가 재개되면 메세지는 다시 소비된다. 
만약 메세지 처리가 다시 실패하면 그 다음 retry 토픽으로 들어간다. 만약 계속 실패하면 dlt로 들억나다.

만약 1000ms 부터 시작해서 2배씩 증가하고 최대 시도를 4번으로 한다면
main-topic, main-topic-retry-1000, main-topic-retry-2000, main-topic-retry-4000, main-topic-dlt 이렇게 생성된다

만약 이 전략을 사용하면 해당 토픽에 대한 카프카의 순서 보장 장점을 잃는다.
AckMode를 설정할 수 있지만, RECORD 전략이 추천된다

```kotlin
@DltHandler
fun processMessage(message: MyPojo){
    
}
```
이런 방식으로 dltHandler 설정 가능. 이거 안만들면 consumtion 했다는걸 로깅만 해주는 default dltHandler가 사용된다.

retryabletopic 설정할때 카프카 템플릿 설정 안해주면, defaultRetryTopicKafkaTemplate을 찾는다. 만약에 이거 없으면 예외 던져진다.

```java
@Bean
public RetryTopicConfiguration myRetryTopic(KafkaTemplate<String, Object> template) {
    return RetryTopicConfigurationBuilder
            .newInstance()
            .create(template);
}
```

```java
@Bean
public RetryTopicConfiguration myRetryTopic(KafkaTemplate<String, MyPojo> template) {
    return RetryTopicConfigurationBuilder
            .newInstance()
            .fixedBackOff(3000)
            .maxAttempts(5)
            .concurrency(1)
            .includeTopics("my-topic", "my-other-topic")
            .create(template);
}

@Bean
public RetryTopicConfiguration myOtherRetryTopic(KafkaTemplate<String, MyOtherPojo> template) {
    return RetryTopicConfigurationBuilder
            .newInstance()
            .exponentialBackoff(1000, 2, 5000)
            .maxAttempts(4)
            .excludeTopics("my-topic", "my-other-topic")
            .retryOn(MyException.class)
            .create(template);
}
```

retry와 dlt의 컨슈머는 topic의 suffix와 gruopId 파라미터로 설정한 것의 조합인 groupid로 컨슈머 그룹에 속하게 된다.

만약 너가 아무것도 제공하지 않으면 그것들은 같은 그룹에 속하고 retry 토픽의 리밸런스가 메인 토픽에도 불필요한 리밸런스 만들 수 있다

## Backoff policy

기본 back off policy는 fixedBackoffpolicy이다 1초 간격 최대 3번 시도

exponentialbackoffpolicy는 30초의 맥시멈 딜레이 존재.

글로벌 타임아웃도 설정 가능. 만약 이 타임아웃 지나면 바로 dlt로 보내버린다

너가 retry를 하고자 하는 exception을 명시할수도 있다.

2.8 버전부터는 재시도 없이 dlt로 보내버리는 치명적인 에러의 종류가 있다. 

## Log Compaction
tomstone이나 트랜잭션 마커는 중요하다. 근데 컨슈머가 처리하기 전에 삭제 되버리면 큰일이다. 따라서 적절한 처리를 통해 삭제 전에 접근 되도록 보장해야 한다.

