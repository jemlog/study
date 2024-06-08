## 취소

- 만약 자식 코루틴에서 CancelException 발생하면 부모로 전파 안된다
- 만약 부모 코루틴 내에서 독립적인 Scope을 만들었다면 Launch의 경우 바로 예외 터지고, async는 await해야 안다
- 만약 부모 자식 관계라면 둘 다 예외 바로 터진다. async는 예외 안 터뜨리려면 반드시 supervisorJob() 넣어야 함
- laucnh이고 root라면 Exceptionhander 넣어진다.

