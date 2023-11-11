# 02. 설정

## MySQL 서버의 시작과 종료

```bash
# 초기 데이터파일, 로그 파일을 생성하고 비밀번호가 없는 관리자 계정 root 생성
mysqld --defaults-file=/etc/my.cnf --initalize-insecure

# 비밀번호가 있는 root 계정 생성
mysqld --defaults-file=/etc/my.cnf --initalize
```
<br/>

### 서버 시작 종료

MySQL 서버에서는 실제 트랜잭션이 정상적으로 커밋돼도 데이터 파일에 변경된 내용이 기록되지 않고 로그 파일(리두 로그)에만 기록돼 있을 수 있다.  
사용량이 많은 MySQL 서버는 이런 현상이 더 일반적이다.  
MySQL 서버가 종료될 때 모든 커밋된 내용을 데이터 파일에 기록하고 종료하게 할 수도 있다.  
<br/>
```sql
-- MySql 서버가 종료될 때 모든 커밋된 내용을 데이터 파일에 기록하고 종료하게 함
SET GLOBAL Innodb_fast_shutdown=0;

-- 서버 셧다운
SHUTDOWN;
```
<br/>

모든 커밋된 데이터를 데이터 파일에 적용하고 종료하는 것을 클린 셧다운(Clean shutdown)이라고 표현한다.  
클린 셧다운으로 종료되면 다시 MySQL 서버가 가동할 때 별도의 트랜잭션 복고 과정을 진행하지 않기 때문에 빠르게 시작할 수 있다.  

MySQL 서버가 시작되거나 종료될 때는 MySQL 서버(InnoDB 스토리지 엔진)의 버퍼 풀 내용을 백헙하고 복구하는 과정이 내부적으로 실행된다.  
실제 버퍼 풀의 내용을 백업하는 것이 아닌, 버퍼 풀에 적재돼 있던 데이터 파일의 데이터 페이지에 대한 메터장보를 백업하기 때문에 용량이 크지 않으며, 백업 자체는 빠르게 완료된다.  
서버가 새로 시작될 때는 디스크에서 데이터 파일들을 모두 읽어서 적재해야 하므로 상당한 시간이 걸릴 수도 있다.  
<br/>
<br/>

### 서버 연결 테스트

MySQL접속을 시도하는 방법

```bash
mysql -uroot -p --host=localhost --socket=/tmp/mysql.sock

mysql -uroot -p --host=127.0.0.1 --port=3306

mysql -uroot -p
```
<br/>

첫 번째는 MySQL 소켓 파일을 이용해 접속하는 예다.  
<br/>

두 번째는 TCP/IP를 통해 로컬호스트로 접속하는 예로, 일반적으로 포트를 명시한다.  
로컬 서버에 설치된 MySQL이 아니라 원격 호스트에 있는 MySQL 서버에 접속할 때는 이 방법을 사용한다.  
--host=localhost 옵션을 사용하면 MySQL 클라이언트 프로그램은 항상 소켓 파일을 통해 MySQL 서버에 접속하게 되는데, 이는 'Unix domain socket'을 이용하는 방식으로 유닉스 프로세스 간 통신(IPC: Inter Process Communication)의 일종이다.  
127.0.0.1을 사용하는 경우는 자기 서버를 가리키는 루프백(loopback) IP이기는 하지만 TCP/IP 통신 방식을 사용하는 것이다.  
<br/>

세 번째는 별도로 호스트 주소와 포트를 명시하지 않는다.  
기본값으로는 호스트는 localhost가 되며 소켓 파일을 사용하게 되는데, 소켓 파일의 위치는 MySQL 서버의 설정 파일에서 읽어서 사용한다.   
MySQL 서버가 가동될 때 만들어지는 유닉스 소켓 파일은 서버를 재시작하지 않으면 다시 만들어낼 수 없기 때문에 실수로 삭제하지 않도록 주의해야 한다.  
유닉스, 리눅스에서 mysql 클라이언트 프로그램을 실행하는 경우에는 myysql 프로그램의 경로를 PAYH 환경변수에 등록해둔다.  
<br/>

```sql
-- 데이터베이스 목록 확인
SHOW DATABASES;
```
<br/>
<br/>

## MySQL 서버 업그레이드

1. 인플레이스 업그레이드(IN-Place Upgrade) : MySQL 서버의 데이터 파일을 그대로 두고 업그레이드하는 방법
2. 논리적 업그레이드(Logical Upgrade) : mysqldump 도구 등을 이용해 MySQL 서버의 데이터를 SQL 문장이나 텍스트 파일로 덤프한 후, 새로 업그레이드된 버전의 MySQL 서버에서 덤프된 데이터를 적재하는 방법  

