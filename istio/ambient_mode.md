## Ambient Mode vs Sidecar Mode

### Sidecar Mode

**장점**
- Envoy Proxy를 사용하기 위해 어플리케이션 코드를 변경하지는 않아도 된다

**단점**
- 배포된 파드의 **일정 리소스를 예약**해야함. 성능 저하가 발생할 수 있다.
- 업데이트 위해서는 파드를 재배포 해야 한다.
- 모든 워크로드가 L7 레이어의 복잡한 기능을 요구하지는 않지만 사이드카 모드에서는 사용 여부 결정 불가능

### Ambient Mode

Ambient Mesh는 노드 단위의 공유 리소스인 **ztunnel**을 제공한다. 이 리소스를 통해서 mTLS를 처리한다.

**장점**
- L4 레이어의 기능과 L7 레이어의 기능을 선택적으로 적용 가능하다
- 파드에 Envoy Proxy가 Sidecar로 Injection되는 형태가 아니기 때문에 파드의 리소스와 완전 분리
- L4 까지의 기능만 원할때는 Secure overlay로써 L4 Traffic Routing과 mTLS 처리 정도만 지원한다. 가볍게 유지 가능.
- L7 기능을 원하는 namespace에서는 Envoy 기반 Waypoint Proxy로 처리한다. Waypoint Proxy는 쿠버네티스 파드이기 때문에 동적인 확장 가능.

> L7 처리를 로컬 노드에서 처리하지 않고 별도의 Waypoint Proxy에서 처리함으로써 Network Hop으로 인한 지연은 없나?

- 지연은 거의 발생하지 않음. 기존에는 L7 레이어 작업을 클라이언트와 서버 각각의 사이드카에서 2번 처리해야했지만, 웨이포인트 프록시를 사용해서 1번만 처리하면 되기 때문에 속도 차이 거의 없음.
