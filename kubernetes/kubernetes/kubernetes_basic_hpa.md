## HPA

### Kubernetes HPA template
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  namespace: default
  name: myapp
  labels:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: helm
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: myapp
  minReplicas: 2
  maxReplicas: 4
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60 # 현재 Pod들의 평균 CPU 사용률을 기준으로 평가한다
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300 
      policies:
        - type: Percent
          value: 100
          periodSeconds: 15
    scaleUp:
      stabilizationWindowSeconds: 120 # 2분동안 60이상 유지시 스케일 아웃
      policies:
        - type: Percent
          value: 100
          periodSeconds: 15
        - type: Pods
          value: 4 # 파드 4개씩 감소
          periodSeconds: 15 # 15초 간격으로
      selectPolicy: Max
```

### HPA 영향 미치는 요소

Pod Spec의 resource와 연관 있다. 

```yaml
resources:
    requests:
      memory: "100Mi"
      cpu: "100m"
    limits:
      memory: "200Mi"
      cpu: "200m"
```

- requests : HPA에서 100%를 계산하기 위한 기준값
  - 만약 2개의 파드가 replicaSet을 통해 생성됐고, 각각 CPU 사용량이 **80m**, **70m**이라면 평균값인 75m requests의 cpu 임계치인 60m을 비교하면 임계치 초과로 스케일링 진행된다.  
- limits : 파드 하나의 리소스가 올라갈 수 있는 상한선

```shell
현대 Pod 수 * (평균 CPU / HPA CPU) = 변경될 Pod 수
```
해당 공식으로 계산했을때, 현재 Pod 개수보다 많다면 스케일링 발생

### HPA 동작 프로세스

현재 컨테이너 자원 사용량은 **containerd**가 모니터링하고 있음. 이때 kubelet이 CPU와 메모리를 **10초**에 한번씩 containerd에서 조회.
metrics-server가 addon으로 설치되어 있어야 60초 단위로 주기적으로 데이터를 수집한다. **kube-controller-manager**가 HPA Spec에 정의한 임계값과 메트릭을 **15초** 단위로 체크한다.
각각의 동작은 독립적으로 수행되므로, 스케일링이 실제 수행되기때지 최대 85초 이상 소요 가능하다.