인플레이스 업그레이드는 여러 제약사항이 있지만 업그레이드 시간을 크게 단축할 수 있고, 논리적 업그레이드는 제약은 없지만 업그레이드 시간이 오래걸린다.  
<br/>

### MySQL 8.0 업그레이드

**데이터 딕셔너리 업그레이드**  
MySQL 5.7 버전까지는 데이터 딕셔너리 정보가 FRM 확장자를 가진 파일로 별도로 보관됐었는데, MySQL 8.0 버전부터는 데이터 딕셔너리 정보가 트랜잭션이 지원되는 InnoDB 테이블로 저장되도록 개선됐다.  
데이터 딕셔너리 업그레이드는 기존의 FRM 파일(테이블 구조가 저장된 파일)의 내용을 InnoDB 시스템 테이블로 저장한다.  
MySQL 8.0 버전부터는 딕셔너리 데이터의 버전 간 호환성 관리를 위해 테이블이 생성될 때 사용된 MySQL 서버의 버전 정보도 함께 기록한다.  

**서버 업그레이드**  
MySQL 서버의 시스템 데이터베이스(performance_schema와 information_schema, 그리고 mysql 데이터베이스)의 테이블 구조를 MySQL 8.0 버전에 맞게 변경한다.  
<br/>
<br/>

## 서버 설정
MySQL은 단 하나의 설정 파일을 사용한다.  
리눅스를 포함한 유닉스 계열에서는 my.cnf, 윈도우 계열에서는 my.ini 파일을 사용한다.  

MySQL 서버는 시작될 때 이 설정파일을 참조하는데, 경로가 고정돼 있는 것이 아닌, 여러 개의 디렉토리를 순차적으로 탐색하면서 처음 발견된 파일을 사용하게 된다.  
<br/>

### 설정 파일의 구성
```bash
[mysqld_safe]
malloc-lib = /opt/lib/libtcmalloc_minimal.so

# mysql 서버를 위한 설정
[mysqld]
socket = /usr/local/mysql/tmp/mysql.sock
port = 3306

# 클라이언트를 위한 설정파일
[mysql]
default-character-set = utf8mb4
socket = /usr/local/mysql/tmp/mysql.sock
port = 3304

# 백업을 위한 설정
[mysqldump]
default-character0set = utf8mb4
socket = /usr/local/mysql/tmp/mysql.sock
port = 3305
```
<br/>

### 시스템 변수의 특징
설정 파일을 읽어 메모리나 작동 방식을 초기화하고 접속된 사용자를 제어하기 위해 이 값들을 별도로 저장해둔 것을 시스템 변수(System Variables)라고 한다.  
SHOW GLOBAL VARIABLES 명령어로 확인할 수 있다.  

시스템 변수(설정) 값이 어떻게 MySQL 서버와 클라이언트에 영향을 미치는지 판단하려면 각 변수가 글로벌 변수인지 세션 변수인지 구분할 수 있어야 한다.  

#### 시스템 변수가 가지는 5가지 속성의 의미
- Cmd-Line: MySQL 서버의 명령행 인자로 설정될 수 있는지 여부를 나타낸다. 즉, 이 값이 Yes면 명령행 인자로 이 시스템 변수의 값을 변경하는 것이 가능하다는 의미다.
- Option file: MySQL 설정 파일인 my.cnf(my.ini)로 제어할 수 있는지 여부를 나타낸다. 옵션 파일이나 설정 파일 또는 configuration 파일 등은 전부 my.cnf(my.ini)파일을 지칭하는 것으로 같은 의미로 사용된다.
- System Var: 시스템 변수인지 아닌지를 나타낸다. MySQL 서버의 설정 파일을 작성할 때 각 변수명에 사용된 하이픈(-)이나 언더스코어(\_)의 구분에 주의해야 한다. 이는 MySQL 서버가 예전부터 수많은 사람들의 손을 거쳐오면서 생긴 일관성 없는 변수의 명명 규칙 때문이다. 어떤 변수는 하이픈으로 구분되고 어떤 시스템 변수는 언더 스코어로 구분되는 등 상당히 애매모호한 부분이 있는데, 뒤늦게 이런 부분을 언더스코어로 통일해가는 중이다. 현재 MySQL 8.0에서는 모든 시스템 변수들이 '_'를 구분자로 사용호도록 변경된 것으로 보인다. 그리고 명령행 옵션으로만 사용 가능한 설정들은 '_'가 아니라 '-'을 구분자로 사용한다. 
- Var Scope: 시스템 변수의 적용 범위를 나타낸다. 이 시스템 변수가 영향을 미치는 곳이 MySQK 서버 전체(Global)를 대상으로 하는지, 아니면 MySQL 서버와 클라이언트 간의 커넥션(Session, Connection)만인지 구분한다. 그리고 어떤 변수는 세션과 글러벌 범위에 모두 적용(Both)되기도 한다. 
- Dynamic: 시스템 변수가 동적인지 정적인지 구분하는 변수이다.  

