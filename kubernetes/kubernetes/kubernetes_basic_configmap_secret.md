## ConfigMap, Secret

### Kubernetes ConfigMap, Secret Template

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