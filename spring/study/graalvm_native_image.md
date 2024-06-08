## GraalVM

고성능 JDK, 네이티브 이미지 빌더이다.

네이티브 이미지 안에는 JVM도 포함이 된다. 따라서 별도의 JDK나 JRE가 필요 없다. 실행할 플랫폼만 맞춰서 빌드하면 된다.

즉, 애초에 실행 환경에 맞춰서 빌드를 진행한다. 서비스 제공 초기 준비 시간 짧고 메모리 사용량이 적다. 

네이티브 이미지로 컴파일하면 동적 런타임 할당 불가능하고 컴파일할때 미리 다 참조 가능해야 한다.

네이티브 이미지를 컴파일 하기 위해서는 힌트 파일을 생성해야 한다. 



ApplicationContext는 빈 생성을 beanFactory에게 위임한다.

beanFactoryPostProcessor 작업을 통해서 싱글톤 객체로 인스턴스화 할 빈을 탐색한다. 인스턴스화를 진행할 빈의 목록 beanDefinition을 로딩하고 실제 인스턴스화 하는 작업 수행, 빈 목록 찾는 단계 여기 속한다
빈 목록은 @Configuration 클래스 파싱해서 가져온다.

@Value @PostConstruct @Autowired 처리하기 위한 BeanPostProcessor들 등록됨. 의존성 주입은 빈 후처리기에 의해 처리된다. 프록시 빈 교체도 빈 후처리기가 한다

@Configuration 클래스들 다 파싱해서 beanDefinition 가져오고, 그거 기반으로 beanFactory에서 다 생성한다.

@Value, @PostConstruct, @Autowired는 모두 빈을 생성자 리플렉션으로 만든 뒤에 PostBeanProcessor를 통해 주입한다.

@Configuration 클래스 파싱 해서 ComponentScan도 파싱

ComponentScanAnnotationParser가 @Component 클래스 로딩

전부 BeanDefinition으로 정의


GraalVM Native Image의 특징
- 더 적은 메모리 사용
- 더 빠른 시작 시간

컨테이너 이미지를 사용해서 배포하는데 적합하다, FaaS 플랫폼과 조합했을때 좋다

실행 파일 만들기 위해 AOT 프로세싱이 필요하다.

GraalVM Native Image는 완전하고 플랫폼 종속적인 실행파일이다. 네이티브 이미지 실행 위해서 JVM 없어도 된다.

JVM랑 다른 점
- main 엔트리 포인트에서 빌드 타임에 어플리케이션에 대한 통계 분석이 이뤄진다.
- 네이티브 이미지가 만들어질때 도달하지 못한 코드는 삭제되고 실행 파일의 일부가 되지 못한다.
- 어플리케이션의 클래스패스가 빌드 타임에 고정되고 변경 안된다.
- lazy 클래스 로딩 안되고, 시작 시점에 모든 실행파일들이 메모리에 올라온다.

@Profile 사용 제한되고 @CondititionalOnProperty 같은거 제한된다

Spring AOT 프로세스는 만들어낸다
- 자바 소스 코드
- 바이트코드
- GraalVM JSON 힌트 파일


- GraalVM
- Java 21
- Native Image

21버전부터 JIT 컴파일러 성능 넘어선다.
네이티브 이미지가 Throughput per memory에서 모든 메모리 크기에더 더 좋은 성능을 보인다. 특히 어플리케이션이 작거나 중간 크기의 힙을 가지면 약 1GB 정도, JIT보다 약 2배 정도의 향상을 보인다.

21부터는 Linux AArch64에서 G1GC 돌아간다 Linux x64는 물론.

G1과 PGO의 조합이 좋다.

-Ob : quick build mode
- O2. Default 이 최적화 레벨은 10-15배 빠른 컴파일 타임을 가지고, 자바 마이크로서비스 워크로드에 맞춰져 있다.
- O3 : 새로 들어왔다. 만약 PGO 사용하면 자동으로 동작한다.

이제 빌드 작업에서 몇개의 스레드를 사용할찌 —parallelism 명령어 사용해서 지정 가능하다.

네이티브 이미지 빌드 프로세스에 메모리 제한 걸 수 있다. 기본적으로 빌드 프로세스는 free memory 사용 가능하다. 이 접근은 너의 머신과 다른 프로세스를 느리게 하는걸 회피하기 위해 의도돼었다.

만약 프리 메모리가 8GB 미만이면, 빌드 프로세스는 전체의 85% 사용한다. 만약 빌드 하는 중에 머신이 느려지는거 같으면 다른 프로세스를 종료해라.

메타데이터 엔트리가 없다면, MissingReflectionMetadataException이 발생하면서 실패한다.

새로운 클래스 초기화 정책

: 모든 클래스가 이제 빌드 타임에 사용되고 초기화되는게 허용된다, 클래스 초기화 설정과 상관 없이. 