<br/>

### 글로벌 변수와 세션 변수
시스템 변수는 적용 범위에 따라 글로벌 변수와 세션 변수로 나뉘는데, 일반적으로 세션별로 적용되는 시스템 변수의 경우 글로벌 변수뿐만 아니라 세션 변수에도 동시에 존재한다.  

- 글로벌 범위의 시스템 변수는 하나의 MySQL 서버 인스턴스에서 전체적으로 영향을 미치는 시스템 변수를 의미하며, 주로 MySQL 서버 자체에 관련된 설정일 떄가 많다. 서버에서 단 하나만 존재하는 InnoDB 버퍼 풀 크기(innodb_buffer_pool_size) 또는 MyISAM의 키 캐시 크기(key_buffer_size) 등이 가장 대표적인 글로벌 영역의 시스템 변수다. 
- 세션 범위의 시스템 변수는 MySQL 클라이언트가 MySQL 서버에 접속할 때 기본으로 부여하는 옵션의 기본값을 제어하는 데 사용된다. 다른 DBMS에서도 거의 비슷하겠지만 MySQL 에서도 각 클라이언트가 처음에 접속하면 기본적으로 부여하는 기본값을 가지고 있다. 별도로 그 값을 변경하지 않은 경우에는 그대로 값이 유지되지만, 클라이언트의 필요에 따라 개별 커넥션 단위로 다른 값으로 변경할 수 있는 것이 세션 변수다. 여기서 기본값은 글로벌 시스템 변수이며, 각 클라이언트가 가지는 값이 세션 시스템 변수다. 각 클라이언트에서 쿼리 단위로 자동 커밋을 수행할지 여부를 결정하는 autocommit 변수가 대표적인 예라고 볼수 있다. autocommit 변수의 값을 ON으로 설정해두면 해당 서버에 접속하는 모든 커넥션은 기본으로 자동 커밋 모드로 시작되지만 OFF로 변경해 자동 커밋 모드를 비활성화할 수도 있다. 이러한 세션 변수는 커넥션 별로 설정값을 서로 다르게 지정할 수 있으며, 한번 연결된 커넥션의 세션 변수는 서버에서 강제로 변경할 수 없다. 
- 세션 범위의 시스템 변수 가운데 MySQL 서버의 설정파일에 명시해 초기화할 수 있는 변수는 대부분의 범위가 'Both'라고 명시돼 있다. 이렇게 'Both'로 명시된 시스템 변수는 MySQL 서버가 기억만 하고 있다가 실제 클라이언트와의 커넥션이 생성되는 순간에 해당 커넥션의 기본값으로 사용되는 값이다. 그리고 순수하게 범위가 세션이라고 명시된 시스템 변수는 MySQL 서버의 설정 파일에 초기값을 명시할 수 업승며, 커넥션이 만들어지는 순간부터 해당 커넥션에서만 유요한 설정 변수를 의미한다.  
<br/>

### 정적 변수와 동적 변수
시스템 변수는 MySQL 서버가 기동중인 상태에서 변경 가능한지에 따라 동적 변수와 정적 변수로 구분된다.  
서버의 시스템 변수는 디스크에 저장돼 있는 설정파일(my.cnf)을 변경하는 경우와 이미 기동 중인 MySQL 서버의 메모리에 있는 서버의 시스템 변수를 변경하는 경우로 구분할 수 있다.  
디스크에 저장된 설정 파일의 내용을 변경해도 서버 재시작 전에는 적용되지 않는다.  
하지만 SHOW 명령어로 변수값을 확인하거나 SET 명령을 이용해 값을 바꿀 수도 있다.

```sql
SHOW GLOBAL VARIABLES LIKE '%max_connections%';
SET GLOBAL max_connections = 500;
SHOW GLOBAL VARIABLES LIKE '%max_connections%';
```
<br/>

SET 변수는 설정 파일에 반영되는 것이 아니기 때문에 현재 기동 중인 MySQL 인스턴스에서만 유효하다.  
영구 적용을 위해서는 설정파일의 내용도 변경해야 한다.  

SHOW나 SET 명령어에 GLOBAL 키워드를 사용하면 글로벌 시스템 변수의 목록과 내용을 변경할 수 있으며, GLOBAL을 빼면 세션 변수를 조회하고 변경한다.  

