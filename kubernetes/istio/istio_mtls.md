# Istio와 mTLS

## Istio의 mTLS 적용 이유

<img width="897" alt="스크린샷 2024-06-06 오후 6 29 37" src="https://github.com/jemlog/toy-msa-repo/assets/82302520/64d4591c-cfec-46c1-9475-c3c6488044ea">

Istio는 쿠버네티스 클러스터에 Istio Ingress Gateway를 제공. 이때 외부 구간에서는 HTTPS로 통신하지만, 쿠버네티스 클러스터 내부로 들어오면 HTTP 평문으로 통신을 한다.

쿠버네티스 클러스터 내에서 평문 통신을 하면 악의적인 공격자가 내부로 들어와서 데이터 패킷을 탈취할 수 있다. 또한 Istio 문서에 따르면 멀티 리전에 대해 클러스터를 구축하면 다른 리전으로 데이터가 이동할때 
데이터가 탈취될 가능성이 있다고 한다.

<img width="583" alt="스크린샷 2024-06-06 오후 6 34 21" src="https://github.com/jemlog/toy-msa-repo/assets/82302520/a2a25282-5a53-4c3b-aca2-2b696f7f71b8">

### Envoy Proxy를 통한 mTLS 적용

Istio는 Envoy Proxy를 Pod에 Sidecar로 배포해서 인바운드/아웃바운드 트래픽을 관리한다.

Pod 내에서 MicroService와 Envoy Proxy는 HTTP 평문 통신을 하지만, Envoy Proxy 끼리는 mTLS Handshake를 맺고 HTTPS 통신을 수행한다.

만약 Istio가 없다면 Zero Trust를 위해 모든 파드에 개별적으로 TLS 인증서를 설치해야 했을 것이다. Istio는 이 과정을 단순화해준다.

## Istio의 mTLS 적용

Istio는 자동적으로 다른 워크로드 호출할때 mTLS를 사용하도록 sidecar를 구성

### mTLS 모드
- PERMISSIVE(default) : 워크로드가 mTLS 와 plainText 트래픽을 모두 허용
- STRICT : 워크로드가 mTLS 트래픽만 허용
- DISABLE : mTLS가 비활성화. 사용 권장되지 않음

### mTLS 모드 적용

```yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
spec:
  mtls:
    mode: STRICT
```
  
특정 namespace에 적용하면 해당 namespace에만 mTLS 정책이 걸리고, **istio-system** 네임스페이스에 적용하면 전역적으로 사용된다.