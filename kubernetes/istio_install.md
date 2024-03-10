# Istio 설치

### Master Node에 Istio 경로 추가
```shell
curl -L https://istio.io/downloadIstio | sh -
cd istio-1.17.1 && export PATH=$PWD/bin:$PATH
```
### Istio 설치

```shell
istioctl install --set profile=demo -y
```
### 정상 설치 확인
```shell
# egressgateway, ingressgateway, istiod 모두 나오면 정상 설치
kubectl get pods -n istio-system
```

### Istio Injection 활성화
```shell
kubectl label namespace default istio-injection=enabled
```

### Reference
https://istio.io/latest/docs/setup/getting-started/