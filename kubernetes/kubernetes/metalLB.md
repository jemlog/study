## MetalLB 설치

```shell
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.11.0/manifests/namespace.yaml
kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.11.0/manifests/metallb.yaml
```

## MetalLB 설정

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
      - 192.168.56.100-192.168.56.110  # IP 대역폭
```

## LoadBalancer Type
<img width="557" alt="스크린샷 2024-06-23 오후 11 44 26" src="https://github.com/jemlog/tech-study/assets/82302520/9b909119-eccb-4e32-a83b-841c4c1ec326">

Service를 LoadBalancer Type으로 설정하면 자동으로 metalLB에 연결되고, External IP 할당