# 6. 기본 DML 튜닝

### DML 성능에 영향을 미치는 요소

- 인덱스
- 무결성 제약
- 조건절
- 서브쿼리
- Redo 로깅
- Undo 로깅
- Lock
- 커밋  
<br/>

### **인덱스와 DML 성능**

**INSERT**  

테이블에 레코드를 입력하면, 인덱스에도 입력해야 한다.  

테이블은 Freelist(테이블마다 입력이 가능한(여유 공간이 있는) 블록 목록을 관리하는 것)를 통해 입력할 블록을 할당받지만, 인덱스는 정렬된 자료구조이므로 수직적 탐색을 통해 입력할 블록을 찾아야 한다.  

인덱스에 입력하는 과정이 더 복잡하므로 DML 성능에 미치는 영향도 더 크다.  
<br/>

![Untitled](https://github.com/Reffy08/TIL/assets/95058915/0e17e13e-1d18-4d35-86f0-b8b9fe51343b)  
<br/>

**DELETE**  

테이블에서 레코드 하나를 삭제하면, 인덱스 레코드를 모두 찾아서 삭제해줘야 한다.  
<br/>

**UPDATE**  

변경된 컬럼을 참조하는 인덱스만 찾아서 변경해주면 된다.  

대신 인덱스는 정렬된 자료구조이기 때문에, A를 K로 변경하면 저장 위치도 달라지므로 삭제후 삽입하는 두 개의 오퍼레이션이 발생한다.  
<br/>

![Untitled (1)](https://github.com/Reffy08/TIL/assets/95058915/69bb5e40-1107-48fb-ad07-7688486374f7)  
<br/>

인덱스 개수가 DML 성능에 미치는 영향이 매우 큰 만큼 설계에 유의해야 한다.  

핵심 트랜잭션 테이블에서 인덱스를 하나라도 줄이면 TPS(Transaction Per Second)는 그만큼 향상된다.  
<br/>
<br/>

### 무결성 제약과 DML 성능

개체 무결성, 참조 무결성, 도메인 무결성, 사용자 정의 무결성(비즈니스 무결성) 및 제약조건을 설정하면 데이터 무결성을 지킬 수 있다.  

PK, FK 제약조건은 실제 데이터를 확인해야 되므로, Check, Not Null 등의 조건환 준수하는지 확인하는 제약보다 큰 영향을 미친다.
<br/>
<br/>

### 조건절, 서브쿼리와 DML 성능

Update 문이나 Delete 문에서 적절한 인덱스는 쿼리을 연산속도를 향상시킨다.
<br/>
<br/>

### Redo로깅과 DML 성능

오라클은 데이터 파일과 컨트롤 파일에 가해지는 모든 변경사항을  Redo 로그에 기록한다.   

Redo 로그는 트랜잭션 데이터가 어떤 이유에서건 유실됐을 때, 트랜잭션을 재현함으로써 유실 이전 상태로 복구하는데 사용된다.  

DML 문을 수행할 때마다 Redo 로그를 생성해야 하므로 Redo 로깅은 DML 성능에 영향을 미친다.  
<br/>
<br/>

**Redo 로그의 용도**  

- DataBase Recovery : Redo 로그는 물리적으로 디스크가 깨지는 등의 Media Fail 발생 시 데이터베이스를 복구하기 위해 사용
- Cache Recovery : 트랜잭션 데이터 유실을 대비하기 위해 Redo 로그를 남긴다.
- Fast Commit : 변경된 메모리 버퍼블록을 디스크 상에 반영하는 것보다, 로그는 Append 방식으로 빠르므로 트랜잭션 변경사항을 로그파일에 기록하고 데이터 파일 간 동기화는 배치 방식으로 일괄 처리한다.  
<br/>
<br/>

### Undo 로깅과 DML 성능

**Redo**  

- 트랜잭션을 재현함으로써 과거를 현재 상태로 되돌리는데 사용
- 트랜잭션을 재현하는 데 필요한 정보를 로깅  
<br/>

**Undo**  

- 트랜잭션을 롤백함으로써 현재를 과거 상태로 되돌리는데 사용
- 변경된 블록을 이전 상태로 되돌리는 데 필요한 정보를 로깅  
<br/>

DML 수행마다 Undo를 생성해야 하므로 Undo 로깅은 DML 성능에 영향을 준다.  

**Redo 로그의 용도**  

- Transaction Rollback : 트랜잭션에 의한 변경사항을 최종 커밋하지 않고 롤백하고자 할 때 사용
- Transaction Recovery : 시스템 Shutdown 시점에 아직 커밋되지 않은 트랜잭션을 모두 롤백해야 할 때 사용
- Read Consistency : 읽기 일관성을 위해 사용  
<br/>
<br/>

### Lock과 DML 성능

Lock을 필요 이상으로 자주 길게 사용하거나 레벨을 높일수록 DML 성능은 느려진다.(Transaction 격리 수준)  

그렇다고 Lock을 너무 적게, 짧게 사용하거나 레벨을 낮추면 데이터 품질이 나빠진다.  

두 가지는 트레이드 오프 관계이므로 세심한 동시성 제어를 요구한다.  
<br/>
<br/>

### 커밋과 DML 성능

DML을 완료할 수 있게 Lock을 푸는 열쇠가 커밋이기 때문에, DML 성능과 연관이 깊다.  

모든 DBMS는 Fast Commit을 구현한다. 이로 인해 커밋을 순차적으로 처리하긴 하지만, 결코 가벼운 작업이 아니다.  
<br/>
<br/>

### **커밋 내부 매커니즘**

**(1) DB 버퍼캐시**  

DB에 접속한 사용자를 대신해 모든 일을 처리하는 서버 프로세스는 버퍼캐시를 통해 데이터를 읽고 쓴다. 버퍼캐시에서 변경된 블록(Dirty 블록)을 모아 주기적으로 데이터 파일에 일괄 기록하는 작업은 DBWR(Database Writer) 프로세스가 맡는다
<br/>

**(2) Redo 로그버퍼**  

버퍼캐시는 휘발성이므로 Dirty블록을 데이터파일에 반영할 때까지 불안한 상태인데, 버퍼캐시가 가한 변경사항을 Redo 로그에도 기록하고, 버퍼캐시 데이터가 유실되더라도 Redo로그를 이용해 복구할 수 있다.  

Redo 로그도 파일이므로 Append방식을 사용해도 디스크 I/O는 느리다.  

이를 해결하기 위해서 로그버퍼를 이용한다. Redo 로그 파일에 기록하기 전에 먼저 로그 버퍼에 기록하는 방식이다. 기록된 내용은 LGWR(Log Writer) 프로세스가 Redo 로그 파일에 일괄 기록한다.  
<br/>

**(3) 트랜잭션 데이터 저장 과정**  

![Untitled (2)](https://github.com/Reffy08/TIL/assets/95058915/c5080f93-55cc-4235-a2b7-1b9ed820946c)
<br/>

1. DML 문을 실행하면 Redo 로그버퍼에 변경사항을 기록한다.
2. 버퍼블록에서 데이터를 변경(레코드 CRUD) 한다. 물론, 버퍼캐시에서 블록을 찾지 못하면, 데이터파일에서 읽는 작업부터 한다.
3. 커밋한다.
4. LGWR 프로세스가 Redo 로그버퍼 내용을 로그파일에 일괄 저장한다.
5. DBWR 프로세스가 변경된 버퍼블록들은 데이터파일에 일괄 저장한다.  
<br/>

오라클은 데이터를 변경하기 전에 항상 로그부터 기록한다.  

메모리 버퍼캐시가 휘발성이여서 Redo로그를 남기는데, Redo 로그마저 휘발성 로그버퍼에 기록하면 트랜잭션 데이터를 안전하게 지킬 수 없다.  

이 문제를 해결하기 위해서 DBWR과 LGWR 프로세스는 주기적으로 깨어나 각각 Dirty 블록과 Redo 로그버퍼를 파일에 기록한다.  
<br/>

**(4) 커밋 = 저장버튼**  

커밋은 서버 프로세스가 그때까지 했던 작업을 디스크에 기록하라는 명령어이다.  

저장을 완료할 때까지 서버 프로세스는 다음 작업을 진행할 수 없다. Redo 로그버퍼에 기록된 내용을 디스크에 기록하도록 LGWR 프로세스에 신호를 보낸 후 작업을 완료했다는 신호를 받아야 다음 작업을 진행할 수 있다. (Sync 방식)  

LGWR 프로세스가 Redo 로그를 기록하는 작업은 디스크 I/O 작업이므로, 커밋은 생각보다 느리다.  

트랜잭션을 논리적으로 잘 정의함으로써 불필요한 커밋이 발생하지 않도록 구현해야 한다.  
<br/>
<br/>

### 데이터베이스 Call과 성능

**데이터베이스 Call**  

SQL이 진행되는 세 단계  

- Parse Call : SQL 파싱과 최적화를 수행하는 단계다. SQL과 실행꼐획을 라이브러리 캐시에서 찾으면 최적화 단계는 생략할 수 있다.
- Execute Call : SQL을 실행하는 단계다. DML은 이 단계에서 모든 과정이 끝나지만, SELECT 문은 Fetch 단계를 거친다.
- Fetch Call : 데이터를 읽어서 사용자에게 결과집합을 전송하는 과정으로 SELECT 문에서만 나타난다. 전송할 데이터가 많을 때는 Fetch Call이 여러 번 발생한다.  
<br/>

![Untitled (3)](https://github.com/Reffy08/TIL/assets/95058915/69995db7-76ce-40e4-878c-a0d5a2ea33f7)
<br/>

Call이 어디서 발생하느냐에 따라 User Call과 Recursive Call로 나눌 수도 있다.  

- User Call : 네트워크를 경유해 DBMS 외부로부터 인입되는 Call이다. 최종 사용자(User)는 클라이언트 단에 위치하지만, DBMS 입장에서 사용자는 WAS 이다. User Call은 WAS 서버에서 발생하는 Call 이다.
- Recursive Call : DBMS 내부에서 발생하는 Call이다. SQL 파싱과 최적화 과정에서 발생하는 데이터 딕셔너리 조회, PL/SQL로 작성한 사용자 정의 함수/프로시저/트리거에 내장된 SQL을 실행할 때 발생하는 Call이 해당된다.  
<br/>

두 경우 모두 SQL 세 단계를 거치며, DB의 Call이 많으면 성능은 느릴 수 밖에 없다. 특히 네트워크를 경유하는 User Call이 성능에 미치는 영향은 매우 크다.  
<br/>

**One SQL의 중요성**  

```sql
insert into emp_history
select * from emp
```
<br/>

업무 로직이 복잡하면 절차적으로 처리할 수 밖에 없지만, 그렇지 않다면 One SQL로 구현하도록 노력해야 한다.  

- Insert Into Select
- 수정가능 조인 뷰
- Merge 문  

### 인덱스 및 제약 해제를 통한 대량 DML 튜닝

인덱스와 무결성 제약 조건은 DML 성능에 큰 영향을 끼친다.  

OLTP(온라인 트랜잭션 처리 시스템)에서 이들의 기능을 해제할 순 없지만, 동시 트랜잭션 없이 대량 데이터를 적재하는 배치 프로그램에서는 이들 기능을 해제함으로써 큰 성능개선 효과를 얻을 수 있다.  
<br/>

```sql
-- 테이블의 모든 로우 제거
truncate table target;
-- pk 제약 및 인덱스 해제
alter table target modify constraint target_pk disable drop index;
-- 일반 인덱스 비활성화
alter index target_x1 unusable; 

-- pk 활성화(자동으로 pk 인덱스 생성)
alter table target modify constraint target_pk enable;
```
<br/>

## 파티션을 활용한 DML 튜닝

### **파티션(Partition)이란?**

DB에서 관리하는 대용량 데이터를 갖는 테이블의 Transaction 작업이나 쿼리 등의 처리 및 테이블을 관리하는 부분에서 TroubleShooting(시스템에서 발생하는 문제를 해결해가는 것)이 발생하면 성능이나 DB를 관리하는데 영향을 받게 된다.

하나의 테이블을 물리적으로 분리한 것이지만 논리적으로는 하나의 테이블로 간주한다.

파티션은 대용량 데이터를 보다 효율적으로 관리하기 위해서 테이블을 나눔으로써 데이터 액세스 작업의 성능을 향상시키고 보다 편하게 관리를 할 수 있도록 해준다.

파티션은 하나의 테이블을 세분화 하는만큼 세심한 관리가 요구된다.

파티션 키로 나누어져 있는 테이블에 파티션 키를 조건으로 주지않아 전체 파티션을 액세스하지 않도록 주의해야 한다. (파티션 키 : 파티션을 나눌때 기준이 되는 키 컬럼)  
<br/>

### **파티션 테이블 종류**

**Range Partition**  

범위로 구분되는 파티션 테이블로 Range는 숫자, 날짜, 문자 모두가 가능하다.  
<br/>

```sql
-- 테이블 생성 및 파티션 지정
create table emp(
  empno    integer primary key,
  ename    varchar(10),
  job      varchar(9),
  mgr      Numeric(4,0),
  hiredate date,
  sal      Numeric(7,2),
  comm     Numeric(7,2),
  deptno   integ
)
partition by range (empno)
(
  partition empno_p1 values less than (5),
  partition empno_p2 values less than (10),
  partition empno_p3 values less than (maxvalue)
);

-- 쿼리 삽입 후 파티션 값 확인
select * from 테이블명 partition ( 파티션명 );
```
<br/>

**List Partition**  

범위가 아닌 특정한 값으로 구분되는 파티션 테이블로 주로 특정 구분자로 데이터의 구분이 가능한 경우에 사용한다.  
<br/>

```sql
-- 테이블 및 파티션 생성
create table emp(
  empno    integer primary key,
  ename    varchar(10),
  job      varchar(9),
  mgr      Numeric(4,0),
  hiredate date,
  sal      Numeric(7,2),
  comm     Numeric(7,2),
  deptno   integer
)
partition by list (deptno)
(
  partition deptno_p1    values (10),
  partition deptno_p2    values (20),
  partition deptno_p3   values (30),
  partition deptno_p4 values (40),
  partition deptno_null    values (null),
  partition deptno_unknown values (default)
);

-- 쿼리 삽입 후 파티션 값 확인
select * from 테이블명 partition ( 파티션명 );
```
<br/>

**Hash Partition**  

해시함수에 의해 자동으로 패턴 갯수만큼 데이터가 분할되는 파티션 테이블로 해시 파티션키로는 사용할 수 있는 컬럼의 아무 타입(숫자, 문자, 날짜 등)이나 사용 가능하다.  

Range Partition, List Partition과는 달리 Hash Partition의 경우 저장되는 데이터가 어느 파티션으로 저장될 지 알 수 없기 때문에, 관리의 목적에는 맞지 않다.  

Hash Partition을 사용하는 이유는 데이터의 여러 위치에 분산배치해서 Disk I/O 성능을 개선하기 위함이다.  
<br/>

```sql
-- 테이블 및 파티션 생성
create table emp(
  empno    integer primary key,
  ename    varchar(10),
  job      varchar(9),
  mgr      Numeric(4,0),
  hiredate date,
  sal      Numeric(7,2),
  comm     Numeric(7,2),
  deptno   integer
)
partition by hash (empno)
partitions 5;

-- 쿼리 삽입 후 파티션 값 확인
select * from 테이블명 partition ( 파티션명 );
```
<br/>

### 인덱스 파티션

인덱스 파티션 구성을 설명하기 위해 테이블 파티션을 구분한다.  

- 비파티션 테이블(Non-Partitional Table)
- 파티션 테이블(Partitional Table)  
<br/>

파티션 인덱스는 각 파티션이 커버하는 테이블 파티션 범위에 따라 로컬과 글로벌로 나눈다.  

- 로컬 파티션 인덱스(Local Partitioned Index)
- 글로벌 파티션 인덱스(Global Partitioned Index)
- 비파티션 인덱스(Non-Partitioned Index)  
<br/>

로컬 파티션 인덱스는 각 테이블 파티션과 인덱스 파티션이 서로 1:1 관계가 되도록 오라클이 자동으로 관리하는 파티션 인덱스를 말한다.  

로컬이 아닌 파티션 인덱스는 모두 글로벌 파티션 인덱스이며, 테이블 파티션과 독립적인 구성(파티션 키, 파티션 기준값 정의)을 갖는다.  
<br/>

![Untitled (4)](https://github.com/Reffy08/TIL/assets/95058915/9d5534a2-5328-4017-aed3-f48c6cbb4718)
<br/>


**로컬 파티션 인덱스**  

로컬 파티션은 파티션별로 별도 색인을 만드는 것과 같다.  

로컬 인덱스라고 줄여서 부르기도 한다.  
<br/>
```sql
create index 인덱스명 on 테이블명 (컬럼명, ...) LOCAL;

create index 주문_x01 on 주문 (주문일자, 주문금액) LOCAL;
```
<br/>

각 인덱스 파티션은 테이블 파티션 속성을 그대로 상속 받는다.  

테이블 파티션 키가 주문일자면 인덱스 파티션 키도 주문일자가 된다.  
<br/>

![Untitled (5)](https://github.com/Reffy08/TIL/assets/95058915/c233945c-bfde-448f-b8b7-30881612fdc3)  
<br/>

로컬 파티션 인덱스는 테이블과 정확이 1:1 대응 관계를 갖도록 오라클이 파티션을 자동으로 관리해준다.  

테이블 파티션 구성을 변경(add, drop, change) 하더라도 인덱스를 재생성할 필요가 없다.

변경작업은 순식간에 끝나므로 피크(peak) 시간대만 피하면 서비스를 중단하지 않고도 작업할 수 있다.  

로컬 파티션의 장점은 관리 편의성에 있다.  
<br/>

**글로벌 파티션 인덱스**  

파티션 테이블과 다르게 구성한 인덱스다. 구체적으로, 파티션 유형이 다르거나, 파티션 키가 다르거나, 파티션 기준값 정의가 다른 경우다.  
<br/>
```sql
create index 주문_x03 on 주문 (주문일자, 주문금액) GLOBAL;
```
<br/>

![Untitled (5-1)](https://github.com/Reffy08/TIL/assets/95058915/ef958bc9-56aa-4654-9da2-737c4c3f21e7)  
<br/>

글로벌 파티션 인덱스는 테이블 파티션 구성을 변경(drop, exchange, split 등) 하는 순간 Unusable 상태로 바뀌므로 곧바로 인덱스를 재생성해야 한다. 그동안 해당 테이블은 사용하는 서비스를 중단해야 한다.  

테이블과 인덱스가 정확히 1:! 관계가 되도록 DB 관리자가 파티션을 직접 구성할 수도 있지만, 그렇다고 로컬 파티션은 아니다. 오라클이 인덱스 파티션을 자동으로 관리해 주지 않는다.  
<br/>

**비파티션 인덱스**  

파티셔닝 하지 않은 인덱스다.
<br/>

```sql
create index 주문_x04 on 주문 (고객ID, 배송일자);
```
<br/>

비파티션 인덱스는 여러 테이블 파티션 인덱스를 가리켜서 글로벌 비파티션 인덱스라고 부르기도 한다.  

![Untitled (6)](https://github.com/Reffy08/TIL/assets/95058915/72cd000b-675a-42b2-b7e7-1cb44587b43e)  

비 파티셔션 인덱스는 테이블 파티션 구성을 변경(drop, exchange, split 등)하는 순간 Unusable 상태로 바뀌므로 곧바로 인덱스를 재생성해야 한다. 그동안 해당 테이블은 사용하는 서비스를 중단해야 한다.  
<br/>

**Prefixed vs Nonprefixed**  

파티션 컬럼이 인덱스 구성상 왼쪽 선두 컬럼에 위치하는지에 따라 나눌 수 있다. 

- Prefixed : 인덱스 파티션 키 컬럼이 인덱스 키 컬럼 왼쪽 선두에 위치한다.
- Nonprefixed : 인덱스 파티션 키 컬럼이 인덱스 키 컬럼 왼쪽 선두에 위치하지 않는다. 파티션 키가 인덱스 컬럼에 아예 속하지 않을 때도 여기에 속한다.  
<br/>

로컬과 글로벌, Prefixed와 Nonprefixed를 조합하면 네 가지 구성이 나온다.  

<br/>
![Untitled (7)](https://github.com/Reffy08/TIL/assets/95058915/05377b8c-dcdf-4872-a6c3-943161dd90d4)  
<br/>
글로벌 파티션 인덱스는 Prefixed 파티션만 지원된다.

- 로컬 Prefixed 파티션 인덱스  
- 로컬 Nonprefixed 파티션 인덱스  
- 글로벌 Prefixed 파티션 인덱스
- 비파티션 인덱스  
<br/>

**중요한 인덱스 파티션 제약**  

> Unique 인덱스를 파티셔닝 하려면, 파티션 키가 모두 인덱스 구성 컬럼이어야 한다.


Unique 인덱스를 파티셔닝할 때 파티션 키가 인덱스 컬럼에 포함돼야 한다는 조건은 DML 성능 보장을 위해 당연히 있어야 할 제약조건이다.  

파티션 키 조건 없이 PK 인덱스로 액세스하는 수 많은 쿼리 성능을 위해서도 필요하다.

문제는 이 제약으로 인해 PK 인덱스를 로컬 파티셔닝 하지 못하면 파티션 Drop, Truncate, Exchange, Split, Merge 같은 파티션 구조 변경 작업도 쉽지 않다는 데 있다. 이들 작업을 하는 순간 PK 인덱스가 Unusable 상태로 바뀌기 때문이다. 곧바로 인덱스를 Rebuild하면 되지만, 그동안 해당 테이블을 사용하는 서비스를 중단해야 한다.  

서비스 중단 없이 파티션 구조를 빠르게 변경하려면 PK를 포함한 모든 인덱스가 로컬 파티션 인덱스이어야 한다.  
<br/>

### 파티션을 활용한 대량 UPDATE 튜닝

인덱스를 Drop하거나 Unusable 상태로 변경하고서 작업하는 방법을 많이 활용한다.  

손익분기점은 5%로 본다. 즉, CRUD 데이터 비중이 5%를 넘는다면, 인덱스를 그대로 둔 상태에서 작업하는 것 보다 인덱스 없이 작업 후 재생성하는게 더 빠르다.  

하지만 인덱스를 제거 후 재생성하는 부담도 크기 때문에 그대로 둔 상태에서 작업하는 경우가 많다.  
<br/>

**파티션 Exchange를 이용한 대량 데이터 변경**  

테이블이 파티셔닝 되어있고 인덱스도 로컬 파티션이라면, 수정된 값을 갖는 임시 세그먼트를 만들어 원본 파티션과 바꿔치기 하는 방식을 사용한다.

(1) 임시 테이블을 생성한다. 할 수 있다면 nologging 모드로 생성한다.  
<br/>

```sql
create table 거래_t
nologging 
as
select * from 거래 where 1 = 2;
```
<br/>

(2) 거래 데이터를 읽어 임시 테이블에 입력하면서 상태코드 값을 수정한다.

```sql
insert /*+ append */ into 거래_t
select 고객번호, 거래일자, 거래순번 ... 
    ,(case when 상태코드 <> 'ZZZ' then 'ZZZ' else 상태코드 end) 상태코드
from 거래
where 거래일자 < '20150101';
```
<br/>

(3) 임시 테이블에 원본 테이블과 같은 구조로 인덱스를 생성한다. 할 수 있다면 nologging 모드로 생성한다.

```sql
create unique index 거래_t_pk on 거래_t (고객번호, 거래일자, 거래순번) nologging;
create index 거래_t_x1 on 거래_t (거래일자, 고객번호) nologging;
create index 거래_t_x2 on 거래_t (상태코드, 거래일자) nologging;
```
<br/>

(4) 파티션과 임시 테이블을 Exchange 한다.

```sql
alter table 거래
exchange partition p201412 with table 거래_t
including indexes without validation;
```
<br/>

(5) 임시 테이블을 Drop 한다

```sql
drop table 거래_t;
```
<br/>

(6) (nologging 모드로 작업했다면) 파티션을 logging 모드로 전환한다.  

```sql
alter table 거래 modify partition p20412 logging;
alter index 거래_pk modify partition p20412 logging;
alter index 거래_x1 modify partition p20412 logging;
alter index 거래_x2 modify partition p20412 logging;
```
<br/>
<br/>

### 파티션을 활용한 대량 DELETE 튜닝

D ELETE 는 여러 부수적인 작업을 수반하므로 느리다.  
 
- 테이블 레코드 삭제
- 테이블 레코드 삭제에 대한 Undo Logging
- 테이블 레코드 삭제에 대한 Redo Logging
- 인덱스 레코드 삭제
- 인덱스 레코드 삭제에 대한 Undo Logging
- 인덱스 레코드 삭제에 대한 Redo Logging
- Undo에 대한 Redo Logging  
<br/>

특히 각 인덱스 레코드를 찾아서 삭제해주는 작업에 대한 부담이 크다. 수직적 탐색 과정을 거쳐 대상 레코드를 찾아야 하기 때문이다.  
<br/>

**파티션 Drop을 이용한 대량 데이터 삭제**  

테이블이 삭제 조건절 컬럼 기준으로 파티셔닝 되어있고, 인덱스도 로컬 파이션이라면 대량데이터를 순식간에 제거 가능하다.  
<br/>

```sql
alter table 거래 drop partition p201412;

-- 오라클 11g 버전 : 대상 파티션 지정
alter table 거래 drop partition for('20141201');
```
<br/>

**파티션 Truncate를 이용한 대량 데이터 삭제**  

거래일자 조건에 해당하는 데이터를 일괄 삭제하지 않고 아래와 같이 또 다른 삭제 조건이 있는 경우 아래의 코드를 사용한다.  
<br/>

```sql
delete from 거래
where 거래일자 < '20150101'
and (상태코드 <> 'ZZZ' or 상태코드 is null);
```
<br/>

조건을 만족하는 데이터가 대다수면, 대량 데이터를 지울 게 아니라 남길 데이터만 백업했다가 재입력하는 아래의 방식이 빠르다.  
<br/>

(1) 임시 테이블을 생성하고, 남길 데이터만 복제한다.  

```sql
create table 거래_t
as
select * 
from 거래
where 거래일자 < '20150101'
and 상태코드 = 'ZZZ'; -- 남길 데이터만 임시 세그먼트로 복제
```
<br/>

(2) 삭제 대상 테이블이 파티션을 Truncate 한다.  

```sql
alter table 거래 truncate partition p201412;

-- 오라클 11g 이후
alter table 거래 truncate partition for('201412');
```
<br/>

(3) 임시 테이블에 복제해 둔 데이터를 원본 테이블에 입력한다.  

```sql
insert into 거래 
select * from 거래_t; -- 남길 데이터만 입력
```
<br/>

(4) 임시 테이블을 Drop 한다.  

```sql
drop table 거래_t;
```

서비스 중단 없이 파티션을 Drop 또는 Truncate 하려면 아래 조건을 모두 만족해야 한다.  

(1) 파티션 키와 커팅 기준 컬럼이 일치해야 함.  

- ex) 파티션 키와 커팅 기준 컬럼이 모두 ‘신청일자’

(2) 파티션 단위 커팅 주기가 일치해야 함  

- ex) 월 단위 파티션을 월 주기로 커팅

(3) 모든 인덱스라 로컬 파티션 인덱스이어야 함  

- ex) 파티션 키는 ‘신청일자’, PK는 ‘신청일자 + 신청순번’
- PK 인덱스는 지금처럼 삭제 기준(파티션 키) 컬럼이 인덱스 구성 컬럼이어야 로컬 파티셔닝 가능  
<br/>

### 파티션을 활용한 대량 INSERT 튜닝

**비파티션 테이블일 때**  

비파티션 테이블에 손익분기점을 넘는 대량 데이터를 INSERT 하려면, 인덱스를 Unusable 시켰다가 재생성하는 방식이 더 빠를 수 있다.  
<br/>
(1) 테이블을 nologging 모드로 전환한다.

```sql
alter table target_t nologging;
```
<br/>

(2) 인덱스를 Unusable 상태로 전환한다.  

```sql
alter index target_t_x01 unusable;
```
<br/>

(3) (할 수 있다면 nologging 모드) 대량 데이터를 입력한다.  

```sql
insert /*+ append */ into target_t 
select * from source_t;
```
<br/>

(4) (할 수 있다면 nologging 모드) 인덱스를 재생성 한다.  

```sql
alter index target_t_x01 rebuild nologging;
```
<br/>

(5) (nologging 모드로 작업했다면) logging 모드로 전환한다.  

```sql
alter table target_t logging;
alter index target_t_x01 logging;

```
<br/>

**파티션 테이블일 때**  

테이블이 파티셔닝되어 있고, 인덱스도 로컬 파티션이면, 파티션 단위로 인덱스를 재생성 할 수 있다.  

(1) (할 수 있다면) 작업 대상 테이블 파티션을 nologging 모드로 전환한다.  

```sql
alter table target_t modify partition p_201712 nologging;
```
<br/>

(2) 작업 대상 테이블을 파티션과 매칭되는 인덱스 파티션 Unusable 상태로 전환한다.  

```sql
alter index target_t_x01 modify partition p_201712 unusable;
```
<br/>

(3) (할 수 있다면 Direct Path Insert 방식으로) 대량 데이터를 입력한다.  

```sql
insert /*+ append */ into target_t 
select * from source_t where dt between '20171201' and '20171231';
```
<br/>

(4) (할 수 있다면 nologging 모드) 인덱스 파티션을 재생성한다.  

```sql
alter index target_t_x01 rebuild partition p_201712 nologging;
```
<br/>

(5) (nologging 모드로 작업했다면) 작업 파티션을 logging모드로 전환한다.  

```sql
alter table target_t modify partition p_201712 logging;
alter index target_t_x01 modify partition p_201712 logging;
```
<br/>
<br/>

## Lock과 트랜잭션 동시성 제어

Lock은 데이터베이스의 특징을 결정짓는 가장 핵심적인 메커니즘이다.
