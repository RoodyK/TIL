# PostgreSQL 파티션 정리

### **파티션(Partition)이란?**

서비스의 규모가 커짐에 따라서 DB에 저장되는 데이터량도 커지게 되고, 기존 테이블의 Transaction 작업이나 SQL 처리, 테이블을 관리하는 등의 작업에서 DB관리나 성능에 영향을 받게 되었다.  

파티션은 대용량 데이터를 보다 효율적으로 관리하기 위해서 테이블을 나눔으로써 데이터 액세스 작업의 성능을 향상시키고 보다 편하게 관리를 할 수 있도록 해준다.  

파티션은 하나의 테이블을 물리적으로 분리한 것이지만 논리적으로는 하나의 테이블로 간주하며, 하나의 테이블을 세분화 하는만큼 세심한 관리가 요구된다.  

파티션 키로 나누어져 있는 테이블에 파티션 키를 조건으로 주지않아 전체 파티션을 액세스하지 않도록 주의해야 한다. (파티션 키 : 파티션을 나눌때 기준이 되는 키 컬럼)  
<br/>
<br/>

### **PostgreSQL 파티션**

PostgreSQL 은 v9까지 partition 명령어가 존재하지 않아 상속을 통해서 구현했었지만 v10 이후부터 Partition 명령어를 지원하기 시작했고(Range, List), v11부터 Hash Partition 까지 지원하기 시작했다.  

파티션은 분할된 테이블이기 때문에 선언 시 분할 방법과 파티션 키를 명시해야 한다.  

분할된 파티션은 저장소가 따로 없는 가상의 테이블로, 원본 테이블이 제거되면 파티션들도 자동으로 제거되게 된다.  

파티션 키는 PK를 갖는 원본 테이블을 파티션으로 분할 시 PK값을 포함해야 한다.  

파티션을 선언한 테이블은 지정된 파티션이 없으면 테이블 레코드에 값을 Insert 할 수 없다. (default 파티션 존재 시 가능)  
<br/>
<br/>

### ※ **파티션 테이블 종류 및 생성**

여기선 v9까지 사용하던 상속에 의한 생성방법은 설명하지 않고 v10 부터 지원하는 선언적 방식을 사용한다.  

각 파티션을 생성하고 분할된 파티션 및 원본 테이블로 전체 데이터도 조회 가능하다.  
<br/>
<br/>

**Range Partition**

범위로 구분되는 파티션 테이블로, 각 분할된 파티션의 범위는 겹치지 않는다.   

Range는 숫자, 날짜, 문자 모두가 가능하다.  
<br/>

```sql
-- 테이블 및 파티션 생성 (id값을 파티션 키로 사용)
CREATE TABLE NOTICE (
	notice_id bigserial,
	title varchar(100),
	content varchar(500),
	created_at date,
	CONSTRAINT notice_pk PRIMARY KEY (notice_id)
)
PARTITION BY RANGE (notice_id);

-- 파티션 선언
CREATE TABLE notice_p1 PARTITION OF NOTICE 
FOR VALUES FROM (1) TO (6);

CREATE TABLE NOTICE_p2 PARTITION OF NOTICE
FOR VALUES FROM (6) TO (11);	

CREATE TABLE notice_p3 PARTITION OF NOTICE 
FOR VALUES FROM (11) TO (MAXVALUE);

CREATE TABLE notice_p_defult PARTITION OF NOTICE DEFAULT;

-- 단일 파티션 조회
SELECT * FROM notice_p1;

-- 다중 파티션 조회
SELECT * FROM notice_p1
UNION ALL 
SELECT * FROM notice_p2;

-- 전체 테이블도 조회 가능
SELECT * FROM notice 


-- Range Partition 날짜로 분할
CREATE TABLE NOTICE (
	notice_id bigserial,
	title varchar(100),
	content varchar(500),
	created_at date
)
PARTITION BY RANGE (created_at);

CREATE TABLE notice_p1 PARTITION OF NOTICE 
FOR VALUES FROM ('1990-01-01') TO ('2000-01-01');

CREATE TABLE notice_p2 PARTITION OF NOTICE 
FOR VALUES FROM ('2000-01-01') TO ('2010-01-01');

CREATE TABLE NOTICE_p3 PARTITION OF NOTICE
FOR VALUES FROM ('2010-01-01') TO ('2020-01-01');

CREATE TABLE notice_p4 PARTITION OF NOTICE 
FOR VALUES FROM ('2020-01-01') TO ('2030-01-01');

CREATE TABLE notice_p_defult PARTITION OF NOTICE DEFAULT;

SELECT * FROM notice_p1;

SELECT * FROM notice_p2
UNION all
SELECT * FROM notice_p3
UNION all
SELECT * FROM notice_p4;
```
<br/>
<br/>

