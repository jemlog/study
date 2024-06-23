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

### Probe 분석

- startupProbe
  - 해당 probe가 성공하면 Service의 endpoint에 pod IP를 연결한다.
  - 해당 probe가 실패하면 어플리케이션을 바로 재시작한다.
  - 해당 probe가 성공해야 readinessProbe와 livenessProbe가 실행된다.
  - 한번만 성공하면 그 다음부터는 호출되지 않는다.
- readinessProbe
  - 해당 Probe가 실패하면 Pod와 연결된 Service의 Endpoint에서 Pod IP가 제거된다.
  - 어플리케이션에 트래픽이 일시적으로 몰려서 부하가 걸렸을때 트래픽을 차단해줌으로써 회복 시간을 벌어주는 목적
  - livenessProbe보다는 실행 간격을 짧게 설정하는게 좋음
  - 파드가 살아있는 동안 지속적으로 체크한다.
- livenessProbe
  - 해당 Probe가 실패하면 심각한 장애로 판단하고 **어플리케이션이 재실행된다**.
  - probe 주기를 가장 길게 잡아야 한다.
  - 파드가 살아있는 동안 지속적으로 체크한다.

## Service

### Service가 하는 역할
- **서비스 퍼블리싱** : 외부에서 내부로 트래픽을 연결
- **서비스 디스커버리** : 내부 DNS를 활용해서 Name으로 API 호출. 다른 NameSpace끼리도 통신 가능
- **서비스 레지스트리** : 서비스에 연결되 Pod IP를 자동 관리한다
- **로드밸런싱** : 클라이언트 요청을 Pod들에게 분배한다

### Service Type
- ClusterIP(default) : 클러스터 내부에서 접근 가능한 IP
- NodePort : 클러스터를 구성하고 있는 모든 노드에 NodePort가 할당된다. NodePort를 통해 접근하면 Service와 연결됨
- LoadBalancer : Service 앞단에 로드밸런서를 배치하기 위한 목적. LB가 존재하면 External IP를 할당해준다. 



