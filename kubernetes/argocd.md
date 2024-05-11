## ArgoCD Image Updater

보통 개발자가 App의 코드를 올리는 Repository와 데브옵스 엔지니어가 yaml 파일을 관리하는 App 릴리즈 전용 레포가 다르다

만약 데브옵스 엔지니어가 리소스 스펙을 변경해야 한다면 수작업을 해야하지만, 개발자가 소스를 젠킨스에 올려서 빌드하고 도커 허브에 업로드 하는 과정과 그 도커 허브에 변경사항이 있을때 감지하고 자동배포 하는 플로우는 자동화가 가능하다.

1. 리소스 스펙 변경
- 아르고 CD에서 알아서 변경 감지 해서 자동 배포를 해준다

2. App 버전 업그레이드
- 만약 만약 Jenkins에서 소스 코드 빌드 후 도커 허브에 올리는 작성을 실행했다면, Docker hub의 변경 사항을 ArgoCD Image Updater에서 감시하고 있다가 변경 사항을 땡겨와서 쿠버네티스 자동 배포를 실행한다

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

