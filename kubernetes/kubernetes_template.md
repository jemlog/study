## 쿠버네티스 Pod 생성 템플릿

해당 부분이 DevOps 엔지니어가 관리하는 릴리즈 파일에 속해 있는 것이다.
개발자는 CI 툴에서 빌드를 통해 Docker Hub로 밀어 올리기만 하면 ArgoCD의 Image Updater가 이미지 변동 감지하고 쿠버네티스 배포를 수행한다

### NameSpace

```yaml
apiVersion: v1
kind: Namespace 
metadata:
  name: jemlog-test # Object 전체를 담당하는 namespace 이름
  labels:
    part-of: k8s-jemlog # 어플리케이션의 전체 이름
    managed-by: dashboard 
```

### Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: jemlog-test
  name: api-tester-1231
  labels:
    part-of: k8s-jemlog # 어플리케이션의 전체 이름
    component: backend-server # 서비스를 구성하고 있는 각각의 분리된 기능, 이게 Object를 나누는 단위는 아니다
    name: api-tester # component 밑에는 여러 종류의 name이 존재할 수 있다
    instance: api-tester-1231 # 인스턴스 식별
    version: 1.0.0
    managed-by: dashboard # 어떤 도구로 배포됐는지에 대한 것, 이거는 정보성 데이터
spec:
  selector: # 두 오브젝트를 연결하는데 사용한다
    matchLabels: 
      part-of: k8s-jemlog
      component: backend-server
      name: api-tester
      instance: api-tester-1231 # 인스턴스 정보는 필수값, 나머지는 옵션값
  replicas: 2 # 처음이 2개의 POD를 띄운다
  strategy:
    type: RollingUpdate # 순차적으로 배포를 진행한다
  template: # 여기 변경되면 업데이트로 판단
    metadata:
      labels:
        part-of: k8s-jemlog
        component: backend-server
        name: api-tester
        instance: api-tester-1231
        version: 1.0.0
    spec:
      nodeSelector:
        kubernetes.io/hostname: k8s-master # Master Node를 선택
      containers:
        - name: api-tester-1231
          image: sjmin/docker-test
          ports:
          - name: http # Service에서 해당 이름과 매칭
            containerPort: 8080 # 컨테이너 포트 지정
          envFrom:
            - configMapRef:
                name: api-tester-1231-properties # 설정 정보
          startupProbe:
            httpGet:
              path: "/health"
              port: 8080
            periodSeconds: 5 # 5초마다 한번씩 진행
            failureThreshold: 24 # 24번 넘어가면 재부팅
          readinessProbe:
            httpGet:
              path: "/health"
              port: 8080
            periodSeconds: 10
            failureThreshold: 3 # readiness는 실패 시 Service Object 분리 후 트래픽 차단
          livenessProbe:
            httpGet:
              path: "/health"
              port: 8080
            periodSeconds: 10
            failureThreshold: 3 # liveness는 실패 시 파드 재기동
          resources:
            requests:
              memory: "100Mi"
              cpu: "100m"
            limits:
              memory: "200Mi"
              cpu: "200m"
          volumeMounts:
            - name: files # config 정보 어디 넣는지 지정
              mountPath: /usr/src/myapp/files/dev # Pod 내 Path
            - name: secret-datasource # 비밀 정보 어디 넣는지 지정
              mountPath: /usr/src/myapp/datasource # Pod 내 Path
      volumes:
        - name: files # 상단에서 files와 매칭되는거 찾는다
          persistentVolumeClaim:
            claimName: api-tester-1232-files # PV에서 해당 이름으로 보내기
        - name: secret-datasource
          secret:
            secretName: api-tester-1231-postgresql
```

### Service
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
### ConfigMap, Secret
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  namespace: jemlog-test
  name: api-tester-1231-properties
  labels:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
data:
  spring_profiles_active: "dev"
  application_role: "ALL"
  postgresql_filepath: "/usr/src/myapp/datasource/postgresql-info.yaml"
---
apiVersion: v1
kind: Secret
metadata:
  namespace: jemlog-test
  name: api-tester-1231-postgresql
  labels:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
stringData:
  postgresql-info.yaml: |
    driver-class-name: "org.postgresql.Driver"
    url: "jdbc:postgresql://postgresql:5431"
    username: "dev"
    password: "dev123"
```

### PVC, PV 
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  namespace: jemlog-test
  name: api-tester-1231-files
  labels:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: kubectl
spec:
  resources:
    requests:
      storage: 2G # 해당 값과 PV의 2G는 동일한 값으로 맞춰야 한다
  accessModes:
    - ReadWriteMany
  selector:
    matchLabels:
      part-of: k8s-jemlog
      component: backend-server
      name: api-tester
      instance: api-tester-1231-files
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: api-tester-1231-files
  labels:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231-files
    version: 1.0.0
    managed-by: dashboard
spec:
  capacity:
    storage: 2G
  volumeMode: Filesystem
  accessModes:
    - ReadWriteMany
  local:
    path: "/root/k8s-local-volume/1231" # 마스터 노드의 해당 디렉토리를 스토리지로 활용
  nodeAffinity:
    required:
      nodeSelectorTerms:
        - matchExpressions:
            - {key: kubernetes.io/hostname, operator: In, values: [k8s-master]} # 해당 name을 가진 노드 사용
```
### HPA
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  namespace: jemlog-test
  name: api-tester-1231-default
  labels:
    part-of: k8s-jemlog
    component: backend-server
    name: api-tester
    instance: api-tester-1231
    version: 1.0.0
    managed-by: dashboard
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment # HPA는 Deployment Object에 연동된다
    name: api-tester-1231 # 해당 이름의 Deployment 설정 필요
  minReplicas: 2 # 최소 2개 운영
  maxReplicas: 4 # 최대 4개까지 증가
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60 # 현재 Pod들의 평균 CPU 사용률을 기준으로 평가한다
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 120 # 만약 CPU 사용률이 120초 동안 60% 이상을 유지하면 스케일링이 수행된다
```