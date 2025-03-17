# 인덱스 기본

## 인덱스 구조 및 탐색

인덱스 탐색 과정은 수직적 탐색과 수평적 탐색, 두 단계로 이루어진다.  
<br/>

### 미리보는 인덱스 튜닝
<br/>

**데이터를 찾는 두 가지 방법**  

- 테이블 전체를 스캔한다.
- 인덱스를 이용한다.  
<br/>

**인덱스 튜닝의 두 가지 핵심 요소**   

- 인덱스 스캔 효율화 튜닝 : 인덱스 스캔 과정에서 발생하는 비효율을 줄이는 것.
- 랜덤 액세스 최소화 튜닝 : 테이블 액세스 횟수를 줄이는 것.  
<br/>

두 가지 방법 중 랜덤 액세스 최소화 튜닝이 성능에 미치는 영향이 더 크다.  (랜덤 엑세스란 데이터를 저장하는 블록을 한 번에 하나의 블록만을 액세스하는 방식이다.)  
<br/>

**SQL 튜닝은 랜덤 I/O와의 전쟁**  

데이터 베이스 성능이 느린 이유는 디스크 I/O 때문이며, 읽어야 할 데이터량이 많고 인덱스를 많이 사용하는 OLTP(Online Transaction Processing) 시스템이라면 디스크 I/O중에서도 랜덤 I/O가 특히 중요하다.   
<br/>

### 인덱스 구조

인덱스는 대용량 테이블에서 필요한 데이터만 빠르게 효율적으로 액세스 하기 위해 사용하는 오브젝트다. 모든 책 뒤에 있는 색인과 같은 역할을 한다.  

DB에서 인덱스 없이 데이터를 검색하려면, 처음부터 끝까지 읽어야 하지만, 인덱스가 있으면 범위 스캔(Range Scan)이 가능하다. 인덱스는 정렬되어 있기 때문이다.  

DBMS는 일반적으로 B*Tree 인덱스 구조를 사용한다.  

