# Istio Traffic Management

## Virtual Service

Istio 트래픽 관리를 유연하고 효과적으로 하는데 핵심 역할을 한다.

Virtual Service의 전형적인 유스케이스는 service subset으로 특정된 서비스의 다른 버전들에게 트래픽을 전송하는 것이다. 클라이언트는 virtual Service 호스트에게 트래픽을 전송한다. 그러면 envoy는 virtual service rule에 따라 다른 버전들에게 트래픽을 전달한다.

예를 들어 20% 트래픽은 새로운 버전에게 보낸다. 이것은 카나리 롤아웃을 가능하게 해준다. 트래픽 라우팅은 인스턴스 deployment로부터 완전히 분리되어 있다.

새로운 서비스 버전을 적용하는 인스턴스의 수는 스케일업 하거나 다운 할 수 있다, 트래픽 로드에 따라서 reffeing to traffic routing at all 하지 않고.
반대로, 쿠버네티스 같은 컨테이너 오케스트레이션은 오직 인스턴스 스케일링 기반으로 트래픽 분산을 지원한다. 그래서 금방 복잡해진다.

Multiple 어플리케이션 서비스를 하나의 single virutal service를 사용해서 다룬다. 예를 들어 너가 쿠버네티스를 사용한다면 너는 virtual service를 configure할 ㅅ ㅜ있다. 특정 네임스페이스 내의 모든 서비스를 핸들링 하기 위해서. 여러개의 실제 서비스에 하나으 ㅣvirtual service를 매핑하는 것은 모놀리틱 어플리케이션을 복ㅈ바한 마이크로서비스로 전환할때 유용하다, 서비스의 소비자가 변화에 적응하기를 피룡로 하지 않고.

Destination Rule은 어떻게 로드밸런싱할지 등을 다룬다.


### Traffic Routing

사용자가 설정한 Rule에 따라 트래픽 라우팅을 진행한다. 별도의 Rule을 지정하지 않으면 모든 버전에 대해 **Round Robin**으로 트래픽을 라우팅한다.

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: reviews
spec:
  hosts:
    - reviews
  http:
  - match:
    - headers:
        end-user:
          exact: jason # HTTP Header 값을 사용해서 Routing 할 수 있다
    route:
    - destination:
        host: reviews
        subset: v2
  - route:
    - destination:
        host: reviews
        subset: v1
```

## Destination Rule

VirtualService와 함께 Istio 트래픽 라우팅의 핵심 부분. 
VirtualService가 어느 목적지로 트래픽을 라우팅할지 결정한다면, Destination Rule은 그 목적지에 대한 트래픽에 어떤 일이 발생하는지를 설정한다.
Destination Rule은 Virtual Service를 평가한 뒤에 적용되므로, 트래픽의 실제 목적지에 적용된다.



## Network resilience

Istio 실패 회복 특징은 어플리케이션에게 매우 투명하다. 즉, 어플리케이션은 엔보이 사이드카 프록시가 응방르 반환하기 전체 called service에 대한
실패를 핸들링하는지 모른다. 이것은 만약 너가 어플리케이션 코드에 실패 회복 전략을 구성한다면, 너는 반드시 그것들이 별도로 동작하고 충돌할 수 있다는걸 알아야 한다
예를 들어서 너는 두개의 타임아웃을 가지고 있다. 하나는 virtual service이고, 나머지 하나는 어플리케이션이다.
어플리케이션은 API Call에 2초의 타임아웃을 지정했다. 그러나 너는 virtual service에 1회의 재시도와 함께 3초의 타임아웃을 설정했다. 이 상황에서는 어플리케이션 타임아웃이 먼저 발생하고,
그래서 너의 엔보이 타임아웃과 재시도는 효과가 없다.

이스티오 실패 복구 특징은 메시 내으 ㅣ서비스들에 대한 안정성과 가용성을 향상시키는 반면, 어플리케이션은 반드시 실패와 에러를 좆정하고 적절한 fallback action 해야한다.
예를 들어서, 로드 밸런싱 풀의 모든 인스턴스가 다운되면, 엔보이는 503 코드를 준다. 어플리케이션은 503 코드를 처리하기 위한 fallback login을 반드시 적용해야 한다.

