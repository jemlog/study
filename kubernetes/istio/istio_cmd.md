# Istio 명령어

### Istio Sidecar Auto Injection 활성화
```shell
kubectl label namespace default istio-injection=enabled
```

### 특정 Namespace에만 Istio Sidecar Injection 사용
```shell
 kubectl apply -f <(istioctl kube-inject -f samples/bookinfo/platform/kube/bookinfo.yaml)
```

