# 카프카 CLI 명령어 정리

아파치 카프카 CLI(Command Line Interface) 명령어를 정리한다.  

아파치 카프카의 스크립트 파일은 카프카를 설치하고 압축해제를 했을 때 `bin` 디렉토리에 스크립트 파일이 존재한다.  

<br/>

## 카프카 CLI

카프카 CLI는 카프카 클러스터와 상호작용하며 다양한 명령어들을 제공하는데, 애플리케이션을 개발하거나 클러스터를 운영할 때 자주쓰이므로 알아두는 것이 좋다.  

카프카 CLI 명령어를 통해 토픽 관련 명령어를 실행할 떄 필수 옵션과 선택 옵션이 있다. 선택 옵션은 지정하지 않을 시  브로커에 설정된 기본 설정값 또는 커맨드 라인 툴의 기본값으로 대체되어 설정된다.

카프카 쉘 스크립트 파일을 사용해서 명령어를 실행하기 전 주키퍼와 카프카를 시작한다.   
```bash
# 주키퍼 서버 시작
bin/zookeeper-server-start.sh conflg/zookeeper.properties

# 카프카 브로커 시작  
bin/kafka-server-start.sh conflg/server.properties
```

<br/>

### kafka-topics.sh

`kafka-topics.sh` 파일은 카프카 토픽을 생성, 삭제하거나 토픽의 상세 설명을 볼 수 있다. 특정 토픽의 설정을 변경하는 것도 가능하다.  

<br/>

#### 카프카 토픽 생성
```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--topic hello-topic
```
- `--bootstrap-server` 옵션은 토픽을 생성할 카프카 클러스터를 구성하는 브로커들의 `[SERVER IP]:[PORT]`를 적는다. 브로커 서버가 여러 개 일 때는 콤마(,)로 구분한다.
- `--create` 옵션은 토픽을 생성한다는 것을 명시한다.
- `--topic` 옵션은 생성할 토픽의 이름을 작성한다. 토픽명은 내부 데이터가 무엇이 있는지 유추가 가능할 정도로 자세히 적는 것을 추천한다.

<br/>

```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--partitions 3 \
--replication-factor 1 \
--config retention.ms=172800000 \
--topic hello-topic

# 리플리케이션 팩터는 브로커의 개수보다 클 수 없다.
# --replication-factor 3 은 원본과 복제 파티션을 모두 포함해서 3개라는 뜻이다.
kafka-topics --bootstrap-server localhost:9092 \
--create \
--topic multi-broker-hello-topic \
--partitions 3 \
--replication-factor 3
```
- `--partitions` 옵션은 토픽 내의 파티션 개수를 지정한다. 지정하지 않으면 server.properties에서 설정한 `num.partitions` 값으로 1개가 생성된다.
- `--replication-factor` 옵션은 토픽의 파티션을 복제할 개수를 지정한다. 1은 복제를 하지않고 사용한다. 복제 수를 지정한 만큼 파티션의 데이터는 각 브로커마다 저장된다. 하나의 브로커가 장애가 발생해도 다른 브로커에서 복제된 파티션으로 처리를 할 수 있다. 복제의 최대 개수는 브로커 서버의 수 만큼 가능하다. 이 설정을 명시적으로 지정하지 않으면 `default.replication.factor` 옵션에 따라 생성된다. 
- `--config` 옵션으로 `kafka-topics.sh` 명령에 포함되지 않은 추가적인 설정을 할 수 있다.

<br/>

#### 토픽 목록 조회
```bash
./kafka-topics.sh --bootstrap-server localhost:9092 \
--list
```
- `--list` 옵션으로 지정한 브로커에 해당하는 토픽 목록을 조회한다.

<br/>

#### 토픽 상세 조회
```bash
./kafka-topics.sh \
--bootstrap-server localhost:9092 \
--describe \
--topic hello-topic
```
- 파티션의 개수, 복제된 파티션이 위치한 브로커의 번호, 기타 토픽을 구성하는 설정들을 출력한다. 토픽이 가진 파티션의 리더가 현재 어떤 브로커에 존재하는지도 확인할 수 있다.
- 리더 파티션이 일부 브로커에 몰려있는 경우 카프카 클러스터 부하가 특정 브로커들로 몰릴 수 있다. 부하가 분산되지 못하면 데이터 쏠림으로 네트워크 대역의 이슈가 생길 수 있다.  

<br/>

#### 토픽의 파티션 수 수정
```bash
./kafka-topics.sh --bootstrap-server localhost:9092 \
--topic hello-topic \
--alter \
--partitions 3
```
- `--alter` 와 `--partitions` 옵션을 함께 사용해서 파티션 개수를 변경할 수 있다.

