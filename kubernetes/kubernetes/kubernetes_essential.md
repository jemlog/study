## Namespace
오브젝트들을 그룹핑한다. 쿠버네티스 만들면 기본적으로 default namespace가 만들어진다.

### label
- part-of : 어플리케이션 전체 이름
- component : 구성요소
- name : 어플리케이션 실제 이름
- instance : 여러 어플리케이션 설치 시 구분
- version : App 버전 변경 시 같이 바꿔줘야 한다

## Deployment
- Pod를 만들어주는 역할을 한다.
- Namespace를 지정해서 특정 Namespace에 속하게 해준다.
- name을 지정해주는데, 한 namespace 안에서 이름 중복되면 안된다. (같은 종류의 Object끼리)
- nodeSelector: Pod를 띄울 노드를 선택
- resources 설정 안하면 Pod가 node의 자원 모두 쓴다
- volumeMounts : mountPath는 Pod 내에 만들어진다

## Service

### Service가 하는 역할
- **서비스 퍼블리싱** : 외부에서 내부로 트래픽을 연결
- **서비스 디스커버리** : 내부 DNS를 활용해서 Name으로 API 호출. 다른 NameSpace끼리도 통신 가능
- **서비스 레지스트리** : 서비스에 연결되 Pod IP를 자동 관리한다
- **로드밸런싱** : 클라이언트 요청을 Pod들에게 분배한다

### Service Type
- ClusterIP(default) : 클러스터 내부에서 접근 가능한 IP
- NodePort : 클러스터를 구성하고 있는 모든 노드에 NodePort가 할당된다. NodePort를 통해 접근하면 Service와 연결됨
- LoadBalancer : Load Balancer가 존재하면 External IP를 할당해준다. 클라우드 서비스를 사용하면 제공해주는 LB 쓰면 되고, 온프레미스면 MetalLB 같은 로드밸런서 구축 필요