**List Partition**

범위가 아닌 특정한 값으로 구분되는 파티션 테이블로 주로 특정 키 값으로 데이터의 구분이 가능한 경우에 사용한다.  

List Partition의 파티션 키로 지정하지 않은 값은 insert 되지 않는다.  
<br/>

```sql
-- 테이블 및 파티션 생성
create table emp (
  empno bigint,
  ename varchar(10),
  job varchar(9),
  mgr Numeric(4,0),
  hiredate timestamp,
  sal Numeric(7,2),
  deptno bigint,
  constraint pk_emp primary key (empno, deptno),
  constraint fk_deptno foreign key (deptno) references dept (deptno)
)
PARTITION BY list (deptno);

CREATE TABLE emp_p1 PARTITION OF emp  
FOR VALUES in (10);

CREATE TABLE emp_p2 PARTITION OF emp  
FOR VALUES in (20);

CREATE TABLE emp_p3 PARTITION OF emp
FOR VALUES IN (30);

CREATE TABLE emp_p4 PARTITION OF emp
FOR VALUES IN (40);

CREATE TABLE emp_p5 PARTITION OF emp
FOR VALUES IN (50);

-- 단일 파티션 조회
SELECT * FROM emp_p1;

-- 다중 파티션 조회
SELECT * FROM emp_p1
UNION ALL 
SELECT * FROM emp_p2;
```
<br/>
<br/>

**Hash Partition**

해시함수에 의해 자동으로 패턴 갯수만큼 데이터가 분할되는 파티션 테이블로 해시 파티션키로는 사용할 수 있는 컬럼의 아무 타입(숫자, 문자, 날짜 등)이나 사용 가능하다.  

파티션을 분할시킬 명확한 방법이 없거나, 대용량이 될 것으로 예상되는 테이블을 설정하기 어려울 때 유용하다.  

Range Partition, List Partition과는 달리 Hash Partition의 경우 저장되는 데이터가 어느 파티션으로 저장될 지 알 수 없기 때문에, 관리의 목적에는 맞지 않다.  

Hash Partition을 사용하는 이유는 데이터의 여러 위치에 분산배치해서 Disk I/O 성능을 개선하기 위함이다.  
<br/>

```sql
-- 테이블 및 파티션 생성
CREATE TABLE NOTICE (
	notice_id bigserial,
	title varchar(100),
	content varchar(500),
	created_at date,
	CONSTRAINT notice_pk PRIMARY KEY (notice_id)
)
PARTITION BY hash (notice_id);

CREATE TABLE notice_p1 PARTITION OF NOTICE
FOR VALUES WITH (MODULUS 5, REMAINDER 0);

CREATE TABLE notice_p2 PARTITION OF NOTICE
FOR VALUES WITH (MODULUS 5, REMAINDER 1);

CREATE TABLE notice_p3 PARTITION OF NOTICE
FOR VALUES WITH (MODULUS 5, REMAINDER 2);

CREATE TABLE notice_p4 PARTITION OF NOTICE
FOR VALUES WITH (MODULUS 5, REMAINDER 3);

CREATE TABLE notice_p5 PARTITION OF NOTICE
FOR VALUES WITH (MODULUS 5, REMAINDER 4);

-- HASH PARTITION은 default 파티션을 생성할 수 없다.
-- CREATE TABLE notice_p_default PARTITION OF NOTICE DEFAULT; 

-- 파티션 조회
SELECT * FROM notice_p1;

SELECT * FROM notice_p2
UNION all
SELECT * FROM notice_p3
```
<br/>

Hash Partition 에서 분할되는 방식은 계수(MODULUS)로 파티션 키를 나눈 나머지(REMAINDER)가 파티션으로 설정한 테이블 설정과 일치할 때 해당 파티션으로 레코드가 생성되게 된다.  

- ex) notice\_id % 5 == 0 파티션 1에 할당됨. 

Hash Partition은 Default 파티션을 생성할 수 없는 것을 참고해야 한다.   
<br/>
<br/>

#### **파티션 인덱스**

테이블을 파티션으로 분할했을 때 원본 테이블의 인덱스는 분할된 모든 파티션에도 적용된다.  

파티션 테이블을 생성한 이후에 원본 테이블에서 인덱스를 생성해도 각 파티션에 적용된다.  

각각의 파티션에서도 인덱스를 정의할 수 있는데, 이 때는 각 파티션에 공유되는 것이 아닌 개별적인 인덱스를 갖게 된다.  
<br/>
<br/>
<br/>
<br/>
<br/>

### 참조
-   [https://www.postgresql.org/docs/14/ddl-partitioning.html](https://www.postgresql.org/docs/14/ddl-partitioning.html)


<br/>
<br/>
<br/>
