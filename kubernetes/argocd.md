argo cd는 반드시 형상관리로 GIT을 사용해야 한다

### Image Updater

사용하면 도커 허브에서 컨테이너 이미지 변경을 감지할 수 있다. 배포 파이프라인 구축 가능

### Rollouts

카나리 블루 그린 같은 배포 지원

## 쿠버네티스 위 아키텍처

아르고 CD는 쿠버네티스 클러스터 위에 설치된다

Node Port를 통해 Argo CD UI 접근 가능
Master Node의 CTL을 사용해서 CLI 날릴수도 있다

- Repo Server : 이 서버가 GIT과 연결되서 Git에 있는 yml 파일을 가지고 와서 매니페스트 생성한다
- Application Controller : K8s의 리소스를 모니터링해서 GIT과 현재 상태가 다른지 체크, 만약 다르면 배포 실행
- Kube API : K8S 배포 진행

Argo CD 제품들은 Artifact HUB에서 Helm 패키지로 설치 가능하다

