# 카프카 멀티 브로커

### Kafka Multi Broker Docker Template
```yaml
---
version: '3.8'
services:
  zookeeper-1:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
    ports:
      - "22181:2181"

  kafka-1:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper-1
    ports:
      - 29092:29092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-1:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1

  kafka-2:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper-1
    ports:
      - "39092:39092"
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-2:9092,PLAINTEXT_HOST://localhost:39092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1

  kafka-3:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper-1
    ports:
      - "49092:49092"
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka-3:9092,PLAINTEXT_HOST://localhost:49092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
```

### Kafka-UI Docker Template
```yaml
version: '2'
services:
  kafka-ui:
    image: provectuslabs/kafka-ui
    container_name: kafka-ui
    ports:
      - "8989:8080"
    restart: always
    environment:
      - KAFKA_CLUSTERS_0_NAME=local
      - KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka-1:29092,kafka-2:29093,kafka-3:29094
      - KAFKA_CLUSTERS_0_ZOOKEEPER=zookeeper-1:22181
```

### Controller 브로커의 역할

만약 Controller가 아닌 브로커가 다운되서 zookeeper에게 session 기간동안 heartbeat를 보내지 않으면, 해당 브로커 노드 정보를 갱신한다
컨트롤러는 zookeeper를 모니터링 하는 과정에서 Watch Event로 브로커에 대한 Down 정보를 받는다
그러면 죽은 브로커가 리더인 파티션을 어떻게 해야할지 결정해야 한다. 리더 파티션 옮겨야 한다. 결국 데이터는 리더 파티션이 받는다.
이 작업을 컨트롤러가 수행한다. 다운된 브로커가 관리하던 파티션들에 대해 새로운 Leader/follower 결정
만약 다른 브로커가 해당 파티션의 리더 역할을 맡기로 하면 리더 파티션이 옮겨가고, 컨트롤러는 바뀐 정보를 주키퍼에게 넒긴다. 해당 파티션을 복제하는 모든 브로커에게 새로운 leader 정보 전달하고 새로운 리더로부터 복제 fetch할 것을 요청한다

각각의 브로커는 메타데이터 캐시라는걸 가지고 있고, 정보 바뀔때마다 리프레시 해줘야 한다

내부적으로 삭제된 브로커에 대한 fetcher를 날려버린다. 근데 이때 멀쩡한 브로커들도 fetcher를 날렸다가 다시 할당한다.


### ISR(In-Sync Replicas)

만약 파티션의 리더 브로커가 다운되면 팔로워 브로커를 리더 브로커가 될 수 있다. 이때 ISR 내에 있는 팔로워만 리더로 승격될 자격이 있다.

리더 브로커는 팔로워 브로커가 리더가 될 수 있는지 지속적으로 모니터링 한다.

### ISR 조건

1. 브로커가 주키퍼에 연결되어 있어야 한다. zookeeper.session.timeout.ms(기본 6초, 최대 18초)로 지정된 시간 내에 HeartBeat를 지속적으로 주키퍼에게 전송해야 한다.
2. 팔로워 브로커는 replica.log.time.max.ms(기본 10초, 최대 30초) 내에 리더 브로커의 메세지를 지속적으로 Fetch 해야 한다.

팔로워가 리더에게 Fetch를 요청할때, 다음에 읽을 메세지의 오프셋 번호를 포함한다. 리더는 팔로워가 요청한 오프셋 번호와 현재 리더의 최신 오프셋 번호를 비교해서 팔로워가 얼마나 리더의 데이터를 잘 복제하고 있는지 판단한다.
만약 팔로워가 제대로 따라오지 못하면 ISR에서 제거한다.

### Preferred Leader Election

처음 멀티 브로커를 생성하면 파티션 리더가 할당된다. 이때 설정된 브로커가 Preferred Leader로 정해진다. 카프카는 지속적으로 리더 브로커가 초기의 Preferred Leader로 설정되어 있는지 확인하고,
만약 달라져있다면 재선출하는 과정을 거친다. 이때 default는 5분이다.

### Uncleaned Leader Election

3개의 브로커 A, B, C가 있고 B, C가 죽어서 모든 파티션 리더가 A로 가있는 상황을 가정해보자. 가장 최신의 데이터는 A가 받고 있다. 이때 A가 죽고 B, C가 올라온다면 outDated된 데이터를 가지고 있다.
따라서 리더로 선출되지 않는다. 이 상황에서는 프로듀서가 데이터를 보낼 수 없다. 가장 마지막에 리더였던 브로커가 다시 살아날때까지 기다려야 한다. 
이때 데이터 유실을 감안하더라도 실시간성을 보장하는게 중요하다면 UnCleaned Leader Election을 설정해야 한다.



