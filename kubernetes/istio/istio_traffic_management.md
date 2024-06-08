# Istio Traffic Management

## Virtual Service

Virtual Service는 Istio 트래픽 관리를 유연하고 효과적으로 만드는데 핵심 역할을 한다.

### Traffic Routing
HTTP 통신 중 특정 헤더의 여부에 따라 다른 버전의 서비스로 라우팅한다. 만약 이런 설정이 아예 없다면 버전과 상관없이 round robin으로 
트래픽을 전송한다.

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
          exact: jason
    route:
    - destination:
        host: reviews
        subset: v2
  - route:
    - destination:
        host: reviews
        subset: v1
```
이런 방식으로 특정 버전의 서비스를 fix해서 트래픽을 전송할 수 있다.
```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: productpage
spec:
  hosts:
  - productpage
  http:
  - route:
    - destination:
        host: productpage
        subset: v1
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: reviews
spec:
  hosts:
  - reviews
  http:
  - route:
    - destination:
        host: reviews
        subset: v1
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: ratings
spec:
  hosts:
  - ratings
  http:
  - route:
    - destination:
        host: ratings
        subset: v1
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: details
spec:
  hosts:
  - details
  http:
  - route:
    - destination:
        host: details
        subset: v1
---
```

## Destination Rule

Destination Rule은 Istio 트래픽 라우팅 기능의 핵심 부분이다. Virtual Service가 어떻게 목적지로 트래픽을 라우팅 하는지를 알려준다면, Destination Rule은 목적지에서 트래픽이 어떤 일이 발생하는지를 알려준다.

기존적으로 이스티오는 least requests 로드밸런싱 정책을 사용한다. 

- Random
- Weighted
- Round Robin

이 3가지의 방식을 추가로 제공한다.

## Network resilience

Istio 실패 회복 특징은 어플리케이션에게 매우 투명하다. 즉, 어플리케이션은 엔보이 사이드카 프록시가 응방르 반환하기 전체 called service에 대한
실패를 핸들링하는지 모른다. 이것은 만약 너가 어플리케이션 코드에 실패 회복 전략을 구성한다면, 너는 반드시 그것들이 별도로 동작하고 충돌할 수 있다는걸 알아야 한다
예를 들어서 너는 두개의 타임아웃을 가지고 있다. 하나는 virtual service이고, 나머지 하나는 어플리케이션이다.
어플리케이션은 API Call에 2초의 타임아웃을 지정했다. 그러나 너는 virtual service에 1회의 재시도와 함께 3초의 타임아웃을 설정했다. 이 상황에서는 어플리케이션 타임아웃이 먼저 발생하고,
그래서 너의 엔보이 타임아웃과 재시도는 효과가 없다.

이스티오 실패 복구 특징은 메시 내으 ㅣ서비스들에 대한 안정성과 가용성을 향상시키는 반면, 어플리케이션은 반드시 실패와 에러를 좆정하고 적절한 fallback action 해야한다.
예를 들어서, 로드 밸런싱 풀의 모든 인스턴스가 다운되면, 엔보이는 503 코드를 준다. 어플리케이션은 503 코드를 처리하기 위한 fallback login을 반드시 적용해야 한다.

