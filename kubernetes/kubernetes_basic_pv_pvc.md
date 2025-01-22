## PVC, PV

### Kubernetes PVC,PV template

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