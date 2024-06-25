## Ingress

### Nginx Ingress Controller Install (Helm)

```shell
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm pull ingress-nginx/ingress-nginx --version 4.10.0

# 압축 해제
tar -xf ingress-nginx-4.10.0.tgz

# 배포
cd ingress-nginx
curl -O https://raw.githubusercontent.com/k8s-1pro/install/main/ground/cicd-server/nginx/helm/ingress-nginx/values-dev.yaml
helm upgrade ingress-nginx . -f ./values-dev.yaml -n ingress-nginx --install --create-namespace
```

### Kubernetes Ingress template

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: myapp-ingress
spec:
  ingressClassName: nginx
  rules:
  - host: jeminapp.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: myapp
            port:
              number: 80
```

### Name based virtual hosting

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: name-virtual-myapp-ingress
spec:
  rules:
    - host: jeminapp.first.com
      http:
        paths:
          - pathType: Prefix
            path: "/"
            backend:
              service:
                name: myapp1
                port:
                  number: 80
    - host: jeminapp.second.com
      http:
        paths:
          - pathType: Prefix
            path: "/"
            backend:
              service:
                name: myapp2
                port:
                  number: 80
```
