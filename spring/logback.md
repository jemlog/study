## Logback 구성 요소

### Logger
특정 이름을 가진 Logger의 동작 방식을 정의한다. logback에는 root Logger가 존재하는데 이 경우에는 모든 Logger에 공통으로 적용되는 설정을 할 수 있다. 
만약 자식 Logger에서 `additivity=false`를 지정하지 않으면 자식 Logger에 설정이 전파된다.

Logger에는 여러개의 Appender를 ref할 수 있다.

### Appender
구체적으로 어디에 로깅을 할지 결정한다. 콘솔에 찍고 싶으면 ConsoleAppender, 파일에 찍고 싶으면 FileAppender 쓰면 된다.
Appender에 `appender-ref`로 Appender를 할당할 수 있는데, 이거는 Fallback 용도이다. 
하나의 Appender에는 하나의 appender-ref만 설정 가능하다. 따라서 AsyncAppender에도 하나의 appender만 ref 가능.

**encoder**

로그를 어떤 형식으로 출력할지 설정 가능하다. 아래의 형식처럼 %X{,,,-default} 형식을 사용해서 MDC에 저장된 값 가져올 수 있다.
```shell
<encoder>
<Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %magenta([%thread,%X{traceId:-},%X{spanId:-}]) %highlight([%-3level]) %logger{5} - %msg %n</Pattern>
</encoder>
```

**filter**

appender 내에서는 level 기반의 filtering도 가능하다

```shell
<filter class="ch.qos.logback.classic.filter.LevelFilter">
    <level>INFO</level>
    <onMatch>ACCEPT</onMatch>
    <onMismatch>DENY</onMismatch>
</filter>
```

## Appender

Logback에서 로깅을 출력하는 기능을 수행한다. 아래의 인터페이스를 공통적으로 구현한다.

![스크린샷 2024-05-09 오후 10 10 21](https://github.com/jemlog/tech-study/assets/82302520/26a14899-a265-4ec5-aacd-989de450a14d)

`doAppend(E event)`에서 E 타입은 기본적으로 ILoggingEvent가 들어온다. Appender는 로그 이벤트 출력을 담당하지만 이벤트를 formatting하는 작업은
Layout이나 Encoder 객체에 위임한다.

### AppenderBase

AppenderBase는 Appender를 구현한 추상 클래스이다. Appender의 Name이나 activation status 그리고 layout이나 filter를 get/set하는 등의 모든 
Appender가 공유하는 기본 기능을 제공한다. 로그백을 통해 사용하는 모든 Appender의 상위 클래스이다. 

## AsyncAppender의 동작방식

AsyncAppender는 IloggingEvents를 비동기적으로 로깅한다. 
이 Appender는 event dispathcer의 역할만 하기 때문에 반드시 다른 appender를 ref해야 한다.

AsyncAppender는 로그의 적재 과정을 톰캣 스레드가 아닌 별도의 Wokrer Thread가 처리하도록 만든다.
따라서 직접 파일이나 네트워크를 통해 로그를 출력하기 위한 I/O 시간을 단축시킬 수 있다.

AsyncAppender는 BlockingQueue에 로그를 적재한다. 그리고 Worker Thread가 이벤트루프와 비슷하게 무한 루프를 돌면서 
BlockingQueue에서 데이터를 가져와 ref된 Appender(ex. KafkaAppender, FileAppender)에 넘겨준다.
그러면 실제 Appender들이 로그를 적재하는 작업을 수행한다.

### AsyncAppender 설정값

- BlockingQueue Size : 256 (default)
- discardingThreshold : 20% (default)
- neverBlock : false (default)
- maxFlushTime : 0 (default)

neverBlock이 false면 Queue Size가 가득 차면 톰캣 스레드가 Blocking 된다.
neverBlock이 true면 Blocking은 되지 않지만 ERROR,WARN 이외의 로그가 모두 버려진다.
따라서 netty의 eventloop를 사용하는 api-gateway 같은 컴포넌트에서는 **neverblock을 꼭 true로 설정해야 한다**.