<br/>
<br/>

### kafka-configs.sh

`kafka-configs.sh` 파일은 브로커, 토픽 등에 설정된 프로퍼티(옵션)를 관리하는 데 사용된다. 설정을 추가, 삭제, 변경할 수 있다.

<br/>

#### 카프카 브로커 설정 확인
```bash
# 전체 설정 확인
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type brokers \
--entity-name 0 \
--describe --all

# 파이프로 필요한 부분만 확인
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type brokers \
--entity-name 0 \
--describe --all | grep segment
```
- `--entity-type` 옵션은 엔티티를 지정하는데, brokers, topics, users를 지정 가능하다.
- `--entity-name` 옵션은 엔티티 명을 지정하며, 브로커는 브로커의 ID, 토픽은 토픽명, users는 사용자명을 작성한다.  
- `--describe --all` 옵션은 전체 프로퍼티를 확인한다. `--describe` 옵션만 사용하면 동적으로 변경한 프로퍼티만 출력한다.
- `--describe --all` 옵션으로 전체 프로퍼티를 확인할 때 `grep` 을 통해 원하는 프로퍼티만 출력할 수 있다.  

<br/>

#### 토픽 설정 확인
```bash
# 전체 설정 확인
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type topics \
--entity-name hello-topic \
--describe --all

# 파이프로 필요한 부분만 확인
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type topics \
--entity-name hello-topic \
--describe --all | grep retention

# 동적으로 변경된 옵션 확인
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type topics \
--entity-name hello-topic \
--describe
```

<br/>

#### 토픽 옵션 동적 변경
```bash
# 동적 옵션 변경
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type topics \
--entity-name hello-topic \
--alter --add-config retention.ms=86400000

# 동적으로 변경된 옵션 확인
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type topics \
--entity-name hello-topic \
--describe
```
- `--entity-type` 옵션을 토픽으로 지정했기 때문에 `--entity-name` 옵션에는 토픽명을 작성한다.  
- `--alter` 옵션과 `--add-config` 옵션을 같이 사용해서 프로퍼티를 동적으로 변경할 수 있다.

<br/>

#### 동적으로 변경한 토픽 옵션 지우기(되돌리기)
```bash
./kafka-configs.sh --bootstrap-server localhost:9092 \
--entity-type topics \
--entity-name hello-topic \
--alter --delete-config retention.ms
```
- `--alter` 옵션과 `--delete-config` 옵션을 같이 사용해서 동적으로 변경한 옵션을 원래 값으로 되돌릴 수 있다.  

<br/>

### kafka-console-producer.sh

`kafka-console-producer.sh` 파일은 카프카 클러스터에 메시지를 전송하는 프로듀서를 실행하는 데 사용되며, 토픽에 데이터(레코드)를 전송할 수 있다. 토픽에 전송하는 메시지는 키(key), 값(value)로 이루어져 있다. 키를 지정하지 않으면 null값으로 저장된다.  

<br/>

#### 프로듀서로 사용한 토픽에 데이터 전송
```bash
# 값만 전송하며, 키는 null로 전송된다.
./kafka-console-producer.sh --bootstrap-server localhost:9092 \
--topic hello-topic
```

<br/>

#### 프로듀서로 토픽에 키와 값을 갖는 전송
```bash
./kafka-console-producer.sh --bootstrap-server localhost:9092 \
--topic hello-topic \
--property parse.key=true \
--property key.separator=:

# 값 입력
key01:value01
key02:value02
key03:value03
```
- `parse.key` 를 `true` 로 하면 레코드를 전송할 때 메시지 키를 추가할 수 있다.
- 메시지의 `key.separator` 를 지정하지 않으면 기본 구분자(Delimeter)는 탭 `\t` 다. 메시지 키를 입력하고 탭 키를 누르고 값을 입력해야 한다.
- 키와 값을 함께 전송한 레코드는 토픽의 파티션에 저장된다. 메시지 키가 null 인 경우에는 프로듀서가 파티션으로 전송할 때 레코드 배치 단위(레코드 전송 묶음)로 라운드 로빈으로 전송한다.
- 파티션에 동일한 키가 존재하는 경우 키의 해시값을 작성해여 존재하는 파티션 중 한 개의 파티션으로 전송되므로 키가 동일하면 동일한 파티션으로 전송된다.

<br/>

### kafka-console-consumer.sh

`kafka-console-consumer.sh` 파일은 카프카 토픽에서 메시지를 읽어들이는 컨슈머를 실행하는 데 사용된다.

