# Istio Traffic Management

## Virtual Service

Istio 트래픽 관리를 유연하고 효과적으로 하는데 핵심 역할을 한다.

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

