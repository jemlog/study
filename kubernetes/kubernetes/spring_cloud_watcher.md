## Kubernetes에서 ConfigMap Hot Reload 하는 법

- 기존 Spring Cloud는 Config를 Restart 없이 Reload할때 actuator의 /refresh나 spring cloud bus를 사용함
- Spring Cloud Kubernetes에서는 Watcher Controller를 클러스터에 배포해서 처리 가능