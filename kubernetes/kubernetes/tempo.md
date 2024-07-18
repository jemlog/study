## Grafana Tempo Template

```yaml
apiVersion: apps/v1 
kind: Deployment 
metadata: 
  name: tempo-deployment 
  labels: 
    app: tempo 
spec: 
  selector: 
    matchLabels: 
      app: tempo 
  template: 
    metadata: 
      labels: 
        app: tempo
    spec: 
      containers: 
      - name: tempo
        image: grafana/tempo
        args: 
        - -config.file=/conf/tempo.yaml 
        ports: 
        - containerPort: 3100
          name: prom-metrics
        - containerPort: 6831
          name: jaeger-thrift-c
          protocol: UDP
        - containerPort: 6832
          name: jaeger-thrift-b
          protocol: UDP
        - containerPort: 14268
          name: jaeger-thrift-h
        - containerPort: 14250
          name: jaeger-grpc
        - containerPort: 9411
          name: zipkin
        - containerPort: 55680
          name: otlp-legacy
        - containerPort: 4317
          name: otlp-grpc
        - containerPort: 55681
          name: otlp-http
        - containerPort: 55678
          name: opencensus
        volumeMounts: 
        - name: tempo-config
          mountPath: /conf
      volumes:
      - name: tempo-config
        configMap:
          name: tempo-configmap
          items:
          - key: tempo.yaml
            path: tempo.yaml
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: tempo-configmap
data:
  tempo.yaml: |
    server:
      http_listen_port: 3100
    distributor:
      receivers:
        otlp:
          protocols:
            grpc:
            http:
    storage:
      trace:
        backend: s3
        s3:
          bucket: tempo
          endpoint: minio-service:9000
          access_key: minio_access_key
          secret_key: minio_secret_key
          insecure: true
---
apiVersion: v1 
kind: Service 
metadata: 
  name: tempo-service 
  labels: 
    app: tempo
spec: 
  selector: 
    app: tempo
  ports: 
  - name: tempo-prom-metrics
    port: 3100
    targetPort: 3100
  - name: jaeger-metrics
    port: 16687
    targetPort: 16687
  - name: tempo-query-jaeger-ui
    port: 16686
    targetPort: 16686
  - name: tempo-jaeger-thrift-compact
    port: 6831
    protocol: UDP
    targetPort: 6831
  - name: tempo-jaeger-thrift-binary
    port: 6832
    protocol: UDP
    targetPort: 6832
  - name: tempo-jaeger-thrift-http
    port: 14268
    protocol: TCP
    targetPort: 14268
  - name: grpc-tempo-jaeger
    port: 14250
    protocol: TCP
    targetPort: 14250
  - name: tempo-zipkin
    port: 9411
    protocol: TCP
    targetPort: 9411
  - name: tempo-otlp-legacy
    port: 55680
    protocol: TCP
    targetPort: 55680
  - name: tempo-otlp-http-legacy
    port: 55681
    protocol: TCP
    targetPort: 4318
  - name: grpc-tempo-otlp
    port: 4317
    protocol: TCP
    targetPort: 4317
  - name: tempo-otlp-http
    port: 4318
    protocol: TCP
    targetPort: 4318
  - name: tempo-opencensus
    port: 55678
    protocol: TCP
    targetPort: 55678
```

### Minio Object Storage Template

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: minio-deployment
  labels:
    app: minio
spec:
  replicas: 1
  selector:
    matchLabels:
      app: minio
  template:
    metadata:
      labels:
        app: minio
    spec:
      containers:
      - name: minio
        image: minio/minio:latest
        command:
        - /bin/bash
        - -c
        args: 
        - minio server /data --console-address :9001
        ports:
        - containerPort: 9000
        - containerPort: 9001
        volumeMounts:
        - name: minio-data
          mountPath: /data
      volumes:
      - name: minio-data
        persistentVolumeClaim:
          claimName: minio-data-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: minio-service
spec:
  selector:
    app: minio
  ports:
    - protocol: TCP
      name: minio-port
      port: 9000
      targetPort: 9000
    - protocol: TCP
      name: minio-console-port
      port: 9001
      targetPort: 9001
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: minio-data-pv
  labels:
    app: minio-data-pv
spec:
  storageClassName: ""
  capacity:
    storage: 2Gi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: /mnt/f/dev/data/minio-data
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: minio-data-pvc
spec:
  volumeName: minio-data-pv
  storageClassName: ""
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 2Gi
```