#### 컨슈머로 토픽의 데이터 읽기(value만 읽기)
```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic hello-topic \
--from-beginning
```
- `--from-beginning` 옵션을 주면 토픽에 저장된 가장 처음 데이터부터 출력한다.

<br/>

#### 컨슈머로 토픽의 데이터 읽기(key, value 모두 읽기)
```bash
./kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic hello-topic \
--property print.key=true \
--property key.separator=: \
--group hello-group \
--from-beginning

# 여기서 컨슈머 그룹을 생성했는데, 파티션을 여러 개 갖는 토픽에서 터미널을 여러 개를 띄운다.
# 위 명령어로 같은 컨슈머 그룹을 갖는 쉘을 실행하면 카프카 서버의 로그에서 그룹 코디네이터에 의한 리벨런싱이 일어나는 것을 확인할 수 있다.  
```
- `print.key` 옵션을 `true` 로 설정해서 메시지 키를 확인할 수 있다. 기본값은 `false` 로 키가 출력되지 않는다.
- `key.separator` 를 설정하지 않으면 기본값은 탭으로 출력된다.
- `--group` 옵션을 통해 컨슈머 그룹(Consumer Group)을 생성하며, 컨슈머 그룹에서 가져간 토픽 메시지는 커밋한다. 커밋은 컨슈머가 특정 레코드까지 처리를 완료했다고 레코드의 오프셋 번호를 카프카 브로커에 저장하는 것이다. 커밋 정보는 카프카 로그 디렉토리 경로에 `__consumer_offsets` 이름으로 저장된다.

<br/>

#### 오프셋 읽기

`__consumer_offsets` 으로 시작되는 디렉토리는 카프카가 내부적으로 사용하는 토픽으로, 오프셋 정보를 가지고 있다.  

```bash
# grep 명령어를 사용하여 특정 토픽에 대한 정보를 필터링할 수 있다
kafka-console-consumer.sh --bootstrap-server <카프카_브로커_주소>:<포트> \
--topic __consumer_offsets \
--formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" \
--from-beginning \ | grep [토픽명]

# grep 명령어를 사용하여 특정 토픽에 대한 정보를 필터링할 수 있다
kafka-console-consumer.sh --bootstrap-server <카프카_브로커_주소>:<포트> \
--topic __consumer_offsets \
--formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" \
--from-beginning 
--consumer.config <설정_파일> | grep [토픽명]
```
- `--formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter"`: `__consumer_offsets` 토픽의 메시지를 해석하기 위해 포맷터를 지정한다. 이 포맷터는 오프셋 메시지를 사람이 읽을 수 있는 형식으로 변환한다.
- `--consumer.config <설정_파일>`: 추가적인 컨슈머 설정이 들어 있는 파일을 지정한다. `exclude.internal.topics` 프로퍼티 처럼 내부 토픽을 허용 여부를 설정한 프로퍼티 파일을 생성해서 지정할 수 있다.  

<br/>

### kafka-consumer-groups.sh

`kafka-consumer-groups.sh` 파일은 컨슈머 그룹을 관리하는 도구이다. 컨슈머 그룹의 오프셋을 확인, 이동, 리셋할 수 있다.  

컨슈머 그룹의 상세 정보를 확인하는 것은 컨슈머를 개발할 때, 카프카를 운영할 때 둘 다 중요하게 활용된다. 컨슈머 그룹이 중복되지 않는지 확인하거나 운영하고 있는 컨슈머가 LAG이 얼마인지 확인해서 컨슈머의 상태를 최적화하는 데 사용된다. 카프카를 운영할 때 컨슈머 그룹의 상세 정보를 통해 카프카에 연결된 컨슈머의 호스트명 또는 IP를 알아내서 접근 중인 컨슈머의 정보를 토대로 카프카가 인가된 사람에게만 사용 중인지 알 수 있다.

<br/>

#### 컨슈머 그룹 확인
```bash
# 그룹 전체 목록 확인
kafka-consumer-groups --bootstrap-server localhost:9092 --list

# 특정 그룹 확인
kafka-consumer-groups --bootstrap-server localhost:9092 --list hello-group
```

<br/>

