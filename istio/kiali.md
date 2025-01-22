# Kiali 사용법

### Kiali 설치
```shell
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/kiali.yaml
```

### Prometheus 설치
```shell
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/prometheus.yaml
```

### 트래픽 전송 시 모습
<img width="876" alt="스크린샷 2024-06-09 오전 1 42 53" src="https://github.com/jemlog/tech-study/assets/82302520/d9a75bec-25d0-464c-b2e5-a84d9b6c07ab">

### mTLS 표시
아래의 Security를 체크해야 Istio mTLS가 적용된 트래픽인지 자물쇠를 통해 알 수 있다.

<img width="199" alt="스크린샷 2024-06-09 오전 1 45 04" src="https://github.com/jemlog/tech-study/assets/82302520/dada095c-8ac7-461f-b5ab-4e478cc75a7c">

Istio Config를 통해 직접 여러가지 Rule들을 설정 가능하다

<img width="1312" alt="스크린샷 2024-06-09 오전 1 48 05" src="https://github.com/jemlog/tech-study/assets/82302520/09ee0612-4a02-4278-a322-a042caabd7da">