동적으로 시스템 변수값을 변경하는 경우 SET 명령으로는 설장 파일에는 내용이 기록되지 않으며, 설정 파일까지 내용을 변경하려 한다면 SET PERSIST 명령을 사용해야 한다.  
SET PERSIST 명령을 사용하는 경우 변경된 시스템 변수는 my.cnf 파일이 아닌 별도의 파일에 기록된다.  

시스템 변수 범위가 'Both'인 경우 글로벌 시스템 변수의 값을 변경해도 이미 존재하는 커넥션의 세션 변수값은 변경되지 않고 그대로 유지된다.  
join_buffer_size라는 Both 타입의 변수로 한번 확인해보자 

```sql
SHOW GLOBAL VARIABLES LIKE 'join_buffer_size';
SHOW VARIABLES LIKE 'join_buffer_size';
SET GLOBAL join_buffer_size = 524288;
SHOW GLOBAL VARIABLES LIKE 'join_buffer_size';
SHOW VARIABLES LIKE 'join_buffer_size';
```
<br/>

### SET PERSIST
서버의 시스템 변수는 정적, 동적 변수로 구분되는데 동적 변수의 경우 MySQL 서버에서 SET GLOBAL 명령으로 변경하면 즉시 서버에 반영된다.  
예를 들어 max_connections 시스템 변수는 동적 변수로, 서버에서 커넥션을 많이 사용중일 때 커넥션의 개수를 늘리기 위해서 변수를 변경하게 될 것이다.  
이후 설정 파일에도 적용해야 하는데, 다른 일을 하다보면 설정 파일에 변경 내용을 반영하는 것을 잊을 수도 있다.  
이러한 문제점을 보완하기 위해서 SET PERSIST 명령을 도입했다.  

```sql
SET PERSIST max_connections = 200;
SHOW GLOBAL VARIABLES LIKE 'max_connections';
```
<br/>

위와 같이 명령어를 작성하면 서버는 변경된 값을 즉시 적용함과 동시에 별도의 설정 파일(mysqld-quto.cnf)에 변경 내용을 추가로 기록해두고 서버가 재시작될 때 기본 설정파일과 별도 설정 파일을 같이 참조해서 시스템 변수를 적용한다.  

SET PERSIST 명령은 세션 변수에는 적용되지 않으며, SET PERSIST 명령으로 시스템 변수를 변경하면 서버는 자동으로 GLOBAL 시스템 변수의 변경으로 인식하고 변경한다.  
서버에서는 변경 내용을 적용하지 않고 별도 설정 파일에만 변경 내용을 기록해두고 다음 재시작에 적용하려면 SET PERSIST_ONLY 명령을 사용하면 된다.  

```sql
SET PERSIST_ONLY max_connections = 200;
SHOW GLOBAL VARIABLES LIKE 'max_connections';
```
<br/>

SET PERSIST_ONLY 명령은 정적 변수의 값을 영구적으로 변경하고자 할 때도 사용할 수 있다.  
SET PERSIST 명령어는 서버의 동적 변수들의 값을 변경함과 동시에 mysqld-auto.cnf 파일에도 기록하는 용도인데, 정적 변수는 실행중인 MySQL 서버에서 변경할 수 없다. 대표적으로 innodb_doublewrite는 정적 변수로, 서버가 재시작될 때만 변경될 수 있다.  
이 경우처럼 정적 변수를 mysqld-auto.cnf 파일에 기록하고자 할 때 SET PERSIST_ONLY 명령을 활용하면 된다.  

```sql
SET PERSIST innodb_doublewrite=ON;
-- SQL Error [1238] [HY000]: Variable 'innodb_doublewrite' is a non persistent variable

SET PERSIST_ONLY innodb_doublewrite=ON;
```
SET PERSIST_ONLY 명령으로 시스템 변수를 변경하면 JSON 포맷의 mysqld-auto.cnf 파일이 생성된다.  

SET PERSIST, SET PERSIST_ONLY 명령으로 변경된 시스템 변수의 메타 데이터는 performance_schema.variables_info와 performance_schema.persisted_variables 테이블을 통해 참조할 수 있다.  

```sql
SELECT a.variable_name, b.variable_value, a.set_time, a.set_user, a.set_host
FROM performance_schema.variables_info a
JOIN performance_schema.persisted_variables b
ON a.VARIABLE_NAME = b.VARIABLE_NAME 
WHERE b.variable_name LIKE 'max_connections';
```
<br/>

SET PERSIST나 SET PERSIST_ONLY 명령으로 추가된 시스템 변수의 내용을 삭제해야 할 때는 RESET PERSIST 명령을 사용하는 것이 설정 파일을 직접 건드리는 것보다 안전하다.  
```sql
RESET PERSIST max_connections;
RESET PERSIST IF EXISTS max_connections;

-- 모든 시스템 변수를 삭제
RESET PERSIST;
```

