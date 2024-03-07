## Namespace
- Object들을 그루핑해주는 역할을 한다

### Metadata
- name
- labels

## Deployment
- 파드를 만들고 업그레이드 하는 역할을 한다

- 하나의 네임스페이스 안에서 metadata의 name은 중복되면 안된다 (같은 종류 끼리)
- template : 여기서부터 실제 구성
- nodeSelector: 파드가 띄워질 노드를 선택한다

![스크린샷 2024-03-07 오후 2.04.43.png](..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-03-07%20%EC%98%A4%ED%9B%84%202.04.43.png)