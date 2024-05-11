## Probe

### Startup Probe

App이 처음 기동중일때 사용하는 API다. 정해진 간격으로 health check를 하다가 한번이라도 성공하면 startup probe를 멈춘다. 
이후에는 readiness probe와 liveness probe 기능을 동작시킨다.