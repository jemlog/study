## Istio Ingress Gateway를 사용한 트래픽 연결

### LB 설정 (MetalLB)

**MetalLB 설치**
```shell
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.11.0/manifests/namespace.yaml
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.11.0/manifests/metallb.yaml
```

**MetalLB 설정**
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  namespace: metallb-system
  name: config
data:
  config: |
    address-pools:
    - name: default
      protocol: layer2
      addresses:
      - 192.168.56.100-192.168.56.110  # 가상머신에 할당한 IP 대역폭 중 선택
```

### Kubernetes Cluster에 Istio 설치 (Helm)

**Istio와 Istio Ingress Controller 설치**
```shell
$ helm repo add istio https://istio-release.storage.googleapis.com/charts
$ helm repo update

$ kubectl create namespace istio-system
$ helm install istiod istio/istiod -n istio-system --set istio_cni.enabled=true --wait
$ helm install istiod istio/istiod -n istio-system --wait

$ kubectl create namespace istio-ingress
$ helm install istio-ingress istio/gateway -n istio-ingress --wait
```

### Gateway & VirtualService 설정

**Gateway 설정**
```yaml
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: jeminapp-gateway
  namespace: default
spec:
  selector:
    istio: ingress
  servers:
    - port:
        number: 80
        name: http
        protocol: HTTP
      hosts:
        - "jeminapp.com"
    - port:
        number: 443
        name: https
        protocol: HTTPS
      tls:
        mode: SIMPLE # enables HTTPS on this port
        credentialName: jeminapp-com-credential # fetches certs from Kubernetes secret
      hosts:
        - "jeminapp.com"
```

**VirtualService 설정**
```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: jeminapp-virtualservice
  namespace: default
spec:
  hosts:
    - "jeminapp.com"
  gateways:
    - default/jeminapp-gateway # namespace/gateway
  http:
    - match:
        - uri:
            prefix: / # all traffic
      route:
        - destination:
            host: myapp # kubernetes service name
            port:
              number: 80 # service port
```

