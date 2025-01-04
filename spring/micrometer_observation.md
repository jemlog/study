## Observation

Timer와 Counter의 경우 @Observed 어노테이션만 붙이면 대체 가능하다

```kotlin
@Configuration
class ObservedAspectConfiguration {
    @Bean
    fun observedAspect(observationRegistry: ObservationRegistry?): ObservedAspect {
        return ObservedAspect(observationRegistry!!)
    }
}
```
Observation을 원하는 객체 위에 @Observed 붙이고 name=원하는태그 설정만 주면 된다
```kotlin
@Observed(name = "monitorService")
@Service
class MonitorService {

    fun order(){
        println("order")
    }

    fun cancel(){
        print("cancel")
    }
}
```
아래와 같이 그라파나에서 PromQL 설정해서 조회 가능하다
![스크린샷 2024-05-10 오후 11 49 31](https://github.com/jemlog/tech-study/assets/82302520/81f8b058-9e88-4cb2-9b2f-32a22f7b4076)

반면 Gauge의 경우 별도의 Gauge를 설정해줘야함. DB 조회 등의 로직이 들어갈 수 있다.