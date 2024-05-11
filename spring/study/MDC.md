### MDC의 용도
- Micrometer Tracing 사용할때 TraceID, SpanID, ParentSpanID를 MDC에 자동으로 저장
- Filter에서 직접 고유값 생성 후 MDC에 넣어주는 방식도 사용 가능

### Logback에서 MDC 내의 값 조회하는 방법

```shell
<encoder>
<Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %magenta([%thread,%X{traceId:default},%X{spanId:-}]) %highlight([%-3level]) %logger{5} - %msg %n</Pattern>
</encoder>
```

### TaskExecutor에서 메인 쓰레드 MDC 전파하는 법

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
### 코루틴에서 메인 쓰레드 MDC 전파하는 방법

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

### MDC 내부 구조

MDC는 내부적으로 `MDCAdapter`를 사용해서 데이터를 put/get 한다. 이때 SLF4J의 구현체를 어떤걸 사용하느냐에 따라 달라진다. `LogbackMDCAdapter`를 살펴보자.
Thread별 MDC 저장에 ThreadLocal을 사용한다.

```kotlin

class LogbackMDCAdapter {

    final ThreadLocal<Map<String, String>> readWriteThreadLocalMap = new ThreadLocal<Map<String, String>>();

    public void put(String key, String
    val ) throws IllegalArgumentException{
        if (key == null) {
            throw new IllegalArgumentException ("key cannot be null");
        }
        Map<String, String> current = readWriteThreadLocalMap . get ();
        if (current == null) {
            current = new HashMap < String, String>();
            readWriteThreadLocalMap.set(current);
        }

        current.put(key, val);
        nullifyReadOnlyThreadLocalMap();
    }

    @Override
    public String get(String key)
    {
        Map<String, String> hashMap = readWriteThreadLocalMap . get ();

        if ((hashMap != null) && (key != null)) {
            return hashMap.get(key);
        } else {
            return null;
        }
    }

    @Override
    public void clear()
    {
        readWriteThreadLocalMap.set(null);
        nullifyReadOnlyThreadLocalMap();
    }

}
```