![Untitled](https://github.com/Reffy08/TIL/assets/95058915/e8196c71-7bc4-47f1-bbb7-705112d15cbd)
<br/>

루트와 브랜치 블록에 있는 각 레코드는 하위 블록에 대한 주소 값을 갖는다.  

키 값은 하위 블록에 저장된 키 값의 범위를 나타낸다.  

루트와 브랜치 블록에는 키 값을 갖지 않는 특별한 레코드가 하나 있는데, 이를 LMC(Left Most Child)라 한다. LMC는 자식 노드 중 가장 왼쪽 끝에 위치한 블록을 가리킨다.   

LMC가 가리키는 주소에는 키 값을 가진 첫 번째 레코드보다 작거나 같은 레코드가 저장되어 있다.  

리프 블록에 저장된 각 레코드는 키 값 순으로 정렬돼 있을 뿐만 아니라 테이블 레코드를 가리키는 주소 값, 즉 ROWID를 갖는다.  인덱스 키 값이 같으면 ROWID 순으로 정렬된다.  

인덱스를 스캔하는 이유는, 검색 조건을 만족하는 소량의 데이터를 빨리 찾고 거기서 ROWID를 얻기 위해서 이다.  

- ROWID = 데이터 블록 주소 + 로우 번호
- 데이터 블록 주소 = 데이터 파일 번호 + 블록 번호
- 블록 번호 : 데이터 파일 내에서 부여한 상대적 순번
- 로우 번호 : 블록 내 순번  
<br/>

인덱스 탐색 과정은 수직적 탐색과 수평적 탐색으로 나눌 수 있다.

- 수직적 탐색 : 인덱스 스캔 시작지점을 찾는 과정
- 수평적 탐색 : 데이터를 찾는 과정  
<br/>

### 인덱스의 수직적 탐색

정렬된 인덱스 레코드 중 조건을 만족하는 첫 번째 레코드를 찾는 과정이다. (인덱스 스캔 시작 지점을 찾는 과정)  
  
인덱스 수직적 탐색은 루트 블록에서부터 시작한다. 루트를 포함해 브랜치 블록에 저장된 각 인덱스 레코드는 하위 블록에 대한 주소값을 갖는다. 루트에서 시작해 리프 블록까지 수직적 탐색이 가능한 이유이다.  

수직적 탐색 과정에서 찾고자 하는 값보다 크거나 같은 값을 만나면, 바로 직전 레코드가 가리키는 하위 블록으로 이동한다.  
<br/>

### 인덱스의 수평적 탐색

수직적 탐색을 통해 스캔 시작점을 찾았으면, 찾고자 하는 데이터가 더 안 나타날 때까지 인덱스 리프 블록을 수평적으로 스캔한다. (데이터를 찾는 과정)  

인덱스 리프 블록끼리는 서로 앞뒤 블록에 대한 주소값을 갖는다. 양방향 연결 리스트 구조(Double Linked List)이다.  
<br/>

인덱스를 수평적으로 탐색하는 이유

- 조건절을 만족하는 데이터를 모두 찾기 위해서.
- ROWID를 얻기 위해서. (일반적으로 인덱스를 스캔하고서 테이블도 액세스 하는데, 이 때 ROWID가 필요하다.)  
<br/>

### 결합 인덱스 구조와 탐색

![Untitled (1)](https://github.com/Reffy08/TIL/assets/95058915/987b1172-4b51-4d7b-a3ac-7f34fdddcc89)
<br/>

```sql
CREATE INDEX C_1 ON 고객(고객명, 성명)
CREATE INDEX C_2 ON 고객(성명, 고객명)

SELECT * FROM 고객 WHERE 고객명 = '이재희' and 성별 = '남' 
SELECT * FROM 고객 WHERE 성별 = '남' and 고객명 = '이재희'
```
<br/>

위와 같은 결합 인덱스에서, 인덱스 선두 컬럼을 모두 = 조건으로 검색할 때는 어느 컬럼을 인덱스 앞쪽에 두든 블록 I/O 개수가 같으므로 성능도 같다.  

DBMS가 사용하는 B*Tree 인덱스는 엑셀처럼 평면 구조가 아니다. 인덱스를 [이름 + 성별]로 구성하든 [성별 + 이름]으로 구성하든 일량에는 차이가 없다.  
<br/>

**※ Balanced의 의미**

B*Tree의 B(Balanced)는 어떤 값으로 탐색하더라도 인덱스 루트에서 리프 블록에 도달하기까지 읽는 블록 수가 같음을 의미한다.  
<br/>

## 인덱스 기본 사용법

인덱스 기본 사용법은 인덱스를 범위 스캔(Range Scan) 하는 방법을 의미한다.  
<br/>

### 인덱스를 사용한다는 것

‘검색’으로 시작하는 단어를 인덱스로 찾게 되면, 정렬된 색인에서 우리가 찾는 단어의 시작 지점을 찾아 갔을 것이고(수직적 탐색), 그 후 리프 노드에서 수평적 탐색을 진행했을 것이다.  

하지만 ‘검색’을 포함하는 단어를 인덱스로 찾게되면, 시작점을 찾을 수없다는 것이 다르다. 색인이 정렬돼 있더라도 가공한 값이나 중간 값(중간에 포함된 값)으로는 스캔 시작점을 찾을 수 없다.  

데이터베이스에서도 인덱스 컬럼(선두 컬럼)을 가공하지 않아야 인덱스를 정상적으로 사용할 수 있다.  인덱스를 정상적으로 사용한다는 표현은 리프 블록 일부만 스캔하는 Index Range Scan을 의미한다.

인덱스 컬럼을 가공해도 인덱스를 사용할 수는 있지만, 스캔 시작점을 찾을 수 없고 멈출 수도 없어 리프 블록 전체를 스캔하는 Index Full Scan 방식으로 동작하게 된다.
<br/>

### 인덱스를 Range Scan 할 수 없는 이유

인덱스 컬럼을 가공했을 때 인덱스를 정상적으로 사용할 수 없는 이유는 **인덱스 스캔 시작점을 찾을 수 없기 때문**이다.  

Index Range Scan은 인덱스에서 일점 범위를 스캔한다는 의미인데, 이를 위해선 시작 지점과 끝 지점이 있어야 한다.  
<br/>

```sql
-- Index Range Scan을 할 수 없는 예
where substr(생년월일, 5, 2) = '05'
where nvl(주문수량, 0) < 100
where 업체명 like '%대한%'
where (전화번호 = :tel_no OR 고객명 = :cust_nm)
where 전화번호 in (:tel_no1, :tel_no2) -- in조건은 or조건을 표현하는 다른 방식이기 때문에 스캔되지 않는다.

-- 인덱스를 타도록 만드는 방법
-- OR Expansion : OR조건을 SQL 옵티마이저가 변환 하는 방식(인덱스를 타게 하는 방식)
select * from 고객 where 고객명 = :cust_nm -- 고객명이 선두 컬럼인 인덱스 Range Scan
union all
select * from 고객 where 전화번호 :tel_no -- 전화번호가 선두 컬럼인 인덱스 Range Scan
and (고객명 <> :cust_nm or 고객명 is null)

-- in조건을 인덱스를 타게 하기 위해선 union all을 사용한다.
select * from 고객 where 전화번호 = :tel_no1
union all
select * from 고객 where 전화번호 = :tel_no2
```
<br/>

### 더 중요한 인덱스 사용 조건

인덱스를 Range Scan하기 위한 첫 번째 초건은 인덱스 선두 컬럼이 조건절에 있어야 하며 가공되지 않은 상태여야 한다.  
<br/>

```sql
TXA1234_IX02 인덱스 : 기준년도 + 과세구분코드 + 보고회차 + 실명확인번호

select * from TXA1234
where 기준년도 = :stdr_year
and substr(과세구분코드, 1, 4) = :txtn_dcd
and 보고회차 = :rpt_tmrd
```
<br/>

위의 코드처럼 가공된 부분이 존재해도 인덱스 선두 컬럼이 가공되지 않은 상태로 조건절에 있으면 인덱스 Range Scan은 무조건 가능하다. 하지만 인덱스를 Range Scan 한다고 해서 항상 성능이 좋은 건 아니다.  
<br/>

**인덱스만 잘 타면 튜닝은 끝인가?**  

인덱스 Range Scan이 동작하더라도 수직적 탐색(인덱스 스캔 시작 범위 탐색)이 동작하겠지만 수평적 탐색(데이터를 찾는 것)의 스캔 범위를 줄이는데 지장이 가면 효율적으로 인덱스가 동작하지 않을 수 있다.  
<br/>

```sql
-- 인덱스는 [주문일자 + 상품번호]로 구성되어 있다. 데이터량(주문량)은 하루에 100만 건이라고 가정한다.
-- 선두 컬럼이 가공되지 않아 인덱스 Scan이 동작하지만 
-- 첫 번째 SQL은 중간 값 검색이고, 두 번째 SQL은 컬럼을 가공했기 때문에 효율적이지 못하다.
-- 조건절에서 인덱스 스캔 데이터량은 하루 주문량인 100만 건일 것이다.
SELECT * FROM 주문상품
WHERE 주문일자 = :ord_dt
AND 상품번호 LIKE '%PING%';

SELECT * FROM 주문상품
WHERE 주문일자 = :ord_dt
AND SUBSTR(상품번호, 1, 4) = 'PING';
```
<br/>

### 인덱스를 이용한 소트 연산 생략

인덱스 컬럼을 가공해도 인덱스를 사용할 순 있지만, 찾고자 하는 데이터가 전체 구간에 흩어져 있기 때문에 Range Scan이 불가능하거나 비효율이 발생한다.  

인덱스는 정렬되어 있기 때문에 Range Scan이 가능하고, 소트(정렬) 연산 생략 효과도 부수적으로 얻게 된다.
<br/>

```sql
-- PK_인덱스 [장비번호 + 변경일자 + 변경순번]
SELECT * FROM 상태변경이력
WHERE 장비번호 = 'C'
AND 변경일자 = '20200202'
ORDER BY 변경순번

Execution Plan
-----------------------------------------------------
0     SELECT STATEMENT Optimizer=ALL_ROWS (COST=85, CARD=81, Bytes=5K)
1   0   TABLE ACCESS (BY INDEX ROWID) OF '상태변경이력' (TABLE) (Cost=85 ... )
2   1     INDEX (RANGE SCAN) OF '상태변경이력_PK' (INDEX (UNIQUE)) (COST=3 ... )

-- 정렬 연산을 생략할 수 없게 구성되어 있을 때
Execution Plan
-----------------------------------------------------
0     SELECT STATEMENT Optimizer=ALL_ROWS (COST=85, CARD=81, Bytes=5K)
1   0   SORT (ORDER BY) (COST=86 CARD=81 Bytes=5K)
...
```
<br/>

SQL 옵티마이저는 인덱스는 이미 정렬되어있기 때문에, 인덱스를 타는 컬럼에 `ORDER BY` 문을 사용해도 따로 생략한다.  
<br/>

### ORDER BY 절에서 컬럼 가공

보통 조건절을 가공 했을 때 인덱스를 정상적으로 사용할 수 없다고 말하지만, `ORDER BY` 또는 `SELECT-LIST` 에서 컬럼을 가공함으로 인해 인덱스를 정상적으로 사용할 수 없는 경우도 종종 있다.
<br/>

```sql
-- 인덱스 [주문일자 + 주문번호]
select *
from (
	select to_char(A.주문번호, 'FM000000') AS 주문번호, A.업체번호, A.주문금액
	from 주문 A
	where A.주문일자 = :dt
	and   A.주문번호 > NVL(:next_ord_no, 0)
	order by 주문번호 -- order by A.주문번호 를 통해 SORT 생략
)
where rownum <= 30

-- 실행계획에 SORT ORDER BY 연산이 나타난다.
-- ORDER BY절에 기술한 주문번호는 순수한 주문번호가 아니라 TO_CHAR() 로 가공한 주문번호를 가리키기 때문
-- ALIAS를 사용하여 변경하면 SORT를 생력할 수 있다.  
```
<br/>

### 자동 형변환

테이블에서 예를 들어 생년월일 같은 컬럼이 문자형인데 조건절 비교값을 숫자형으로 표현 했을 때, 각 조건절에서 양쪽 값의 데이터 타입이 서로 다르면 값을 비교할 수 없다. 이 때 타입 체크를 엄격히 함으로써 컴파일 시점에 에러를내는 DBMS가 있고, 자동으로 형변환 처리를 해주는 DBMS도 있다.  

조건절에서 좌변이 우변을 이기게 되면 인덱스를 타는데 이상이 없지만, 우변이 이기게 되면 인덱스를 Range Scan 할 수 없다.  
<br/>

```sql
-- 숫자형이 문자형을 이기므로 인덱스 스캔 불가
-- 생년월일은 문자형인데 숫자형으로 인식 되어버림
SELECT * FROM 고객 WHERE 생년월일 = 19901212

-- 날짜형이 문자형을 이기므로 인덱스 사용
-- 문자열이 날짜형으로 자동 변환됨
SELECT * FROM 고객 WHERE 가입일자(날짜형) = '01-JAN-2020' 

-- 날짜형을 문자열로 지정해주면 NLS_DATE_FORMANT 파라미터가 다른 환경에서 수행 시 
-- 오류가 날 수 있으므로 포맷을 지정해주는 것이 좋다.
SELECT * FROM 고객 WHERE 가입일자(날짜형) = TO_DATE('01-JAN-2020', 'DD-MON-YYYY')
```
<br/>

연산자가 LIKE 일 때, LIKE 자체가 문자열 비교 연산자이므로 이때는 문자형 기준으로 숫자형 컬럼이 변환되는 점을 주의하기 바란다.  

LIKE 조건을 옵션 조건 처리 목적으로 사용하는 경우가 종종 있다. 예를 들어 거래 데이터 조회에 사용하는 것인데 인덱스 스캔 효율이 안좋아진다.  
<br/>

```sql
-- LIKE, BETWEEN 조건을 같이 사용했으므로 인덱스 스캔 효율이 매우 떨어진다.
SELECT * FROM 거래
WHERE 계좌번호 LIKE :acnt_no || '%'
AND   거래일자 between :trd_dt1 and :trd_dt2
```
<br/>

SQL 성능 원리를 잘 모르는 개발자는 TO_CHAR, TO_DATE, TO_NUMBER 같은 형변환 함수를 생략하면 연산횟수가 줄어 성능이 좋지 않을까라고 생각하지만, SQL 성능은 블록 I/O를 줄일 수 있는냐 없느냐에서 결정된다.  

형변환 함수를 생략한다고 해도 옵티마이저가 자동으로 생성하기 때문에 연산횟수가 주는 것도 아니다.  
<br/>

## 인덱스 확장기능 사용법

인덱스 스캔 방식은 Index Range Scan외에도 Index Full Scan, Index Unique Scan, Index Skip Scan, Index Fast Full Scan 등이 있다.
<br/>

### Index Range Scan

![Untitled (2)](https://github.com/Reffy08/TIL/assets/95058915/9a1cf5e4-a54d-4b8f-868f-03d3e86464fe)
<br/>

B*Tree 인덱스의 가장 일반적인 방식으로, 인덱스 루트에서 리프 블록까지 수직적으로 탐색한 후 필요한 범위(Range)만 스캔한다.  

선두 컬럼을 가공하지 않은 상태로 조건절에 사용해야 하며, 인덱스를 잘 타서 성능도 좋아지는 것이 아닌 성능은 인덱스 스캔 범위, 테이블 액세스 횟수를 얼마나 줄일 수 있는가로 결정된다.  

### Index Full Scan

![Untitled (3)](https://github.com/Reffy08/TIL/assets/95058915/035aff8b-ebb6-492f-8cae-a3d40572d171)
<br/>

수직적 탐색 없이 인덱스 리프 블록을 처음부터 끝까지 수평적으로 탐색하는 방식이다.  

데이터 검색을 위한 최적의 인덱스가 없을 때 차선으로 선택된다.  
<br/>

**Index Full Scan의 효용성**  

인덱스 선두 컬럼이 조건절에 없으면 옵티마이저는 Table Full Scan을 고려하게 되는데, 대용량 테이블이라면 옵티마이저는 인덱스 활용을 다시 고려하게 된다.

데이터 저장공간은 ‘컬럼 길이x 레코드 수’ 로 결정되므로 인덱스가 차지하는 면적은 테이블보다 훨씬 적다. 인덱스를 Range Scan 할 수 없을 때, 테이블 전체를 스캔하기보단 인덱스 전체를 스캔하는 것이 유리하다.
<br/>

```sql
create index emp_idx on emp (ename, sal);

select * from emp
where sal > 9000
order by ename;

Execution Plan
------------------------------------------------------------
0     SELECT STATEMENT Optimizer=ALL_ROWS
1   0   TABLE ACCESS (BY INDEX ROWID) OF 'EMP' (TABLE)
2   1     INDEX (FULL SCAN) OF 'EMP_IDX' (INDEX)
```
<br/>

이 방식은 차선책이므로 수행빈도가 높은 SQL이라면 선두 컬럼으로 인덱스를 생성해 주는 것이 좋다.
<br/>

**인덱스를 이용한 소트 연산 생략**  

인덱스를 Full Scan하면 Range Scan과 마찬가지로 결과집합이 인덱스 컬럼 순으로 정렬되므로 Sort Order By 연산을 생략할 목적으로 사용할 수도 있다. 이 때 차선책이 아닌 옵티마이저가 전략적으로 선택한 경우가 해당한다.
<br/>

```sql
select /*+ first_rows */ * from emp
where sal > 1000
order by ename;

Execution Plan
------------------------------------------------------------
0     SELECT STATEMENT Optimizer=ALL_ROWS
1   0   TABLE ACCESS (BY INDEX ROWID) OF 'EMP' (TABLE)
2   1     INDEX (FULL SCAN) OF 'EMP_IDX' (INDEX)
```

대부분의 사원이 SAL > 1000 조건을 만족하는 상황에서 Index Full Scan을 사용하면 Table Full Scan보다 불리하다. 위에서는 인덱스 힌트로 인해 옵티마이저 모드를 바꿨기 때문인데 소트 연산을 생략함으로써 전체 집합 중 처음 일부를 빠르게 출력할 목적으로 옵티마이저가 Index Full Scan을 선택한 것이다.  

이는 옵티마이저의 잘못이 아닌 힌트를 사용한 사용자의 잘못이다.  
<br/>

### Index Unique Scan

![Untitled (4)](https://github.com/Reffy08/TIL/assets/95058915/4d150cd4-6c0b-4756-8bb2-c1ae98699a92)
<br/>

수직적 탐색만으로 데이터를 찾는 스캔 방식으로 Unique 인덱스를 = 조건으로 탐색하는 경우 작동한다.  

Unique 인덱스가 존재하는 컬럼은 중복 값이 입력되지 않게 DBMS가 데이터 정합성을 관리해주기 때문에, 인덱스 키 컬럼 모두 = 조건으로 검색할 때는 데이터를 한건 찾는 순간 더 이상 탐색할 필요가 없다.  

Unique 인덱스라고 해도 범위검색(Between, Like)이나 일부 컬럼만으로 검색 시 Index Range Scan으로 처리된다.  
<br/>

### Index Skip Scan

![Untitled (5)](https://github.com/Reffy08/TIL/assets/95058915/1982eb2a-b0ff-4871-bbff-b40b261b0b5b)
<br/>

인덱스 선두 컬럼이 조건절에 없어도 인덱스를 활용하는 새로운 스캔 방식이다.

조건절에 빠진 인덱스 선두 컬럼의 Distinct Value 개수가 적고 후행 컬럼의 Distinct Value 개수가 많을 때 유용하다. (카디널리티가 낮은 컬럼)
<br/>

```sql
-- 인덱스 스캔 방식을 유도할 때 index_ss, 방지할 때 no_index_ss 사용
select /*+ index_ss(사원 사원_IDX) */ * 
from 사원 
where 연봉 between 2000 and 4000

Execution Plan
------------------------------------------------------------
0     SELECT STATEMENT Optimizer=ALL_ROWS
1   0   TABLE ACCESS (BY INDEX ROWID) OF '사원' (TABLE)
2   1     INDEX (SKIP SCAN) OF '사원_IDX' (INDEX)
```
<br/>

Index Skip Scan은 루트 또는 브랜치 블록에서 읽은 컬럼 값 정보를 이용해 조건절에 부합하는 레코드를 포함할 가능성이 있는 리프 블록만 골라서 액세스 하는 방식이다.  
<br/>

**Index Skip Scan이 작동하기 위한 조건**  

인덱스 선두 컬럼이 없을 때만 Index Skip Scan이 동작하는 것은 아니다.  

중간컬럼(=업종코드)에 대한 조건절이 없는 경우에도 Skip Scan을 사용할 수 있다.  
<br/>

```sql
-- 인덱스 [업종유형코드 + 업종코드 + 기준일자]
select /*+ INDEX_SS(A index1_pk) */
        기준일자, 업종코드, 체결건수, 체결수량, 거래대금
from 일별업종거래 A
where 업종유형코드='01'
and 기준일자 between '20200501' and '20200531'
```
<br/>

Index Range Scan을 사용한다면, 업종유형코드 = ‘01’인 인덱스 구간을 모두 스캔해야하지만, Skip Scan을 사용한다면 업종유형코드 = ‘01’인 구간에서 기준일자의 범위에 포함할 가능성이 있는 리프블록만 골라서 액세스할 수 있다.  

선두 컬럼이 부등호, BETWEEN, LIKE 같은 범위검색 조건일 때도 Index Skip Scan을 사용할 수 있다.  
<br/>

```sql
-- 인덱스 [기준일자 + 업종유형코드]
select /*+ INDEX_SS(A index1_pk) */
        기준일자, 업종코드, 체결건수, 체결수량, 거래대금
from 일별업종거래 A
where 기준일자 between '20080501' and '20080531' 
and 업종유형코드='01'
```
<br/>

만약 Range Scan을 사용한다면, 기준일자 BETWEEN 조건을 만족하는 인덱스 구간을 모두 스캔해야 한다. Skip Scan을 사용한다면, 기준일자 BETWEEN 조건을 만족하는 인덱스 구간에서 업종유형코드 = ‘01’ 인 레코드를 포함할 가능성이 있는 리프 블록만 골라서 액세스 할 수 있다.  

인덱스는 최적의 Index Range Scan을 목표로 설계하되, 차선책으로 스캔 방식을 사용해야 한다.  
<br/>

### Index Fast Full Scan

논리적인 인덱스 트리 구조를 무시하고 인덱스 세그먼트 전체를 Multi Block I/O 방식으로 스캔하기 때문에 Index Full Scan보다 빠르다.   
<br/>

**논리적 순서로 배치한 블록(Index Full Scan)**  

![Untitled (6)](https://github.com/Reffy08/TIL/assets/95058915/920ac62f-25bf-49fe-bcb2-92aa634416bb)
<br/>

**물리적 순서로 배치한 블록(Index Fast Full Scan)**  

![Untitled (7)](https://github.com/Reffy08/TIL/assets/95058915/1ecd5055-b810-4207-929b-6d4780412a20)
<br/>

| Index Full Scan | Index Fast Full Scan |
| :-- | :-- |
| 1. 인덱스 구조를 따라 스캔 | 1. 세그먼트 전체를 스캔 |
| 2. 결과 집합 순서 보장 | 2. 결과 집합 순서 보장 안됨 |
| 3. Single Block I/O | 3. Multi Block I/O |
| 4. (파티션 돼 있지 않다면) 병렬 스캔 불가 | 4. 병렬 스캔 가능 |
| 5. 인덱스에 포함되지 않은 컬럼 조회 시에도 사용 가능 | 5. 인덱스에 포함된 컬럼으로만 조회할 때 사용 가능 |
<br/>

### Index Range Scan Descending

![Untitled (8)](https://github.com/Reffy08/TIL/assets/95058915/cc76dde8-8d83-48a2-a136-036566497ac9)
<br/>

Index Range Scan과 기본적으로 동일한 스캔 방식으로, 인덱스를 뒤에서부터 앞쪽으로 스캔하기 때문에 내림차순으로 정렬된 결과 집합을 얻는다는 점만 다르다.

<br/>
<br/>
<br/>
<br/>
<br/>

### 참조
- 친절한 SQL 