#### 컨슈머 그룹 상세 확인
```bash
./kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
--group hello-group \
--describe

# 결과
GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
hello-group     hello-topic     0          1               3               2               -               -               -
hello-group     hello-topic     1          2               3               1               -               -               -
hello-group     hello-topic     2          2               2               0               -               -               -
```
- `--group` 옵션으로 어떤 그룹의 상세 내용을 확인할 것인지 지정한다.
- `--describe` 옵션으로 컨슈머 그룹의 상세 내용을 확인할 수 있다.
- GROUP, TOPIC, PARTITION은 조회한 컨슈머 그룹이 마지막으로 커밋한 토픽과 파티션을 나타낸다.
- CURRENT_OFFSET은 컨슈머 그룹이 가져간 토픽의 파티션에 가장 최신 오프셋이 몇 번인지 나타낸다.
- LOG-END-OFFSET은 해당 컨슈머 그룹의 컨슈머가 어느 오프셋까지 커밋했는지 알 수 있다. CURRENT-OFFSET은 LOG-END-OFFSET 보다 같거나 작은 값일 수 있다.
- LAG는 컨슈머 그룹이 토픽의 파티션에 있는 데이터를 가져가는 데 얼마나 지연이 발생하는지 나타내는 지표이다. 랙은 컨슈머 그룹이 커밋한 오프셋과 해당 파티션의 가장 최신 오프셋간의 차이다.
- CONSUMER-ID는 컨슈머의 토픽(파티션) 할당을 카프카 내부적으로 구분하기 위해서 사용하는 유니크한 ID값이다.
- HOST는 컨슈머가 동작하는 host 명을 출력한다. 이 값으로 카프카에 붙은 컨슈머의 호스트 명 또는 IP를 알 수 있다.
- CLIENT-ID는 클라이언트에 할당된 ID로 사용자가 지정할 수 있고, 지정하지 않으면 자동 생성된다.

<br/>

#### 컨슈머 그룹 제거(사용하지 않는 그룹은 잘 제거해주는 것이 좋음)

```bash
# Consumer 그룹은 기본적으로 모든 Consumer가 사용중이지 않으면 일정 기간(기본 7일) 뒤에 삭제된다.
# 강제로 삭제할 때는 활성화된 Consumer가 없어야 한다.
kafka-consumer-groups --bootstrap-server localhost:9092 --delete --group hello-group
```

<br/>

### kafka-verifiable-producer, consumer.sh

`kafka-verifiable-producer.sh, kafka-verifiable-consumer.sh` 두 스크립트를 사용하면 String 타입 메시지 값을 코드 없이 주고받을 수 있다. 카프카 클러스터 설치가 완료된 이후에 토픽에 데이터를 전송하여 간단한 네트워크 통신 테스트를 할 때 유용하다.

<br/>

#### 데이터 전송
```bash
./kafka-verifiable-producer.sh --bootstrap-server localhost:9092 \
--max-messages 10 \
--topic verify-test
```
- `--max-messages` 는 `kafka-verifiable-producer.sh` 로 보내는 데이터 개수를 지정한다. -1로 지정하면 쉘이 종료될 때까지 데이터를 토픽으로 보낸다.
- 최초 실행 시점이 `startup_complete` 와 함께 출력된다. 메시지 별로 보낸 시간과 키, 값, 토픽, 저장된 파티션, 저장된 오프셋 번호가 출력된다. 데이터가 모두 전송된 이후 통계값이 출력되고 평균 처리량을 알 수 있다.  

<br/>

#### 전송한 데이터 확인
```bash
./kafka-verifiable-consumer.sh --bootstrap-server localhost:9092 \
-- topic verify-test \
--group-id test-group
```

<br/>

### kafka-delete-records.sh
`kafka-delete-records.sh` 로 이미 적재된 토픽의 데이터를 지울 수 있다.  

`kafka-delete-records.sh` 스크립트는 이미 적재된 토픽의 데이터 중 가장 오래된 데이터(가장 낮은 오프셋)부터 특정 시점의 오프셋까지 삭제할 수 있다.  

```bash
# 토픽이 1~100까지 있을 때 50번까지 삭제

# 삭제 토픽, 파티션, 오프셋 정보를 담은 json 파일 생성
vim delete-topic.json
{"partitions": [{"topic": "test", "partition": 0, "offset": 50}], "version": 1}

./kafka-delete-records.sh --bootstrap-server localhost:9092 \
--offset-json-file delete-topic.json
```
- 삭제하고자 하는 토픽, 파티션, 오프셋 정보를 담은 json 파일을 생성한다.
- `--offset-json-file` 옵션으로 생성한 파일을 지정해주면 된다.
- 주의해야 하는 점은 토픽의 특정 레코드 하나만 삭제되는 것이 아니라 파티션에 존재하는 가장 오래된 오프셋부터 지정한 오프셋까지 삭제된다는 점이다. 카프카에서는 토픽의 파티션에 저장된 특정 데이터만 삭제할 수 없다.  

<br/>
<br/>