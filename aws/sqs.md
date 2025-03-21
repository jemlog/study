## SQS 학습

### MessageDuplicationId
- FIFO 방식을 사용할때만 사용 가능하다
- 5 minute window로 Duplication을 트래킹한다.
- 만약 window 범위 내에 같은 ID를 가진 메세지가 큐에 들어오면 Ack는 되지만 Consumer에게 제공되지 않는다.
- Duplicate 트래킹은 메세지가 Receive & Delete 된 이후에도 지속된다.