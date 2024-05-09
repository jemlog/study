### TaskExecutor에서 MDC 전파하는 방법

Spring Async를 사용할때 TaskExecutor를 그대로 사용하면 톰캣 스레드의 MDC가 TaskExecutor의 스레드에 전파되지 않는다.
따라서 `TaskDecorator`를 사용해서 MDC Context를 복사해서 넣어야 한다.

```kotlin
class MDCTaskDecorator: TaskDecorator {
    override fun decorate(runnable: Runnable): Runnable {
        
        val copyOfContextMap = MDC.getCopyOfContextMap()
        
        return Runnable {
            MDC.setContextMap(copyOfContextMap)
            runnable.run()
        }
    }
}
```
이후 TaskDecorator를 TaskExecutor에 적용해야 한다. 참고로 TaskExecutor에 `Virtual Thread`를 사용하더라도 똑같이 적용하면 된다.
```kotlin
@Configuration
@EnableAsync
class AsyncConfig {

    /*
    Async에 Virtual Thread 사용하기
    MDC 전파까지 적용
     */
    @Bean
    fun taskExecutor(): TaskExecutor {
        val taskExecutorAdapter = TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor())
        taskExecutorAdapter.setTaskDecorator(MDCTaskDecorator())
        return taskExecutorAdapter
    }
}
```
### 코루틴에서 MDC 전파하는 방법

코루틴에서는 MDC 전파를 위해 별도의 라이브러리를 다운 받아야 한다.

```groovy
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.6.0")
```

이후 `runBlocking()`같이 코루틴을 시작하는 부분에 MDCContext()를 주입하면 된다

```kotlin
runBlocking(MDCContext()) {
    log.info("실행")
}
```