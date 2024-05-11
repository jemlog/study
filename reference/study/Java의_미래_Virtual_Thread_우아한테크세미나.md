## [4월 우아한테크세미나] ‘Java의 미래, Virtual Thread’

참고 영상 링크
- [https://www.youtube.com/watch?v=BZMZIM-n4C0&t=1231s](https://www.youtube.com/watch?v=BZMZIM-n4C0&t=1231s)

### Virtual Thread 선택 배경

배민에서 전사 게이트웨이 시스템을 개발했다. 모든 트래픽을 게이트웨이가 먼저 받아야 하기 때문에 안정성과 처리량에 대해 고민함.
처음에는 Kotlin의 Coroutine을 선택해서 개발했지만, JDK 21 버전 릴리즈 된 이후 Virtual Thread를 적용함.

### 기존 Thread 모델의 특징

- 스레드 생성 비용이 크다. 따라서 효율적으로 사용하기 위해서는 스레드풀을 통해 관리하는게 중요
- 사용하는 메모리 크기가 크다. 스레드 당 **최대 2MB**까지 사용한다.
- OS에 의해 스케줄링 되기 때문에 자주 **시스템 콜**이 발생한다.
- 기존 Thread는 OS Thread와 일대일 매핑되는 플랫폼 스레드이다. `Thread.start()` 하는 순간 JNI에 의해 OS Thread 생성 후 매핑된다.
- 작업 단위 Runnable

### Virtual Thread 특징

- 스레드 생성 비용이 적다
- 스레프풀 없이 운영 가능하다. 요청이 들어올때 새로 생성하고 사용 후 파기하는 구조
- 사용 메모리 크기가 작다. **최대 수십 KB**만 사용한다.
- **JVM 스레드 스케쥴링**과 **Continuation**을 통해 **NonBlocking I/O**를 지원한다.
- 기존 스레드 코드를 상속했기에 코드가 호환된다.
- 캐리어 스레드와 1:N 매핑된다.
- 작업 단위 Continuation

![스크린샷 2024-04-29 오후 11.50.05.png](..%2F..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-04-29%20%EC%98%A4%ED%9B%84%2011.50.05.png)

### Virtual Thread 내부 구현

![스크린샷 2024-04-30 오전 12.04.57.png](..%2F..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-04-30%20%EC%98%A4%EC%A0%84%2012.04.57.png)

모든 Virtual thread가 동일한 스케줄러 공유
ForkJoinPool을 통해 스케쥴링 한다.

![스크린샷 2024-04-30 오전 12.08.35.png](..%2F..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-04-30%20%EC%98%A4%EC%A0%84%2012.08.35.png)

### Continuation

- 실행 가능한 작업 흐름
- 중단 가능
- 중단 지점으로부터 재실행 가능

### 최범균님 강의

기존의 Thread Per Request 모델에서는 I/O가 발생하면 OS 쓰레드 자체가 블로킹 되서 낭비된다.
가상 스레드는 I/O 블로킹 되면 캐리어 스레드가 다른 가상 스레드 실행한다.

주의할 점
- Pinned : 가상 스레드가 캐리어 스레드에 고정되는 것
  - 예를 들어 synchonized 블록에서 I/O 블로킹
  - 가상 쓰레드 블로킹이 끝날 때까지 플랫폼 쓰레드도 같이 블로킹
- synchronized 보다 Lock을 사용해서 피하기
- 네이티브 메서드 또는 foreign 함수 사용할때 발생
- mysql jdbc driver에서 가상 스레드 사용하면 pinned 된다
- ThreadLocal 사용하면 좋지만, 가상 스레드는 무지 많이 만들어지기 때문에 주의하기
- 가상 스레드 풀링하지 말기