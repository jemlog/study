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
  ingressClassName: nginx # 해당 값을 사용해서 Ingress와 IngressController를 연결
  rules:
  - host: jeminapp.com # 어떤 도메인을 수신할지 결정
    http:
      paths:
      - path: / # 모든 URI의 트래픽을 수신한다
        pathType: Prefix
        backend:
          service:
            name: myapp # 구체적인 Kubernetes Service 설정
            port:
              number: 80
```

### Ingress Controller

Ingress Controller는 Deployment를 사용해서 Pod로 쿠버네티스에 배포된다. 이때 Ingress Controller Pod에 연결된 Service가 LoadBalancer Type로 생성되고, 
외부의 LB(AWS LB or MetalLB)에 연결되는 구조다.

### Ingress Internal

Ingress Controller와 Ingress는 `IngressClass`를 통해 연결된다.

```yaml
apiVersion: networking.k8s.io/v1
kind: IngressClass
metadata:
  name: nginx # 해당 이름으로 Ingress에서 조회
  annotations:
    ingressclass.kubernetes.io/is-default-class: "true"
spec:
  controller: k8s.io/ingress-nginx # 해당 이름으로 Ingress Controller 매핑
```

Ingress Controller는 Kubernetes api-server를 통해 모든 Namespace의 Ingress를 검색할 수 있어야 한다. 이를 위해서 **ClusterRole**을 부여한다.

Helm Template을 통해 배포할때 **rbac**을 true로 설정하기 (default true)

**ClusterRole**
```yaml
- apiGroups:
  - discovery.k8s.io
  resources:
  - endpointslices
  verbs:
  - get
  - list
  - watch
- apiGroups:
  - networking.k8s.io
  resources:
  - ingresses
  verbs:
  - get
  - list
  - watch
```

**ClusterRoleBinding**
```yaml

roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: ingress-nginx
subjects:
- kind: ServiceAccount
  name: ingress-nginx
  namespace: ingress-nginx
```

Ingress를 조회해서 연결된 Service들을 찾았다면, 다시 연결된 `EndPointSlice`에 있는 Pod IP를 조회한 후 직접 트래픽을 Pod로 전송. 

### Name based virtual hosting

하나의 IP에 대해 여러 domain이 설정되어 있다면, domain name에 따라 다른 Service로 분기처리 가능

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
