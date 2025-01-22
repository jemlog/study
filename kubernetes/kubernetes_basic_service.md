## Service

### Kubernetes Service template

```yaml
apiVersion: v1
kind: Service
metadata:
  namespace: jemlog-test
  name: api-tester-1231
  labels:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
spec:
  selector:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231
  ports:
    - port: 80
      targetPort: http
      nodePort: 31232
  type: NodePort
```

### Service가 하는 역할
- **서비스 퍼블리싱** : 외부에서 내부로 트래픽을 연결
- **서비스 디스커버리** : 내부 DNS를 활용해서 Name으로 API 호출. 다른 NameSpace끼리도 통신 가능
- **서비스 레지스트리** : 서비스에 연결되 Pod IP를 자동 관리한다
- **로드밸런싱** : 클라이언트 요청을 Pod들에게 분배한다

### Service Type
- ClusterIP(default) : 클러스터 내부에서 접근 가능한 IP
- NodePort : 클러스터를 구성하고 있는 모든 노드에 NodePort가 할당된다. NodePort를 통해 접근하면 Service와 연결됨
- LoadBalancer : Service 앞단에 로드밸런서를 배치하기 위한 목적. LB가 존재하면 External IP를 할당해준다. 

