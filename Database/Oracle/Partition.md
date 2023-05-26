# 파티션

### **파티션(Partition)이란?**

DB에서 관리하는 대용량 데이터를 갖는 테이블의 Transaction 작업이나 쿼리 등의 처리 및 테이블을 관리하는 부분에서 TroubleShooting(시스템에서 발생하는 문제를 해결해가는 것)이 발생하면 성능이나 DB를 관리하는데 영향을 받게 된다.  

하나의 테이블을 물리적으로 분리한 것이지만 논리적으로는 하나의 테이블로 간주한다.  

파티션은 대용량 데이터를 보다 효율적으로 관리하기 위해서 테이블을 나눔으로써 데이터 액세스 작업의 성능을 향상시키고 보다 편하게 관리를 할 수 있도록 해준다.  

파티션은 하나의 테이블을 세분화 하는만큼 세심한 관리가 요구된다.  

파티션 키로 나누어져 있는 테이블에 파티션 키를 조건으로 주지않아 전체 파티션을 액세스하지 않도록 주의해야 한다. (파티션 키 : 파티션을 나눌때 기준이 되는 키 컬럼)  
<br/>

※ **파티션 테이블 종류**

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
);
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
);
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

해시함수에 의해 자동으로 파텬 갯수만큼 데이터가 분할되는 파티션 테이블로 해시 파티션키로는 사용할 수 있는 컬럼의 아무 타입(숫자, 문자, 날짜 등)이나 사용 가능하다.  

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
);
partition by hash (empno)
partitions 5;

-- 쿼리 삽입 후 파티션 값 확인
select * from 테이블명 partition ( 파티션명 );
```
<br/>
<br/>
