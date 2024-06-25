## Deployment

### Kubernetes Deployment template

```yaml
### Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: default
  name: myapp
spec:
  selector: # 두 오브젝트를 연결하는데 사용한다
    matchLabels: 
      app: myapp
  replicas: 2 # 처음이 2개의 POD를 띄운다
  strategy:
    type: RollingUpdate # 순차적으로 배포를 진행한다
  template: # 여기 변경되면 업데이트로 판단
    metadata:
      labels:
        app: myapp
    spec:
      nodeSelector:
        kubernetes.io/hostname: k8s-master # Master Node를 선택
      containers:
        - name: myapp
          image: sjmin/myapp
          ports:
          - name: http # Service에서 해당 이름과 매칭
            containerPort: 8080 # Container Port
          envFrom:
            - configMapRef:
                name: myapp-properties # 설정 정보
            - secretRef:
                name: myapp-mysql
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
            claimName: myapp-files # PV에서 해당 이름으로 보내기
        - name: secret-datasource
          secret:
            secretName: myapp-mysql
```

### Probe 분석

- startupProbe
    - 해당 probe가 성공하면 Service의 endpoint에 pod IP를 연결한다.
    - 해당 probe가 실패하면 어플리케이션을 바로 재시작한다.
    - 해당 probe가 성공해야 readinessProbe와 livenessProbe가 실행된다.
    - 한번만 성공하면 그 다음부터는 호출되지 않는다.
- readinessProbe
    - 해당 Probe가 실패하면 Pod와 연결된 Service의 Endpoint에서 Pod IP가 제거된다.
    - 어플리케이션에 트래픽이 일시적으로 몰려서 부하가 걸렸을때 트래픽을 차단해줌으로써 회복 시간을 벌어주는 목적
    - livenessProbe보다는 실행 간격을 짧게 설정하는게 좋음
    - 파드가 살아있는 동안 지속적으로 체크한다.
- livenessProbe
    - 해당 Probe가 실패하면 심각한 장애로 판단하고 **어플리케이션이 재실행된다**.
    - probe 주기를 가장 길게 잡아야 한다.
    - 파드가 살아있는 동안 지속적으로 체크한다.