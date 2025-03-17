# 옵티마이저와 힌트

MySQL 서버로 요청된 쿼리는 결과는 동일하지만 내부적으로 그 결과를 만들어내는 방법은 다양하다.  
이 방법들 중 쿼리를 최적으로 실행하기 위해 각 테이블의 데이터가 어떤 분포로 저장돼 있는지 통계 정보를 참조하며 기본 데이터를 비교해 최적의 실행 계획을 수립하는 작업이 필요하다.  
이 기능을 DBMS에서는 옵티마이저가 담당한다.  

MySQL 에서는 EXPLAIN 명령으로 쿼리의 실행 계획을 확인할 수 있는데, 이해를 위해서는 옵티마이저가 실행하는 최적화에 대해서 어느 정도 지식이 필요하다.  

<br/>
<br/>

## 개요

어떤 DBMS든지 쿼리의 실행 계획을 수립하는 옵티마이저는 가장 복잡한 부분으로 알려져 있으며, 옵티마이저가 만들어 내는 실행 계획을 이해하는 것 또한 어려운 부분이다.  
하지만 실행 계획을 이해할 수 있어야만 실행 계획의 불합리한 부분을 찾아내고, 더 최적화된 방법으로 실행 계획을 수립하도록 유도할 수 있다.  

<br/>
<br/>

### 쿼리 실행 절차 

#### MySQL 서버에서 쿼리가 실행되는 과정
1. 사용자로부터 요청된 SQL 문장을 잘게 쪼개서 MySQL 서버가 이해할 수 있는 수준으로 분리/파스 트리한다.  
2. SQL의 파싱 정보(파스 트리)를 확인하면서 어떤 테이블로부터 읽고 어떤 인덱스를 이용해 테이블을 읽을지 선택한다.  
3. 두 번째 단계에서 결정된 테이블의 읽기 순서나 선택된 인덱스를 이용해 스토리지 엔진으로부터 데이터를 가져온다.  

첫 번째 단계를 "SQL 파싱(Parsing)"이라고 하며, MySQL 서버의 SQL파서라는 모듈로 처리한다. SQL 문장이 문법적으로 잘못됐다면 이 단계에서 걸러진다.  
이 단계에서 SQL 파스 트리가 만들어지며, MySQL 서버는 SQL 문장 그 자체가 아니라 SQL 파스 트리를 이용해 쿼리를 실행한다.  

두 번째 단계는 SQL 파스 트리를 참조하면서 다음 내용을 처리한다.  
- 불필요한 조건 제거 및 복작한 연산의 단순화 
- 여러 테이블의 조인이 있는 경우 어떤 순서로 테이블을 읽을 지 결정
- 각 테이블에 사용된 조건과 인덱스 통계 정보를 이용해 사용할 인덱스 결정
- 가져온 레코드들을 임시 테이블에 넣고 다시 한번 가공해야 하는지 결정  

<br/>

두 번째 단계는 "최적화 및 실행 계획 수립" 단계이며, 옵티마이저에서 처리한다. 또한 두 번째 단계가 완료되면서 쿼리의 실행 계획이 만들어진다.  

세 번째 단계에서는 수립된 실행 계획대로 스토리지 엔진이 레코드를 읽어오도록 요청하고, MySQL 엔진에서는 스토리지 엔진으로부터 받은 레코드를 조인하거나 정렬하는 작업을 수행한다.  

첫 번째, 두 번째 단계는 거의 MySQL 엔진에서 처리하며, 세 번째 단계는 MySQL 엔진과 스토리지 엔진이 동시에 참여해서 처리한다.  

<br/>
<br/>

### 옵티마이저 종류 

옵티마이저는 데이터베이스 서버에서 두뇌와 같은 역할을 담당한다.  
옵티마이저는 현재 대부분 DBMS가 선택하고 있는 비용 기반 최적화(Cost-based optimizer, CBO) 방법과 예전 초기 오라클 DBMS에서 많이 사용했던 규칙 기반 최적화 방법(Rule-based optimizer, RBO)으로 크게 나눌 수 있다.  

#### **규칙 기반 최적화**  
기본적으로 대상 테이블의 레코드 건수나 선택도 등을 고려하지 않고 옵티마이저에 내장된 우선순위에 따라 실행 계획을 수립하는 방식을 의미한다.  
이 방식에서는 통계 정보(테이블의 레코드 건수나 컬럼값의 분포도)를 조사하지 않고 실행 계획이 수립되기 때문에 같은 쿼리에 대해서는 거의 항상 같은 실행 방법을 만들어 낸다.  
하지만 사용자의 데이터는 분포도가 매우 다양하기 때문에 규칙 기반의 최적화는 이미 오래전부터 많은 DBMS에서 사용되지 않는다.  
각 테이블이나 인덱스의 통꼐 정보가 거의 없고 상대적으로 느린 CPU 연산 탓에 비용 계산 과정이 부담스럽다는 이유로 사용되던 방법이다.  

<br/>

#### **비용 기반 최적화**  

쿼리를 처리하기 위한 여러 가지 방법을 만들고, 각 단위 작업의 비용(부하) 정보와 대상 테이블의 예측된 통계 정보를 이용해 실행 계획별 비용을 산출한다. 이렇게 산출된 실행 방법별로 비용이 최소로 소요되는 처리 방식을 선택해 최종적으로 쿼리를 실행한다.  

<br/>
<br/>

## 기본 데이터 처리

모든 RDBMS는 데이터를 정렬하거나 그루핑하는 등의 기본 데이터 가공 기능을 가지고 있지만, 결과물은 동일하더라도 RDBMS별로 그 결과를 만들어 내는 과정은 천차만별이다.  

<br/>
<br/>

### 풀 테이블 스캔과 풀 인덱스 스캔

풀 테이블 스캔은 인덱스를 사용하지 않고 테이블의 데이터를 처음부터 끝까지 읽어서 요청된 작업을 처리하는 작업을 의미한다.  
옵티마이저는 다음 조건일 때 주로 풀 테이블 스캔을 선택한다.  

- 테이블의 레코드 건수가 너무 작아서 인덱스를 통해 읽는 것보다 풀 테이블 스캔을 하는 편이 더 빠른 경우(일반적으로 테이블이 페이지 1개로 구성된 경우) 
- WHERE 절이나 ON 절에 인덱스를 이용할 수 없는 경우
- 인덱스 레인지 스캔을 사용할 수 있는 쿼리라고 하더라도 옵티마이저가 판단한 조건 일치 레코드 건수가 너무 많은 경우(인덱스의 B-Tree를 샘플링해서 조사한 통계 정보 기준)  

<br/>

일반적으로 테이블의 전체 크기는 인덱스보다 훨씬 크기 때문에 테이블을 처음부터 끝까지 읽는 작업은 상당히 많은 디스크 읽기가 필요하다.  
대부분의 DBMS는 풀 테이블 스캔을 실행할 때 한꺼번에 여러 개의 블록이나 페이지를 읽어오는 기능을 내장하고 있다.  
하지만 MySQL에는 풀 테이블 스캔을 실행할 때 한꺼번에 몇 개씩 페이지를 읽어올지 설정하는 시스템 변수는 없다. 그래서 많은 사용자는 MySQL은 풀 테이블 스캔을 실행할 때 디스크로부터 페이지를 하나씩 읽어오는 것으로 생각한다.  

InnoDB 스토리지 엔진은 특정 테이블의 연속된 데이터 페이지가 읽히면 백그라운드 스레드에 의해 리드 어헤드(Read ahead)작업이 자동으로 시작된다. 리드 어헤드란 어떤 영역의 데이터가 앞으로 필요해지리라는 것을 예측해서 요청이 오기 전에 미리 디스크에서 읽어 InnoDB의 버퍼 풀에 가져다 두는 것을 의미한다.  
즉, 풀 테이블 스캔이 실행되면 처음 몇 개의 데이터 페이지는 포그라운드 스레드(클라이언트 스레드)가 페이지 읽기를 실행하지만 특정 시점부터는 읽기 작업을 백그라운드 스레드로 넘긴다.  
백그라운드 스레드가 읽기를 넘겨받는 시점부터는 한 번에 4개 또는 8개의 페이지를 읽으면서 계속 그 수를 증가시킨다. 이 때 한 번에 최대 64개의 데이터 페이지까지 읽어서 버퍼 풀에 저장해 둔다.  
포그라운드 스레드는 버퍼 풀에 준비된 데이터를 가져다 사용하기만 하면 되므로 쿼리가 상당히 빨리 처리된다.  

`innodb_read_ahead_threahold` 시스템 변수로 언제 리드 어헤드를 시작할지 임계값을 설정할 수 있다.  
포그라운드 스레드에 의해 시스템 변수에 설정된 개수만큼의 연속된 데이터 페이지가 읽히면 InnoDB 스토리지 엔진은 백그라운드 스레드를 이용해 대량으로 그 다음 페이지들을 읽어서 버퍼 풀로 적재한다.  

리드 어헤드는 풀 테이블 스캔에서만 사용되는 것이 아닌 풀 인덱스 스캔에서도 동일하게 사용된다.  

`SELECT COUNT(*) FROM employees;`

이 쿼리는 아무 조건없이 테이블을 조회하므로 풀 테이블 스캔을 할 것으로 보이지만, 실제 실행 계획은 풀 인덱스 스캔을 할 가능성이 높다.  
예제 쿼리처럼 레코드의 건수만 필요로 하는 쿼리라면 용량이 작은 인덱스를 선택하는 것이 디스크 읽기 횟수를 줄일 수 있기 때문이다.  
일반적으로 인덱스는 테이블의 특정 컬럼만으로 구성되기 때문에 테이블 자체보다는 용량이 작아서 훨씬 빠른 처리가 가능하다.  
모든 컬럼을 요구하는 조회 쿼리의 경우 풀 테이블 스캔을 사용한다.  

<br/>
<br/>

### 병렬 처리

8.0 버전부터는 용도가 한정돼 있긴 하지만 처음으로 MySQL 서버에서도 쿼리의 병렬 처리가 가능해졌다.  

병렬 처리란 하나의 쿼리를 여러 스레드가 작업을 나누어 동시에 처리하는 것을 의미한다.  
여러 스레드가 동시에 각각 쿼리를 처리하는 것은 MySQL 서버가 처음 만들어질 때부터 가능했다.  

`innodb_parallel_read_threads` 시스템 변수를 이용해 하나의 쿼리를 최대 몇개의 스레드를 이용해서 처리할지를 변경할 수 있다.  
아직 MySQL 서버에서는 쿼리를 여러 개의 스레드를 이용해 병렬로 처리하게 하는 힌트나 옵션은 없다. 8.0 버전에서는 아무런 WHERE 조건 없이 단순히 테이블의 전체 건수를 가져오는 쿼리만 병렬로 처리할 수 있다.  

```sql
SET SESSION innodb_parallel_read_threads=1;
SELECT COUNT(*) FROM salaries;

SET SESSION innodb_parallel_read_threads=2;
SELECT COUNT(*) FROM salaries;

SET SESSION innodb_parallel_read_threads=4;
SELECT COUNT(*) FROM salaries;
```

쿼리 실행 결과를 보면 병렬 처리용 스레드 개수가 늘어날수록 쿼리 처리에 걸리는 시간이 감소하는 것을 확인할 수 있다.  
병렬 처리용 스레드 개수는 서버에 장착된 CPU 코어 개수를 넘어서는 경우 성능이 떨어질 수 있다.  

<br/>
<br/>

### ORDER BY 처리(Using filesort)

레코드 1~2건을 가져오는 쿼리를 제외하면 대부분의 SELECT 쿼리에서 정렬은 필수적으로 사용된다.  
데이터 웨어하우스처럼 대량의 데이터를 조회해서 일괄 처리하는 기능이 아니라면 레코드 정렬 요건은 대부분의 조회 쿼리에 포함돼 있을 것이다.  
정렬 방법은 인덱스 이용과, "Filesort"라는 별도의 처리를 이용하는 방법으로 나눌 수 있다.

<br/>

<table>
    <thead>
        <tr>
            <th style="width: 10%;"></th>
            <th style="width: 45%;">장점</th>
            <th style="width: 45%;">단점</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>인덱스 이용</th>
            <td>INSERT, UPDATE, DELETE 쿼리가 실행될 때 이미 인덱스가 정렬돼 있어서 순서대로 읽기만 하면 되므로 매우 빠르다.</td>
            <td>INSERT, UPDATE, DELETE 작업 시 부가적인 인덱스 추가/삭제 작업이 필요하므로 느리다.<br/> 인덱스 때문에 디스크 공간이 더 많이 필요하다.<br/> 인덱스 개수가 늘어날수록 InnoDB 버퍼 풀을 위한 메모리가 많이 필요하다.</td>
        </tr>
    </tbody>
    <tbody>
        <tr>
            <td>Filesort 이용</th>
            <td>인덱스를 생성하지 않아도 되므로 인덱스를 이용할 때의 단점이 장점으로 바뀐다.<br/>정렬해야 할 레코드가 많지 않으면 메모리에서 Filesort가 처리되므로 충분히 빠르다.</td>
            <td>정렬 작업이 쿼리 실행 시 처리되므로 레코드 대상이 건수가 많아질수록 쿼리의 응답 속도가 느리다.</td>
        </tr>
    </tbody>
</table>

<br/>

레코드를 정렬하기 위해 항상 Filesort라는 정렬 작업을 거쳐야 하는 것은 아니다.  

다음 이유들로 모든 정렬을 인덱스를 이용하도록 튜닝하기란 불가능하다.  
- 정렬 기준이 너무 많아서 요건별로 모두 인덱스를 생성하는 것이 불가능한 경우
- GROUP BY의 결과 또는 DISTINCT 같은 처리의 결과를 정렬해야 하는 경우
- UNION의 결과와 같이 임시 테이블의 결과를 다시 정렬해야 하는 경우
- 랜덤하게 결과 레코드를 가져와야 하는 경우  

<br/>

MySQL 서버에서 인덱스를 이용하지 않고 별도의 정렬 처리를 수행했느지는 실행 계획의 Extra 컬럼의 "Using Filesort" 메시지가 표시되는지 여부로 판단할 수 있다.  

<br/>
<br/>

## 소트 버퍼

MySQL이 정렬을 수행하기 위해 사용하는 별도의 메모리 공간을 소트 버퍼(Sort Buffer)라고 한다.  
소드 버퍼는 정렬이 필요한 경우에만 할당되며, 버퍼의 크기는 정렬해야 할 레코드의 크기에 따라 가변적으로 증가하지만 최대 사용 가능한 소트 버퍼의 공간은 `sort_buffer_size`라는 시스템 변수로 설정할 수 있다.  
소트 버퍼를 위한 메모리 공간은 쿼리의 실행이 완료되면 즉시 시스템으로 반납된다.  

정렬이 문제가 되는 이유는 정렬해야 할 레코드가 아주 소량이어서 메모리에 할당된 소트 버퍼만으로 정렬할 수 있다면 아주 빠르게 정렬이 처리될 것이다.  

하지만 정렬해야 할 레코드의 건수가 소트 버퍼로 할당된 공간보다 크다면 MySQL은 정렬해야 할 레코드를 여러 조각으로 나눠서 처리하는데, 이 과정에서 임시 저장을 위해 디스크를 사용한다.
메모리의 소트 버퍼에서 정렬을 수행하고, 그 결과를 임시로 디스크에 기록해 둔다. 그리고 다음 레코드를 가져와서 다시 정렬해서 반복적으로 디스크에 임시 저장한다.  
이처럼 각 버퍼 크기만큼 정렬된 레코드를 다시 병합하면서 정렬을 수행해야 한다. 
이 병합 작업을 멀티 머지(Multi-merge)라고 하며, `SHOW STATUS LIKE 'Sort_merge_passes'` 상태 변수에 누적해서 집계된다. 

이 작업들이 모두 디스크의 쓰기와 읽기를 유발하며, 레코드 건수가 클수록 작업 횟수가 많아진다.  
소트 버퍼를 크게 설정하면 디스크를 사용하지 않아 빨라질 것으로 생각하는데, 실제 벤치마크 결과로는 큰 차이가 없었다.  

개인용 PC에서 실행해본 벤치마크에서는 MySQL 소트 버퍼 크기가 256KB에서 8MB 사이에서 최적의 성능을 보였으며, 그 밖의 범위에서는 소트 버퍼 크기 변화에 대해 성능 효과가 보이지 않았다.  

![sort-buffer-graph](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FDeZRe%2FbtrH1CpjvLK%2F0LkIfuHj50gXOv5dXONsf1%2Fimg.png)  

`sort_buffer_size` 시스템 변수의 설정값이 무조건 크면 메모리에서 모두 처리되니 빨라질 것으로 예상하지만 실제 결과는 그렇지 않다.  
그리고 리눅스 계열의 운영체제에서 너무 큰 소트 버퍼 사이즈를 사용하면, 큰 메모리 공간 할당 때문에 성능이 떨어질 수 있다.  

MySQL은 글로벌 메모리 영역과 세션(로컬) 메모리 영역으로 나눠서 생각할 수 있는데, 소트 버퍼는 세션 메모리 영역에 해당한다. 
즉, 소트 버퍼는 여러 클라이언트가 공유해서 사용할 수 있는 영역이 아니다.  
커넥션이 많으면 많을수록, 정렬 작업이 많으면 많을수록 소트 버퍼로 소비되는 메모리 공간이 커짐을 의미한다.  
소트 버퍼를 너무 크게 설정하면 OS는 메모리 부족 현상을 겪을 수도 있다.  

소트 버퍼를 크게 설정해서 빠른 성능을 얻을 수는 없지만 디스크의 읽기와 쓰기 사용량을 줄일 수 있는데, 서버 메모리가 부족할 수 있으므로 적절히 선택하는 것이 좋다.  

<br/>
<br/>

### 정렬 알고리즘

레코드를 정렬할 때 레코드 전체를 소트 버퍼에 담을지 또는 정렬 기준 컬럼만 소트 버퍼에 담을지에 따라 "싱글 패스(Single-pass)"와 "투 패스(Two-pass)" 2가지 정렬 모드로 나눌 수 있다.(공식 명칭 아님)  
정렬 수행 쿼리가 어떤 정렬 모드를 사용하는지 옵티마이저 트레이스 기능으로 확인할 수 있다.  

```sql
-- 옵티마이서 트레이스 활성화
SET OPTIMIZER_TRACE="enabled_on", END_MARKERS_IN_JSON=on;
SET OPTIMIZER_TRACE_MAX_MEM_SIZE=1000000;

-- 쿼리 실행
SELECT * FROM employees ORDER BY last_name LIMIT 100000, 1;

-- 트레이스 내용 확인
SELECT * FROM INFORMATION_SCHEMA.OPTIMIZER_TRACE \G
```

출력 내용에서 filesort_summary 섹션의 sort_algorithm 필드에 정렬 알고리즘이 표시되고 sort_made 필드에는 `<fixed_sort_key, packed_additional_fields>`가 표시된 것을 확인할 수 있다.  

#### MySQL 서버의 정렬 방식
- `<sort_key, rowid>`: 정렬 키와 레코드의 로우 아이디만 가져와서 정렬하는 방식
- `<sort_key, additional_fields>`: 정렬 키와 레코드 전체를 가져와서 정렬하는 방식으로, 레코드의 컬럼들은 고정 사이즈로 메모리 저장
- `<sort_key, packed_additional_fields>`: 정렬 키와 레코드 전체를 가져와서 정렬하는 방식으로, 레코드의 컬럼들은 가변 사이즈로 메모리에 저장  

<br/>

첫 번째 방식을 "투 패스", 두 번째와 세 번째 방식을 "싱글 패스" 정렬 방식이라고 명명한다.  
5.7 버전부터 세 번째 방식이 도입됐는데, 이는 정렬을 위한 메모리 공간의 효율적인 사용을 위해서 추가로 도입된 방식이다.  

<br/>
<br/>

### 싱글 패스 정렬 방식

소트 버퍼에 정렬 기준 컬럼을 포함해 SELECT 대상이 되는 컬럼 전부를 담아서 정렬을 수행하는 정렬 방법이다.  

```sql
SELECT * FROM emp_no, first_name, last_name
FROM employees
ORDER BY first_name;
```

위 쿼리를 싱글 패스 정렬 방식으로 처리하는 절차를 보면 그림과 같다.  

![single-pass](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Ft0qJW%2FbtrX1ReAkyZ%2F2YLj7mG0x1tzCCcuYtNJMK%2Fimg.png)  

처음 employees 테이블을 읽을 때 정렬이 필요하지 않은 last_name 컬럼까지 전부 읽어서 소트 버퍼에 담고 정렬을 수행한다. 그리고 정렬이 완료되면 정렬 버퍼의 내용을 그대로 클라이언트로 넘겨주는 과정을 볼 수 있다.  

<br/>
<br/>

### 투 패스 정렬 방식

정렬 대상 컬럼과 프라이머리 키 값만 소트 버퍼에 담아서 정렬을 수행하고, 정렬된 순서대로 다시 프라이머리 키로 테이블을 읽어서 SELECT할 컬럼을 가져오는 정렬 방식으로, 싱글 패스 정렬 방식의 도입 이전부터 사용하던 방식이다.  
8.0에서도 특정 조건에서는 투 패스 정렬 방식을 사용한다.  

![two-pass](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FwPTZm%2FbtrX3ry8Fqz%2FlLe5W20R82q5oqwc6eM4O1%2Fimg.png)  

같은 쿼리를 투 패스 정렬 방식으로 정렬했을 때 처음 테이블을 읽을 때는 정렬에 필요한 first_name 컬럼과 프라이머리 키인 emp_no만 읽어서 정렬을 수행한다.  
이 정렬이 완료되면 그 결과 순서대로 테이블을 한번 더 읽어서 last_name을 가져오고, 최종적으로 그 결과를 클라이언트 쪽으로 넘긴다.  

투 패스 방식은 테이블을 두 번 읽어야 하므로 불합리 하지만 새로운 정렬 방식인 싱글 패스는 이런 불합리가 없다.  
하지만 싱글 패스 정렬 방식은 더 많은 소트 버퍼 공간이 필요하다. 즉, 대략 128KB의 정렬 버퍼를 사용한다면 이 쿼리는 투 패스 정렬 방식에서는 대략 7000건의 레코드를 정렬할 수 있지만 싱글 패스 방식에서는 그것의 반 정도밖에 정렬할 수 없다.  
이는 소트 버퍼 공간의 크기와 레코드의 크기에 의존적이다.  

최근 버전에서는 주로 싱글 패스 정렬 방식을 주로 사용하지만 다음의 경우에는 투 패스 방식을 사용한다.  
- 레코드의 크기가 `max_length_for_sort_data` 시스템 변수에 설정된 값보다 클 때 
- BLOB이나 TEXT 타입의 컬럼이 SELECT 대상에 포함될 때  

<br/>

싱글 패스 방식은 정렬 대상 레코드의 크기나 건수가 작은 경우 빠른 성능을 보이며, 투 패스 방식은 정렬 대상 레코드의 크기나 건수가 상당히 많은 경우 효율적이라고 볼 수 있다.  

<br/>
<br/>

## 정렬 처리 방법

ORDER BY가 사용되면 다음 3가지 처리 방법중 하나로 정렬이 처리된다. (아래쪽일수록 처리 속도가 떨어짐)

정렬 처리 방법 | 실행 계획의 Extra 컬럼 내용
:--| :--
인덱스를 사용한 정렬 | 별도 표기 없음
조인에서 드라이빙 테이블만 정렬 | "Using filesort" 메시지가 표시됨
조인에서 조인 결과를 임시 테이블로 저장 후 정렬 | "Using temporary; Using filesort" 메시지가 표시됨

<br/>

옵티마이저는 정렬 처리를 위해 인덱스를 이용할 수 있을지 검토할 것이다. 인덱스를 이용할 수 있다면 별도의 Filesort 과정 없이 인덱스를 순서대로 읽어서 결과를 반환한다.  
인덱스를 사용할 수 없다면 WHERE 조건에 일치하는 레코드를 검색해 정렬 버퍼에 저장하면서 정렬을 처리(Filesort)할 것이다.  

이 때 옵티마이저는 정렬 대상 레코드를 최소화하기 위해 다음을 수행한다. 
- 조인의 드라이빙 테이블만 정렬한 다음 조인을 수행
- 조인이 끝나고 일치하는 레코드를 모두 가져온 후 정렬을 수행  

<br/>

일반적으로 조인이 수행되면서 레코드 건수와 레코드 크기는 거의 배수로 불어나기 때문에 가능하다면 드라이빙 테이블만 정렬한 다음 조인을 수행하는 방법이 효율적이다. (첫 번째 방법이 더 효율적)  

<br/>
<br/>

### 인덱스를 이용한 정렬

인덱스를 이용한 정렬을 위해서는 ORDER BY에 명시된 컬럼이 제일 먼저 있는 테이블에 (조인이 사용된 경우 드라이빙 테이블)애 속하고, ORDER BY의 순서대로 생성된 인덱스가 있어야 한다.  
또한 WHERE절에 선두 컬럼에 대한 조건이 잇다면 그 조건과 ORDER BY는 같은 인덱스를 사용할 수 있어야 한다.  
B-Tree가 아닌 해시 인덱스나 전문 검색 인덱스 등에서는 인덱스를 이용한 정렬을 사용할 수 없다. (R-Tree도 사용x)  
여러 테이블이 조인되는 경우 NL조인 방식에서만 사용 가능하다.  

인덱스를 이용해 정렬되는 경우 실제 인덱스 값이 정렬되어 있기 때문에 인덱스의 순서대로 읽기만 하면 되기 때문에 MySQL 엔진에서 별도의 정렬을 위한 추가 작업을 하지 않는다.  

```sql
-- emp_no 컬럼으로 정렬이 필요한데 인덱스로 자동 정렬된다고 ORDER BY 컬럼을 제거하는 것은 좋지 않은 선택이다. 
SELECT *
FROM employees e JOIN salaries s
ON s.emp_no = e.emp_no
WHERE e.emp_no BETWEEN 10002 AND 10020
ORDER BY e.emp_no;
```

![index-sort](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FvcrMh%2FbtrH2nyvk4v%2F08C0poqdFJnJST10aO5sG0%2Fimg.png)  

MySQL 서버는 정렬을 인덱스로 처리할 수 있는 경우 부가적으로 불필요한 정렬 작업을 수행하지 않으므로 ORDER BY절을 명시한다고 해서 작업량이 더 늘지는 않는다.  

B-Tree 인덱스가 키 값으로 정렬되어 있고 NL조인 방식으로 실행되기 때문에 인덱스의 읽기 순서가 흐트러지지 않는데, 실행 계획에 조인 버퍼(join buffer)가 사용되면 순서가 흐트러질 수 있기 때문에 주의해야 한다.  

<br/>
<br/>

### 조인의 드라이빙 테이블만 정렬

조인이 수행되면 결과 레코드의 건수가 몇 배로 불어나고, 레코드 하나하나의 크기도 늘어나기 때문에 조인 실행 전에 첫 번째 테이블의 레코드를 먼저 정렬한다음 조인을 실행하는 것이 정렬의 차선책이 될 것이다. 이 방법으로 처리되려면 첫 번째로 읽히는 테이블(드라이빙 테이블)의 컬럼만으로 ORDER BY 절을 작성해야 한다.  

```sql
SELECT *
FROM employees e JOIN salaries s
ON s.emp_no = e.emp_no -- 3
WHERE e.emp_no BETWEEN 10002 AND 10020 -- 1
ORDER BY e.last_name; -- 2
```

옵티마이저는 다음 두 조건을 갖추었기 때문에 employees 테이블을 드라이빙 테이블로 선택할 것이다.  

- WHERE 절의 조건(emp_no BETWEEN ...)은 employees 테이블의 PK를 이용해 검색하면 작업량을 줄일 수 있다. 
- 드리븐 테이블(salaries)의 조인 컬럼인 emp_no 컬럼에 인덱스가 있다.  

<br/>

검색은 인덱스 레인지 스캔으로 처리할 수 있지만 ORDER BY 절에 명시된 컬럼은 employees 테이블의 PK와 연관이 없으므로 인덱스를 이용한 정렬은 불가능 하다.  
하지만 정렬 기준 컬럼(last_name)이 드라이빙 테이블에 포함된 컬럼이므로 옵티마이저는 드라이빙 테이블만 검색해서 정렬을 수행하고, 그 결과와 드리븐 테이블을 조인한다.  

![driving-table-join](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbGprhJ%2FbtrH3ccBYJH%2FEzr19Fo0tH6NhNRPTsBxM0%2Fimg.png)  

<br/>
<br/>

### 임시 테이블을 이용한 정렬

쿼리가 여러 테이블을 조인하지 않고 하나의 테이블로부터 SELECT 해서 정렬하는 경우라면 임시 테이블이 필요하지 않지만, 2개 이상의 테이블을 조인해서 그 결과를 정렬해야 한다면 임시 테이블이 필요할 수도 있다.  

앞의 예제는 임시 테이블을 사용하지 않지만, 그 외의 패턴의 쿼리에서는 항상 조인의 결과를 임시 테이블에 저장하고, 그 결과를 다시 정렬하는 과정을 거친다.  
이 방법은 정렬해야 할 레코드 건수가 가장 많기 때문에 정렬 방법중 가낭 느리다.  

```sql
SELECT *
FROM employees e JOIN salaries s
ON s.emp_no = e.emp_no
WHERE e.emp_no BETWEEN 10002 AND 10020
ORDER BY s.salary;
```

이 쿼리에서 ORDER BY 절의 정렬 기준 컬럼이 드라이빙 테이블이 아닌 드리븐 테이블에 있는 컬럼이다. 즉, 정렬이 수행되기 전에 salaries 테이블을 읽어야 하므로 이 쿼리는 조인된 데이터를 가지고 정렬할 수 밖에 없다.  

실행 계획을 보면 Extra 컬럼에 "Using temporary; Using filesort"가 출력되며, 조인 결과를 임시 테이블에 저장 후 결과를 다시 정렬했을을 알 수 있다.  

![temp-table-sort](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F5nwRW%2FbtrH1bevi8S%2FaNMp2dUgzGGo9hqUDkhKM0%2Fimg.png)  

<br/>
<br/>

### 정렬 처리 방법의 성능 비교

일반적으로 LIMIT은 테이블이나 처리 결과의 일부만 가져오기 때문에 MySQL 서버가 처리해야 할 작업량을 줄이는 역할을 한다.  
하지만 ORDER BY, GROUP BY 같은 작업은 조건절을 만족하는 레코드를 LIMIT 건수만큼 가져와서는 처리할 수 없고, 우선 조건을 만족하는 레코드를 모두 가져와서 작업을 실행해야만 LIMIT으로 건수를 제한할 수 있다.  
WHERE 조건이 인덱스를 활용하도록 튜닝해도 ORDER BY, GROUP BY 때문에 쿼리가 느려지는 경우가 자주 발생한다.  

<br/>
<br/>

#### 스트리밍 방식  

![streming](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FE4SKj%2FbtrX311ldBY%2FJuhXKcy0069morRBoGrco1%2Fimg.png)  

서버 쪽에서 처리할 데이터가 얼마인지에 관계없이 조건에 일치하는 레코드가 검색될 때마다 바로바로 클라이언트로 전송해주는 방식이다.  
이 방식으로 쿼리를 처리할 경우 클라이언트는 쿼리를 요청하고 곧바로 원했던 첫 번째 레코드를 전달받는다.  

그림처럼 쿼리가 스트리밍 방식으로 처리될 수 있다면 클라이언트는 MySQL 서버가 일치하는 레코드를 찾는 즉시 전달받기 때문에 동시에 데이터의 가공 작업을 시작할 수 있다.  
OLTP환경에서는 쿼리의 요청 후 응답 시간이 중요하다. 이 방식은 얼마나 많은 레코드를 조회하느냐에 관계없이 빠른 응답 시간을 보장해준다.  

스트리밍 방식으로 처리되는 쿼리에서 LIMIT 처럼 결과 건수를제한하는 조건들은 쿼리의 전체 실행 시간을 상당히 줄여줄 수 있다.  
매우 큰 테이블을 아무런 조건 없이 SELECT만 해보면 첫 번째 레코드는 아주 빨리 가져온다. 이는 풀 테이블 스캔의 결과가 아무런 버퍼링 처리나 필터링 과정 없이 바로 클라이언트로 스트리밍 되기 때문이다. LIMIT을 추가하면 가져오는 레코드 건수가 줄어들기 때문에 마지막 레코드를 가져오기 까지 시간을 상당히 줄일 수 있다.  

<br/>

#### 버퍼링 방식  

ORDER BY, GROUP BY 같은 처리는 쿼리의 결과가 스트리밍되는 것을 불가능하게 한다. 조건에 일치하는 레코드를 가져온 후 정렬하거나 그루핑해서 차례대로 보내야 하기 때문이다.  

MySQL 서버에서는 모든 레코드를 검색하고 정렬 작업을 하는 동안 클라이언트는 아무것도 하지 않고 기다려야 하기 때문에 응답 속도가 느려진다. (스트리밍의 반대이므로 버퍼링)  

![buffering](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbizGub%2FbtrX1QNyxgl%2FkAJtKaedGh1WyJBN6hknlK%2Fimg.png)  

그림처럼 버퍼링 방식으로 처리되는 쿼리는 먼저 결과를 모아서 MySQL 서버에서 일괄 가공해야 하므로 모든 결과를 스토리지 엔진으로부터 가져올 때 까지 기다려야 한다.  
그래서 버퍼링은 LIMIT처럼 결과 건수를 제한하는 조건이 있어도 성능 향상에 별 도움이 되지 않는다.  

인덱스를 사용한 정렬 방식은 LIMIT으로 제한된 건수만큼만 읽으면서 바로바로 클라이언트로 전송해줄 수 있지만, 인덱스를 사용하지 못하는 경우의 처리는 필요한 모든 레코드를 디스크로부터 읽어서 정렬한 후에 LIMIT으로 제한된 건수만큼 잘라서 클라이언트로 전송해줄 수 있다.  

<br/>
<br/>

### 정렬 관련 상태 변수

MySQL 서버는 처리하는 주요 작업에 대해서는 해당 작업의 실행 횟수를 상태 변수로 저장한다.  
지금까지 정렬과 관련해서도 몇 건의 레코드나 정렬 처리를 수행했는지, 소트 버퍼 간의 병합 작업(멀티 머지)은 몇 번이나 발생했는지 등을 확인할 수 있다.  

```sql
FLUSH STATUS; -- 현제 세션의 상태 값 초기화
SHOW STATUS LIKE 'Sort%';
```
- Sort_merge_passes: 멀티 머지 처리 횟수를 의미한다.
- Sort_range: 인덱스 레인지 스캔을 통해 검색된 결과에 대한 정렬 작업 횟수다.
- Sort_scan은 풀 테이블 스캔을 통해 검색된 결과에 대한 정렬 작업 횟수다. Sort_scan, Sort_range는 둘 다 정렬 작업 횟수를 누적하고 있는 상태 값이다.  
- Sort_row: 지금까지 정렬한 전체 레코드 건수를 의미한다.  

<br/>
<br/>

## GROUP BY 처리

스트리밍된 처리를 할 수 없게 하는 처리 중 하나다.  
GROUP BY에 사용된 조건은 인덱스를 사용해서 처리될 수 없으므로 HAVING 절을 튜닝하려고 인덱스를 생성하거나 다른 방법을 고민할 필요는 없다.  

GROUP BY절도 인덱스를 이용할 때는 인덱스를 차례대로 읽는 인덱스 스캔 방법과 인덱스를 건너뛰면서 읽는 루스 인덱슷 스캔이라는 방법으로 나뉜다.  
인덱스를 사용하지 못하는 쿼리의 GROUP BY 작업은 임시 테이블을 사용한다.  

<br/>
<br/>

### 인덱스 스캔을 이용하는 GROUP BY(타이트 인덱스 스캔)

ORDER BY의 경우와 마찬가지로 조인의 드라이빙 테이블에 속한 컬럼만 이용해 그루핑할 때 GROUP BY 컬럼으로 이미 인덱스가 있다면 그 인덱스를 차례대로 읽으면서 그루핑 작업을 수행하고 그 결과로 조인을 처리한다.  
GROUP BY가 인덱스를 사용해서 처리된다 하더라도 집계 함수 등의 그룹값을 처리해야 해서 임시 테이블이 필요할 때도 있다.  

<br/>
<br/>

### 루스 인덱스 스캔을 이용하는 GROUP BY 

루스(Loose) 인덱스 스캔은 인덱스의 레코드를 건너뛰면서 필요한 부분만 읽어서 가져오는 것을 의미하는데, 옵티마이저가 이를 사용할 때는 실행계획의 Extra 컬럼에 "Using index for group-by" 코멘트가 표시된다.  

```sql
EXPLAIN
SELECT emp_no
FROM salaries
WHERE from_date = '1985-03-01'
GROUP By emp_no;
```

salaries 테이블의 인덱스는 (emp_no, from_date)로 생성되어 있어서 위의 쿼리 문장에서 WHERE 조건은 인덱스 레인지 스캔 방식으로 이용할 수 없는 쿼리다.  
하지만 쿼리를 실행해보면 인덱스 레인지 스캔(range 타입)을 이용했으며, Extra 컬럼의 메시지를 보면 GROUP BY 처리까지 인덱스를 사용했음을 알 수 있다.  

#### MySQL 서버의 위의 쿼리 실행 순서  

1. (emp_no, from_date) 인덱스를 차례대로 스캔하면서 emp_no의 첫 번째 유일한 값(그룹 키) "10001"을 찾아낸다.  
2. (emp_no, from_date) 인덱스에서 emp_no가 "10001"인 것 중에서 from_date 값이 '1985-03-01'인 레코드만 가져온다. (WHERE emp_no = 10001 AND from_date = '1985-03-01'과 흡사함) 
3. (emp_no, from_date) 인덱스에서 emp_no의 그 다음 유니크한(그룹 키) 값을 가져온다. 
4. 3번 단계에서 결과가 더 없으면 처리를 종료하고, 있다면 2번 과정으로 돌아가 반복 수행한다.  

<br/>

루스 인덱스 스캔 방식은 단일 테이블에 대해 수행되는 GROUP BY 처리에만 사용할 수 있다. 또한 프리픽스 인덱스(Prefix index, 컬럼 값의 앞쪽 일부만 생성된 인덱스)는 루스 인덱스 스캔을 사용할 수 없다.  
인덱스 레인지 스캔은 유니크한 값의 개수가 많을수록 성능이 향상되지만, 루스 인덱스 스캔은 유니크한 값의 개수가 적을수록 성능이 향상된다.  
즉, 분포도가 좋지 않은 인덱스일수록 더 빠른 결과를 만들어 낸다.  
루스 인덱스 스캔은 임시 테이블이 필요하지 않다.  

<br/>
<br/>

### 임시 테이블을 사용하는 GROUP BY

GROUP BY 기준 컬럼이 드라이빙 테이블에 있든 드리븐 테이블에 있든 관계없이 인덱스를 전혀 사용하지 못할 때 이 방식으로 처리된다.  

```sql
EXPLAIN
SELECT e.last_name
FROM employees e
JOIN salaries s
ON s.emp_no = e.emp_no
GROUP By e.last_name;
```

쿼리를 실행해보면 Extra 컬럼에 "Using temporary" 메시지가 표시됐는데, 임시 테이블이 사용된것은 employees 테이블을 풀 스캔하기 때문이 아닌 인덱스를 전혀 사용할 수 없는 GROUP BY이기 때문이다.  
주의 깊게 볼 점은 "Using filesort"는 표시되지 않은 점이다. (ORDER BY를 추가하면 나타남)  
8.0 이전 버전까지는 GROUP BY가 사용된 쿼리는 그루핑되는 컬럼을 기준으로 묵시적인 정렬도 함께 수행했지만, 8.0 이후부터는 묵시적인정렬은 더 이상 실행되지 않게 바뀌었다.  

8.0에서는 GROUP BY가 필요한 경우 내부적으로 GROUP BY 절의 컬럼들로 구성된 유니크 인덱스를 가진 임시 테이블을 만들어서 중복 제거와 집계 함수 연산을 수행한다.  

```sql
-- 위의 쿼리를 처리하기 위해 다음의 임시 테이블을 생성 후 임시 테이블에서 조인의 결과를 하나씩 가져와 중복 체크를 하면서 INSERT 또는 UPDATE 수행
-- 즉, 별도의 정렬 작업 없이 GROUP BY가 처리됨
CREATE TAMPORARY TABLE ... (
    last_name VARCHAR(16),
    salary INT,
    UNIQUE INDEX ux_lastname (last_name)
)
```

<br/>
<br/>

## DISTINCT 처리

특정 컬럼의 유니크한 값만 조회하려면 SELECT 쿼리에 DISTINCT를 사용한다.  
집합(집계) 함수와 같이 DISTINCT가 사용되는 쿼리의 실행 계획에서 DISTINCT 처리가 인덱스를 사용하지 못할 때는 항상 임시 테이블이 필요하다. 하지만 실행계획에서 Extra 컬럼에서는 "Using temporary" 메시지가 출력되지 않는다.  

<br/>
<br/>

### SELECT DISTINCT ...

SELECT 되는 레코드 중에 유니크한 레코드만 가져오고자 한다면 SELECT DISTINCT 형태의 쿼리 문장을 사용하며, 이 경우 GROUP BY와 동일한 방식으로 처리된다.  
8.0부터는 GROUP BY절에 ORDER BY가 없으면 정렬하지 않으므로 두 쿼리는 내부적으로 같은 작업을 수행한다.  

DISTINCT는 SELECT 하는 레코드(튜플)을 유니크하게 가져오는 것이지, 특정 컬럼 하나만 유니크하게 가져오는 것이 아니다.  
DISTINCT는 함수가 아니므로 함수처럼 사용해도 괄호는 의미없으므로 제거하여 해석한다.  

```sql
-- (first_name, last_name)의 유니크한 레코드값 목록
SELECT DISTINCT first_name, last_name FROM employees;
```

SELECT 절에 사용된 DISTINCT는 조회되는 모든 컬럼에 영향을 미치며, 집합 함수와 사용된 경우는 다르다.  

<br/>
<br/>

### 집합 함수와 함께 사용된 DISTINCT

집합 함수가 없는 SELECT 쿼리에서 DISTINCT는 조회하는 모든 컬럼의 조합이 유니크한 것들만 가져온다.  
COUNT(), MIN(), MAX() 같은 집합 함수 내에서 사용된 DISTINCT는 집합 함수의 인자로 전달된 컬럼값이 유니크한 것들을 가져온다.  

```sql
EXPLAIN 
SELECT COUNT(DISTINCT s.salary)
FROM employees e
JOIN salaries s
ON e.emp_no = s.emp_no
WHERE e.emp_no BETWEEN 100001 AND 100100;
```

이 쿼리는 내부적으로 `COUNT(DISTINCT s.salary)`를 처리하기 위해 임시 테이블을 사용한다. 하지만 실행계획에는 임시 테이블을 사용한다는 메시지가 표시되지 않는다.  

이 쿼리의 경우 employees 테이블과 salaries 테이블을 조인한 결과에서 salary 컬럼의 값만 저장하기 위한 임시 테이블을 만들어서 사용한다.  
이 때 임시 테이블의 salary 컬럼에는 유니크 인덱스가 생성되므로 레코드 건수가 많아지면 상당히 느려질 수 있는 형태의 쿼리다.  

```sql
EXPLAIN 
SELECT COUNT(DISTINCT s.salary), COUNT(DISTINCT e.last_name)
FROM employees e
JOIN salaries s
ON e.emp_no = s.emp_no
WHERE e.emp_no BETWEEN 100001 AND 100100;
```

COUNT() 함수가 두 번 사용되면 s.salary 컬럼의 값을 저장하는 임시 테이블과 e.last_name 컬럼의 값을 저장하는 임시 테이블이 필요해서 전체적으로 2개의 임시 테이블을 사용한다.  

위 쿼리들은 DISTINCT 처리를 위해 인덱스를 이용할 수 없어서 임시 테이블이 필요했지만, 인덱스된 컬럼에 대해 임시 테이블 없이 최적화된 처리를 수행할 수 있다.  

```sql
SELECT COUNT(DISTINCT emp_no) FROM employees;
SELECT COUNT(DISTINCT emp_no) FROM dept_emp GROUP BY dept_no;
```

#### 쿼리 차이 이해하기
```sql
-- SELECT하는 (first_name, last_name)의 값이 유니크한 레코드 조회
SELECT DISTINCT first_name, last_name
FROM employees e 
WHERE emp_no BETWEEN 10001 AND 10200;

-- first_name과 last_name의 각각 유니크한 값의 카운트
SELECT COUNT(DISTINCT first_name), COUNT(DISTINCT last_name)
FROM employees e 
WHERE emp_no BETWEEN 10001 AND 10200;

-- SELECT하는 (first_name, last_name)의 값이 유니크한 레코드 카운트
SELECT COUNT(DISTINCT first_name, last_name)
FROM employees e 
WHERE emp_no BETWEEN 10001 AND 10200;
```

<br/>
<br/>

### 내부 임시 테이블 활용

MySQL 엔진이 스토리지 엔진으롭퉈 받아온 레코드를 정렬하거나 그루핑할 때는 내부적인 임시 테이블(Internal temporary table)을 사용한다.  
내부적이라는 의미는 `CREATE TEMPORARY TABLE` 명령으로 만든 임시 테이블과는 다르기 때문이다.  
일반적으로 MySQL 엔진이 사용하는 임시 테이블은 처음에는 메모리에 생성됐다가 테이블의 크기가 커지면 디스크로 옮겨진다. 특정 예외 케이스에는 메모리를 거치지 않고 디스크에 임시 테이블이 만들어지기도 한다.  
MySQL 엔진이 내부적인 가공을 위해 생성하는 임시 테이블은 다른 세션이나 다른 쿼리에서 볼 수 없으며 사용하느 것도 불가능하다.  
사용자가 생성한 임시 테이블(CREATE TEMPORARY)과는 달리 내부적인 임시 테이블은 쿼리의 처리가 완료되면 자동으로 삭제된다.  

<br/>
<br/>

### 메모리 임시 테이블과 디스크 임시 테이블

8.0 이전 버전은 임시 테이블이 메모리를 사용할 때는 MEMORY 엔진을 사용하고, 디스크에 저장될 때는 MyISAM 스토리지 엔진을 이용한다.  
8.0 버전부터는 메모리는 TempTable이라는 스토리지 엔진을 사용하고, 디스크에 저장되는 임시 테이블은 InnoDB 스토리지 엔진을 사용하도록 개선됐다.  

MEMORY 스토리지 엔진은 VARCHAR 등 가변 길이 타입을 지원히지 못해서 임시 테이블이 메모리에 만들어지면 가변 길이 타입의 경우 최대 길이만큼 메모리를 할당해서 메모리 낭비가 심했고, MyISAM은 트랜잭션을 지원하지 못하는 문제가 있었다.  
8.0부터는 가변 길이 타입을 지원하는 TempTable 스토리지 엔진이 도입됐으며, 트랜잭션을 지원하는 InnoDB 스토리지 엔진이 사용되도록 개션된 것이다.  

`internal_tmp_mem_storage_engine` 시스템 변수로 메모리용 임시 테이블을 MEMORY, TempTable 중 선택할 수 있다.  
TempTable의 최대한 사용 가능한 메모리 공간의 크기는 `temptable_max_ram` 시스템 변수로 제어할 수 있는데, 기본값은 1GB다.  
1GB보다 커지는 경우 MMAP 파일로 디스크에 기록하거나 InnoDB 테이블로 기록하는 두 가지 방식 중 하나륵 택한다.  

 MMAP 파일로 기록할지 InnoDB 테이블로 전환할지는 `temptable_use_map` 시스템 변수로 설정할 수 있는데, 기본값은 ON으로 설정돼 있다.  
 즉, 메모리의 TempTable 크기가 1GB를 넘으면 서버는 메모리의 TempTable을 MMAP 파일로 전환한다.  

 MySQL 서버는 디스크의 임시 테이블을 생성할 때 파일 오픈 후 즉시 파일삭제를 실행한다. 긜고 저장하기 위해 해당 임시 테이블을 사용한다. MySQL 서버가 종료되거나 해당 쿼리가 종료되면 임시 테이블은 즉시 사라지게 보장하는 것이다. (서버 외부의 사용자가 임시 테이블 파일을 확인 및 조작 불가)  

 내부 임시 테이블이 메모리에 생성되지 않고 처음부터 디스크 테이블로 생성되는 경우 `internal_tmp_disk_sotrage_engine` 시스템 변수에 설정된 스토리지 엔진이 사용된다. 기본값은 InnoDB다.  

<br/>
<br/>

### 임시 테이블이 필요한 쿼리

- ORDER BY와 GROUP BY에 명시된 컬럼이 다른 쿼리
- OREDR BY와 GROUP BY에 명시된 컬럼이 조인의 순서상 첫 번째 테이블이 아닌 쿼리
- DISTINCT와 ORDER BY가 동시에 쿼리에 존재하는 경우 또는 DISTINCT가 인덱스로 처리되지 못하는 쿼리
- UNION이나 UNION DISTINCT가 사용된 쿼리(select_type 컬럼이 UNION RESULT인 경우)
- 쿼리의 실행 계획에서 select_type이 DERIVED인 쿼리  

<br/>

이 밖에도 인덱스를 사용하지 못할 때 내부 임시 테이블을 생성해야 할 때가 많다.  
어떤 쿼리의 실행 계획에서 임시 테이블을 사용하는 지는 Extra 컬럼에 `Using temporary` 메시지가 표시되는지 확인하면 된다. 하지만 메시지가 표시되지 않을 때도 임시 테이블을 사용할 수 있는데 위의 예에서 마지막 3개의 패턴이 그렇다.  
1 ~ 4 쿼리 패턴은 뉴니크 인덱스를 갖는 내부 임시 테이블이 생성된다.  
마지막 쿼리 패턴은 유니크 인덱스가 없는 내부 임시 테이블이 생성된다. (처리 성능이 느림)  

<br/>
<br/>

### 임시 테이블이 디스크에 저장되는 경우

임시 테이블은 기본적으로 메모리상에 생성되지만, 아래의 조건을 만족하면 디스크 기반 임시 테이블을 사용한다.  

- UNION이나 UNION ALL에서 SELECT 되는 컬럼 중에서 길이가 512바이트 이상인 크기 컬럼이 있는 경우
- GROUP BY나 DISTINCT 컬럼에서 512바이트 이상인 크기의 컬럼이 있는 경우
- 메모리 임시 테이블의 크기가 MEMORY 스토리지 엔진에서 `tmp_table_size` 또는 `max_heap_table_size` 시스템 변수보다 크거나, TempTable 스토리지 엔진에서 `temptable_max_ram` 시스템 변수보다 값이 큰 경우  

<br/>
<br/>

### 임시 테이블 관련 상태 상수

실행 계획상에서 Using Temporary가 표시되면 임시 테이블이 사용됏다는 사실을 알 수 있고, 임시 테이블이 디스크에 생성됐는지 메모리에 생성됐는지 확인하려면 상태 변수 `SHOW SESSION STATUS LIKE 'Created_tmp%';`를 확인하면 된다.

- Created_tmp_tables: 쿼리의 처리를 위해 만들어진 내부 임시 테이블의 개수를 누적하는 상태 값이다. 이 값은 내부 임시 테이블이 메모리에 만들어졌는지 디스크에 만들어졌는지를 구분하지 않고 모두 누적한다. 
- Created_tmp_disk_tables: 디스크에 내부 임시 테이블이 만들어진 개수만 누적해서 가지고 있는 상태 값이다.  

<br/>
<br/>

## 고급 최적화

옵티마이저가 실행 계획을 수립할 때 통계 정보와 옵티마이저 옵션을 결합해서 최적의 실행 계획을 수립하게 된다.  
옵티마이저 옵션은 크게 조인 관련 옵티마이저 옵션과 옵티마이저 스위치로 구분할 수 있다.  
조인 관련 옵티마이저 옵션은 조인이 많이 사용되는 서비스에서 알아야 하는 부분이다.  
옵티마이저 스위치는 MySQL 서버의 고급 최적화 기능들을 활성화할지를 제어하는 용도로 사용된다.  

<br/>
<br/>

### 옵티마이저 스위치 옵션

`optimizer_switch` 시스템 변수를 이용해서 제어하는데, 이 변수에는 여러 개의 옵션을 세트로 묶어서 설정하는 방식으로 사용한다.  

#### optimizer_switch 시스템 변스에 설정 가능한 최적화 옵션  

옵티마이저 스위치 이름 | 기본 값 | 설명 
:-- | :-- | :-- 
batched_key_access | off | BKA 조인 알고리즘 사용 여부
block_nested_loop | on | Block Nested Loop 조인 알고리즘 사용 여부
engine_condition_pushdown | on | Engine Condition Pushdown 기능 사용 여부
index_condition_pushdown | on | Index Condition Pushdown 기능 사용 여부
use_index_extensions | on | Index Extension 최적화 사용 여부
index_merge | on | Index Merge 최적화 사용 여부
index_merge_intersection | on | Index Merge Intersection 최적화 사용 여부
index_merge_sort_union | on | Index Merge Sort Union 최적화 사용 여부
index_merge_union | on | Index Merge Union 최적화 사용 여부
mrr | on | MRR 최적화 사용 여부
mrr_cost_based | on | 비용 기반 MRR 최적화 사용 여부
semijoin | on | 세미 조인 최적화 사용 여부
firstmatch | on | FirstMatch 세미 조인 최적화 사용 여부
loosescan | on | LooseScan 세미 조인 최적화 사용 여부
materialization | on | Materialization 최적화 사용 여부<br/>(Materialization 세미 조인 최적화 포함)
subquery_materialization_cost_based | on | 비용 기반 Materialization 최적화 사용 여부  

<br/>

각 옵티마이저 스위치 옵션은 `default`, `on`, `off` 중 하나를 선택할 수 있는데 default를 설정하면 기본 값이 적용된다.  
옵티마이저 스위치 옵션은 글로벌과 세션별 모두 설정할 수 있는 시스템 변수다.  

```sql
-- 서버 전체적으로 옵티마이저 스위치 설정
SET GLOBAL optimizer_switch='index_merge=on,index_merge_union=on,...';

-- 현재 커넥션의 옵티마이저 스위치만 설정
SET optimizer_switch='index_merge=on,index_merge_union=on,...';

-- 옵티마이저 힌트로 현재 쿼리에만 옵티마이저 설정
SELECT /*+ SET_VAR(optimizer_switch='condition_fanout_filter=off') */
...
FROM ...
```

<br/>
<br/>

### MRR과 배치 키 인덱스(mrr & batched_keyaccess)

MRR(Multi-Range Read)은 메뉴얼에서 DS-MRR(Disk Sweep Multi-Range Read)이라고도 한다.  
서버에서 지금까지 지원하던 조인 방식은 드라이빙 테이블(조인이 제일 먼저 되는 테이블)에서 레코드를 한 건 읽어서 드리븐 테이블(조인되는 테이블에서 드라이빙 테이블이 아닌 테이블)의 일치하는 레코드를 찾아서 조인을 수행하는 것이었다.  
이를 NL조인(Nested Loop Join)이라고 한다.  
서버의 내부 구조상 조인 처리는 MySQL 엔진이 처리하지만, 실제 레코드를 검색하고 읽는 부분은 스토리지 엔진이 담당한다. 이 때 드라이빙 테이블의 레코드 건별로 드리븐 레코드를 찾으면 레코드를 찾고 읽는 스토리지 엔젠에서는 아무런 최적화를 수행할 수 없다.  

이 단점을 보완하기 위해 MySQL 서버는 드라이빙 테이블의 레코드를 읽어서 드리븐 테이블과의 조인을 즉시 실행햐지 않고 조인 대상을 버퍼링한다.  
조인 버퍼에 레코드가 가득 차면 MySQL 엔진은 버퍼링된 레코드를 스토리지 엔진으로 한 번에 요청한다. 스토리지 엔진은 읽어야 할 레코드들을 데이터 페이지에 정렬된 순서로 접근해서 디스크의 데이터 페이지 읽기를 최소화할 수 있는 것이다.  
데이터 페이지가 메모리(버퍼 풀)에 있더라도 접근을 최소화할 수 있다.  

이 방식을 MRR이라고 하며, MRR을 응용해서 실행되는 조인 방식을 BKA(Batched Key Access) 조인이라고 한다.  
BKA 조인 최적화는 기본적으로 비활성화돼 있는데, 부가적인 정렬 작업이 필요해지면서 성능이 악화될 수 있기 때문이다.  

<br/>
<br/>

### 블록 네스티드 루프 조인(block_nested_loop)

MySQL 서버에서 사용되는 대부분의 조인은 NL 조인인데, 조인의 연결 조건이 되는 컬럼에 모두 인덱스가 있는 경우 사용되는 조인 방식이다.  

```sql
-- first_name 조건에 일치하는 레코드 1건을 찾아서 salaries 테이블의 일치하는 레코드를 찾는 조인 실행
EXPLAIN
SELECT * 
FROM employees e
JOIN salaries s 
ON s.emp_no = e.emp_no
AND s.from_date <= now()
AND s.to_date >= now()
WHERE e.first_name = 'Amor';
```

위의 조인 쿼리를 수행할 때처럼 프로그래밍 언어에서 중첩된 반복 명령을 사용하는 것처럼 작동한다고 해서 NL 조인이라고 한다.  
레코드를 읽어서 다른 버퍼 공간에 저장하지 않고 즉시 드리븐 테이블의 레코드를 찾아서 반환함을 알 수 있다.

```sql
for (row1 IN employees) {
    for (row2 IN salaries) {
        if (condition_matched) return (row1, row2);
    }
}
```

NL조인과 블록 NL조인의 가장 큰 차이는 조인 버퍼(join_buffer_size 시스템 설정으로 조정되는 조인을 위한 버퍼)가 사용되는지 여부와 조인에서 드라이빙 테이블과 드리븐 테이블이 어떤 순서로 조인되느냐다.  
조인 알고리즘에서 "Block"이라는 단어가 사용되면 조인용으로 별도의 버퍼가 사용됐다는 의미인데, 조인 쿼리 실행 계획에서 Extra 컬럼에 "Using Join buffer" 문구가 표시되면 조인 버퍼를 사용한다는 것을 의미한다.  

조인은 드라이빙 테이블에서 일치하는 레코드의 건수만큼 드리븐 테이블을 검색하면서 처리된다. 드라이빙 테이블은 한 번에 쭉 읽지만, 드리븐 테이블은 여러 번 읽는다는 것을 의미한다.  

드라이빙 테이블에 일치하는 레코드가 1000건 이었는데, 드리븐 테이블의 조인 조건이 인덱스를 이용할 수 없다면 드리븐 테이블에서 연결되는 레코드를 찾기 위해 1000번의 풀 테이블 스캔을 해야한다. (쿼리가 매우 느림)  

어떤 방식으로도 드리븐 테이블의 풀 테이블 스캔이나 인덱스 풀 스캔을 피할 수 없다면 옵티마이저는 드라이빙 테이블에서 읽은 레코드를 메모리에 캐시한 후 드리븐 테이블과 이 메모리 캐시를 조인하는 형태로 처리한다.  
이 때 사용되는 메모리의 캐시를 조인 버퍼(join buffer)라고 하며, `join_buffer_size` 시스템 변수로 크기를 제한할 수 있고 조인이 완료되면 조인 버퍼는 바로 해제된다.  

```sql
-- 두 테이블의 연결 고리 역할을 하는 조인 조건이 없어서 카테시안 조인 수행
SELECT * 
FROM dept_emp de, employees e
WHERE de.from_date > '1995-01-01' AND e.emp_no < 109004
```

위 쿼리에서 dept_emp가 드라이빙 테이블이며, employees 테이블을 읽을 때 조인 버퍼를 이용해 블록 네스티드 루프 조인을 한는 것을 시행 계획으로 알 수 있다.

#### 쿼리 실행 계획 단계  

1. dept_emp 테이블의 ix_fromdate 인덱스를 이용해 `from_date > '1995-01-01'` 조건을 만족하는 레코드를 검색한다.  
2. 조인에 필요한 나머지 컬럼을 모두 dept_emp 테이블로 읽어서 조인 버퍼에 저장한다. 
3. employees 테이블의 프라이머리 키를 이용해 `emp_no < 109004` 조건을 만족하는 레코드를 검색한다. 
4. 3번에서 검색된 결과(employees)에 2번의 캐시된 조인 버퍼의 레코드(dept_emp)를 결합해서 반환한다.  

![join-buffer-bnljoin](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FDdk8y%2FbtrYvmpF1qM%2FEtGL9EveD5z6NmkAv06Sgk%2Fimg.png)  

<br/>

그림에서 중요한 점은 조인 버퍼가 사용되는 쿼리에서는 조인의 순서가 거꾸로인 것처럼 실행된다. 위 절차의 4번 단계가 employees 테이블의 결과를 기준으로 dept_emp 테이블의 결과를 결합(병합)한다는 것을 의미한다.  
실행 계획 상으로는 dept_emp가 드라이빙 테이블이되고, employees가 드리븐 테이블이 되지만, 실제 드라이빙 테이블의 결과는 조인 버퍼에 담아두고, 드리븐 테이블을 먼저 읽고 조인 버퍼에서 일치하는 레코드를 찾는 방식으로 처리된다.  
일반적으로 조인이 수행된 후 가져오는 결과는 드라이빙 테이블의 순서에 의해 결정되지만, 조인 버퍼가 사용되는 조인에서는 결과의 정렬 순서가 흐트러질 수 있음을 기억해야 한다.  

**8.0.18 버전부터 블록NL 조인은 더이상 사용되지 않고 해시 조인 알고리즘으로 대체되어 사용된다.**

8.0.18 버전부터 블록NL 조인은 더이상 사용되지 않고 해시 조인 알고리즘으로 대체되어 사용된다.  

<br/>
<br/>

### 인덱스 컨디션 푸시다운(index_condition_pushdown)

```sql
ALTER TABLE employees ADD INDEX ix_lastname_firstname (last_name, first_name);

SET optimizer_switch='index_condition_pushdown=off';
SHOW VARIABLES LIKE 'optimizer_switch' \G

SELECT * FROM employees WHERE last_name='Action' AND first_name LIKE '%sal';
```

위 조회 쿼리에서 `last_name` 조건은 생성된 인덱스를 레인지 스캔할 수 있지만, `first_name LIKE '%sal'` 조건은 인덱스 레인지 스캔으로 검색해야 할 인덱스의 범위를 좁힐 수 없다.  
`last_name` 조건을 인덱스의 특정 범위만 조회할 수 있는 조건이며 `first_name LIKE '%sal'` 조건은 데이터를 모두 읽은 후 사용자가 원하는 결과인지 하나씩 비교해보는 조건(체크 또는 필터링 조건)으로만 사용된다.  

위 쿼리의 실행 계획을 보면 Extra 컬럼에 "Using where"가 표시되는데, 이는  스토리지 엔진이 읽어서 반환해준 레코드가 인덱스를 사용할 수 없는 WHERE 조건에 일치하는지 검사하는 과정을 의미한다.  
`first_name LIKE '%sal'`이 검사 과정에 사용된 조건이다.  

<br/>

#### 인덱스 컨디션 푸시다운이 작동하지 않을 때  

![index-condition-pushdown](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbcGKH0%2FbtrYqy6mkp4%2FuLGJ1zhfrZuL31FxN9yWY0%2Fimg.png)  

그림은 `last_name` 조건으로 인덱스 레인지 스캔을 하고 테이블의 레코드를 읽은 후 `first_name LIKE '%sal'` 조건에 부합되는지 여부를 비교하는 과정이다.  
실제 테이블을 읽어서 3건의 레코드를 가져왔지만 그중 단 1건만 `first_name LIKE '%sal'` 조건에 일치했다. 이는 `last_name` 조건에 일치하는 레코드가 많아질수록 불필요한 작업이 커질 것이다.  

`first_name LIKE '%sal'` 조건 수행 시 `ix_lastname_firstname` 인덱스의 first_name 컬럼을 사용하지 못한 이유는 `first_name LIKE '%sal'` 조건을 누가 처리하느냐에 따라 인덱스에 포함된 first_name 컬럼을 이용할지 또는 테이블의 first_name 컬럼을 이용할지 결정되는데, 그림에서 인덱스를 비교하는 작업은 InnoDB 스토리지 엔진이 수행하지만 테이블의 레코드에서 조건을 비교하는 작업은 MySQL 엔진이 수행하는 작업이다.  
5.5 버전까지는 인덱스를 범위 제한 조건으로 사용하지 못하는 `first_name LIKE '%sal'` 조건은 MySQL 엔진이 스토리지 엔진으로 아예 전달하지 않아서 불필요한 2건의 테이블 읽기를 수행할 수 밖에 없 었다.  

<br/>

5.6 버전부터 인덱스를 범위 제한 조건으로 사용하지 못한다고 하더라도 인덱스에 포함된 컬럼의 조건이 있다면 모두 같이 모아서 스토리지 엔진으로 전달할 수 있게 핸들러 API가 개선 됐다.  

#### 인덱스 컨디션 푸시다운이 사용될 때 

![index-condition-pushdown](https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2Fd938bb05-6df1-4314-938a-066773a85786%2FUntitled.png&blockId=04d156db-925d-40de-a57e-dc4988968c9e)  

그림처럼 인덱스를 이용해 최대한 필터링까지 완료해서 필요한 레코드 1건에 대해서만 테이블 읽기를 수행할 수 있게 됐다.  

```sql
ALTER TABLE employees ADD INDEX ix_lastname_firstname (last_name, first_name);

SET optimizer_switch='index_condition_pushdown=on';
SHOW VARIABLES LIKE 'optimizer_switch' \G

SELECT * FROM employees WHERE last_name='Action' AND first_name LIKE '%sal';
```

실행 계획을 보면 Extra 컬럼에 "Using where"가 없어지고 "Using index condition"이 출력되는 것을 확인할 수 있다.  

인덱스 컨디션 푸시다운은 고도의 기술력은 필요치 않지만 쿼리의 성능이 몇 배 ~ 몇십 배로 향상될 수 있는 중요한 기능이다.  

<br/>
<br/>

### 인덱스 확장(use_index_extensions)

`use_index_extensions` 옵티마이저 옵션은 InnoDB 스토리지 엔진을 사용하는 테이블에서 세컨더리 인덱스에 자동으로 추가된 프라이머리 키를 활용할 수 있게 할지를 결정하는 옵션이다.  

InnoDB 스토리지 엔진은 PK를 클러스터링 키로 생성한다. 그래서 모든 세컨더리 인덱스는 리프 노드에 프라이머리 키 값을 가진다.  
이는 모든 세컨더리 인덱스는 PK컬럼을 포함하는 것처럼 작동한다는 것이다.  

```sql
CREATE  TABLE dept_emp (
    emp_no INT NOT NULL,
    dept_no CHAR(4) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    PRIMARY KEY (dept_no, emp_no),
    KEY ix_fromdate (from_date)
) ENGINE=InnoDB;
```

dept_emp 테이블에서 PK는 (dept_no, emp_no)이며, 세컨더리 인덱스는 from_date만 포함힌다.  
세컨더리 인덱스는 데이터 레코드를 찾아가기 위해 PK인 (dept_no, emp_no)를 순서대로 포함한다. 그래서 최종적으로 세컨더리 인덱스 ix_fromdate는 (from_date, dept_no, emp_no) 조합으로 인덱스를 생성한 것과 흡사하게 동작할 수 있다.  

MySQL 서버는 업그레이드 되면서 옵티마이저가 in_fromdate 인덱스의 마지막에 (dept_no, emp_no)가 숨어있다는 것을 인지하고 실행계획을 수립하도록 개선됐다.  

```sql
EXPLAIN
SELECT COUNT(*) FROM dept_emp WHERE from_date = '1987-07-25' AND dept_no = 'd001';
```

실행 계획의 key_len 컬럼은 이 쿼리가 인덱스를 구성하는 컬럼 중 어느 부분(어느 컬럼)까지 사용했는지를 바이트 수로 보여주는데 19바이트로 표시된 것을 보면 from_date(3바이트)와 dept_emp(16바이트) 까지 사용했다는 것을 알 수 있다.  
`dept_np = 'd001'`을 제외하면 key_len이 3으로 표시된다.  

뿐만 아니라 InnoDB의 프라이머리 키가 세컨더리 인덱스에 포함돼 있으므로 정렬 작업도 인덱스를 활용해서 처리되는 장점도 있다.  
Extra 컬럼에 "Using filesort"가 표시되지 않았다는 것은 서버가 별도의 정렬 작업 없이 인덱스 순서대로 레코드를 읽기만 함으로써 `ORDER BY dept_no`를 만족했다는 것을 의미한다.  

<br/>
<br/>

### 인덱스 머지(index_merge)  

인덱스를 이용해 쿼리를 실행하는 경우, 대부분의 옵티마이저는 테이블별로 하나의 인덱스만 사용하도록 실행 계획을 수립한다.  
인덱스 머지 실행 계획을 사용하면 하나의 테이블에 대해 2개 이상의 인덱스를 이용해 쿼리를 처리한다.  
일반적으로 WHERE 절의 조건이 여러 개 있어도 하나의 조건만으로 인덱스를 검색하고 나머지 조건은 읽어온 레코드를 체크하는 것이 일반 적이고, 작업 범위를 충분히 줄일 수 있는 경우라면 테이블별로 하나의 인덱스만 활용하는 것이 효율적이이다.  
하지만 쿼리에 사용된 각각의 조건이 서로 다른 인덱스를 사용할 수 있고 그 조건을 만족하는 레코드 건수가 많을 것으로 예상될 때 MySQL 서버는 인덱스 머지 실행 계획을 선택한다.  

#### 인덱스 머지 세부 실행 계획(각각 결과를 어떤 식으로 병합할지에 따라 구분)
- index_merge_intersection
- index_merge_sort_union
- index_merge_union  

<br/>
<br/>

### 인덱스 머지 - 교집합(index_merge_intersection)

다음 쿼리는 employees 테이블의 first_name 컬럼과 emp_no 컬럼 모두 각각의 인덱스 (ix_firstname, PK)를 가지고 있어서 2개 중 어느 조건을 사용해도 인덱스를 사용할 수 있는데, 옵티마이저는 ix_firstname, emp_no를 모두 사용해서 쿼리를 처리하기로 결정한다.  
Extra 컬럼의 "Using intersect"라고 표시된 것은 쿼리가 여러 개의 인덱스를 각각 검색해서 결과의 교집합만 반환했다는 것을 의미한다.  

```sql
EXPLAIN 
SELECT * FROM employees WHERE first_name = 'Georgi' AND emp_no BETWEEN 10000 AND 20000;
```

ix_firstname, emp_no 조건 중 하나라도 충분히 효율적으로 쿼리릴 처리할 수 있었다면 옵티마이저는 2개의 인덱스를 모두 사용하는 실행 계획을 사용하지 않았을 것이다.  
옵티마이저는 각각의 조건에 일치하는 레코드 건수를 예측해 본 결과, 두 조건 모두 상대적으로 많은 레코드를 가져와야 한다는 것을 알게 된 것이다.  

```sql
SELECT * FROM employees WHERE first_name = 'Georgi';
-- 결과 253

SELECT * FROM employees WHERE emp_no BETWEEN 10000 AND 20000;
-- 결과 10000
```

인덱스 머지 실행 계획이 아니면 다음 2가지 방식으로 처리해야 했을 것이다.  
- `first_name = 'Georgi'` 조건만 인덱스를 사용했다면 일치하는 레코드 253건을 검색한 다음 데이터 페이지에서 레코드를 찾고 emp_no 컬럼의 조건에 일치하는 레코드들만 반환하는 형태로 처리되야 한다. 
- `emp_no BETWEEN 10000 AND 20000` 조건만 인덱스를 사용했다면 프라이머리 키를 이용해 10000건을 읽어와서 `first_name = 'Georgi'` 조건에 일치하는 레코드만 반환하는 형태로 처리되야 한다.  

<br/>

첫 번째 두 번째 모두 나쁘지 않지만 실제 두 조건을 처리하면 만족하는 레코드 수는 14건뿐이다.

```sql
SELECT COUNT(*) FROM employees WHERE first_name = 'Georgi' AND emp_no BETWEEN 10000 AND 20000;
```

ix_firstname 인덱스를 사용했다면 253번의 데이터 페이지 읽기를 하지만 실제 그중에서 겨우 14번만 의미있는 작업이었을 것이고, PK를 사용했다면 10000건을 읽어서 14건만 반환하는 작업이 됐을 것이다.  
두 작 모두 비효율적이므로 옵티마이저는 각 인덱스를 검색하 두 결과의 교집합만 찾아서 반환한 것이다.  

ix_firstname 인덱스는 PK인 emp_no를 자동으로 포함하기 때문에 그냥 ix_firstname 인덱스만 사용하는 것이 성능이 더 좋을 것으로 생각한다면 인덱스 머지를 비활성화 하면 된다.  
```sql
-- 서버 전체적
SET GLOBAL optimizer_switch='index_merge_intersection=off';
-- 현재 커넥션
SET optimizer_switch='index_merge_intersection=off';
-- 현재 쿼리에서만
EXPLAIN 
SELECT /*+ SET_VAR(optimizer_switch='index_merge_intersection=off') */  * 
FROM employees WHERE first_name = 'Georgi' AND emp_no BETWEEN 10000 AND 20000;
```

<br/>
<br/>

### 인덱스 머지 - 합집합(index_merge_union)

인덱스 머지의 "Using union"은 WHERE 절에 사용된 2개 이상의 조건이 각각의 인덱스를 사용하되 OR 연산자로 연결된 경우에 사용되는 최적화다.

```sql
-- 테이블에 ix_firstname, ix_hiredate 인덱스 존재
SELECT *
FROM employees 
WHERE first_name = 'Matt' OR hire_date = '1987-03-31';
```

쿼리의 실행 계획에서 Extra 컬럼에 "Using union(ix_firstname,ix_hiredate)"라고 표시되는데, 인덱스 머지 최적화가 두 인덱스의 검색 결과를 Union 알고리즘으로 병합했다는 것을 의미한다. (합집합을 가져옴)  

위의 쿼리에서 `first_name = 'Matt'`이면서 `hire_date = '1987-03-31'`인 사원이 있었다면, 두 인덱스를 검색한 결과에 모두 포함됐을 것이다.  

```sql
SELECT * FROM employees WHERE first_name = 'Matt'; -- 1
SELECT * FROM employees WHERE hire_date = '1987-03-31'; --2
```

![index-merge-union](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FvbJfb%2FbtrYydOUO5X%2F9B1uErtN2mKYKp3JIo4EqK%2Fimg.png)

MySQL에서 세컨더리 인덱스는 자동으로 PK를 포함하기 때문에, 두 조건을 분리해서 각각 실행하면 두 인덱스 검색을 통한 결과가 PK로 정렬돼 있음을 알 수 있다.  
MySQL 서버는 두 집합에서 하나씩 가쟈와서 서로 비교하면서 PK인 emp_no 컬럼의 값이 중복된 레코드들을 정렬 없이 걸러낼 수 있는 것이다.  
이렇게 두 집합의 결과를 하나씩 가져와 중복 제거를 수행할 때 사용된 알고리즘을 우선 순위 큐(Priority Queue)라고 한다.  

<br/>
<br/>

### 인덱스 머지 - 정렬 후 합집합 (index_merge_sort_union)

인덱스 머지 작업을 하는 도중에 결과의 정렬이 필요한 경우 MySQL 서버는 인덱스 머지 최적화의 "Sort union" 알고리즘을 사용한다.  

```sql
EXPLAIN
SELECT * FROM employees
WHERE first_name = 'Matt'
OR hire_date BETWEEN '1987-03-01' AND '1987-03-31';

SELECT * FROM employees WHERE first_name = 'Matt'; -- 1
SELECT * FROM employees WHERE hire_date BETWEEN '1987-03-01' AND '1987-03-31'; -- 2
```

두 쿼리 결과를 보면 1번 쿼리는 PK로 정렬되어 출력되지만, 2번 쿼리는 PK로 정렬되지 않는 다는 것을 알 수 있다. 즉, 예제 쿼리에서 중복을 제거하기 위해 우선순위 큐를 사용하는 것이 불가능하다. (hire_date를 정렬했을 때 PK가 순차적일 수 없기 때문)  
MySQL 서버는 두 집합의 결과에서 중복을 제거하기 위해 각 집합을 emp_no 컬럼으로 정렬한 다음 중복 제거를 수행한다.  
실행 계획을 보면 Extra 컬럼에 "Using sort union"가 표시된다.  

<br/>
<br/>

### 세미 조인(semijoin)

다른 테이블과 실제 조인을 수행하지는 않고, 단지 다른 테이블에서 조건에 일치하는 레코드가 있는지 없는지만 체크하는 형태의 쿼리를 세미 조인(Semi-Join)이라고 한다.  

```sql
SELECT *
FROM employees e
WHERE emp_no IN (SELECT de.emp_no FROM dept_emp de WHERE de.from_date = '1995-01-01');
```

다른 RDBMS에 익숙한 사용자는 서브쿼리 실행 후 일치하는 레코드만 employees 테이블에서 검색할 것을 기대했지만, 5.7 이전 버전에서는 employees 테이블을 풀 스캔하면서 한 건 한 건 서브쿼리의 조건에 일치하는지 비교했다.  

<br/>

8.0 버전부터는 세미 조인을 사용한다. 세미 조인 쿼리와 안티 세미 조인 쿼리는 최적화 방법이 약간 차이가 있다.  

`= (서브쿼리)` 형태와 `IN (서브쿼리)` 형태의 세미 조인 쿼리는 3가지 최적화 방법이 있다.  

- 세미 조인 최적화
- IN-to-EXISTS 최적화
- MATERIALIZATION 최적화  

<br/>

`<> (서브쿼리)` 형태와 `NOT IN (서브쿼리)` 형태의 안티 세미 조인 쿼리는 다음 2가지 최적화 방법이 있다.  

- IN-to-EXISTS 최적화
- MATERIALIZATION 최적화  

<br/>

8.0 버전부터 세미 조인 쿼리 성능을 개선하기 위한 최적화 전략이 있다.

- Table Pull-out
- Duplicate Weed-out
- First Match
- Loose Scan
- Materialization  

<br/>

쿼리에 사용되는 테이블과 조인 조건의 특성에 따라 옵티마이저는 사용 가능한 전략들을 선별적으로 사용한다.  
Table pull-out 전략은 항상 세미 조인보다는 좋은 성능을 내기 떄문에 별도로 제어하는 옵티마이저 옵션을 제공하지 않는다. 
First Match, Loose scan 전략은 각각 firstmatch와 loosescan 옵티마이저 옵션으로 사용 여부를 결정할 수 있다.  
Duplicate Weed-out과 Materialization 전략은 materialization 옵티마이저 스위치로 사용 여부를 선택할 수 있다.  

<br/>
<br/>

#### 테이블 풀-아웃(Table Pull-out)

Table pullout 최적화는 세미 조인의 서브쿼리에 사용된 테이블을 아우터 쿼리로 끄집어낸 후 쿼리를 조인 쿼리로 재작성하는 형태의 쿼리다.  
서브쿼리 최적화 도입 이전에 수동으로 쿼리를 튜닝하던 대표적인 방법이었다.  

```sql
EXPLAIN
SELECT * FROM employees e
WHERE emp_no IN (SELECT de.emp_no FROM dept_emp de WHERE de.dept_no = 'd009');
```

위 쿼리의 실행 계획을 보면 id 값이 모두 1로 표시되는데 이는 두 테이블이 서브쿼리 형태가 아닌 조인으로 처리됐음을 의미한다.  

Table pullout 최적화는 별도로 실행 계획의 Extra 컬럼에 "Using table pullout"과 같은 문구가 출력되지 않는다.  
그래서 Table pullout 최적화가 사용됐는지 실행 계획에서 해당 테이블들의 id 컬럼 값이 같은지 다른지를 비교해보는 것(id가 같으면서 Extra 컬럼에 아무것도 출력되지 않는 경우)이 가장 간단한 방법이다.  

Table pullout 최적화는 제한사항이 있다.  
- Table pullout 최적화는 세미 조인 서브쿼리에서만 사용 가능하다.  
- Table pullout 최적화는 서브쿼리 부분이 UNIQUE 인덱스나 PK 룩업으로 결과가 1건인 경우에만 사용 가능하다. 
- Table pullout이 적용된다고 하더라도 기존 쿼리에서 가능했던 최적화 방법이 사용 불가능 것은 아니므로 MySQL에서는 가능하다면 Table pullout 최적화를 최대한 적용한다.  
- Table pullout 최적화는 서브쿼리의 테이블을 아우터 쿼리로 가져와서 조인으로 풀어쓰는 최적화를 수행하는데, 만약 서브쿼리의 모든 테이블이 아우터 쿼리로 끄집어 낼 수 있다면 서브쿼리 자체는 없어진다. 
- MySQL에서는 "최대한 서브쿼리를 조인으로 풀어서 사용해라"라는 튜닝 가이드가 많은데, Table pullout 최적화는 사실 이 가이드를 그대로 실행하는 것이다. 이제부터는 서브쿼리를 조인으로 풀어서 사용할 필요가 없다.  

<br/>
<br/>

#### 퍼스트 매치(firstmatch)  

First Match 최적화 전략은 IN(서브쿼리) 형태의 세미 조인을 EXISTS(서브쿼리) 형태로 튜닝한 것과 비슷한 방법으로 실행된다.  

```sql
EXPLAIN
SELECT * FROM employees e
WHERE e.first_name = 'Matt'
AND e.emp_no IN (
    SELECT t.emp_no FROM titles t 
    WHERE t.from_date BETWEEN '1995-01-01' AND '1995-01-30'
);
```

위 쿼리의 실행 계획을 보면 id값이 1로 같고 Extra 컬럼은 "FirstMatch(e)"라는 문구가 표시된다.  

실행 계획에서 id가 1로 표시되는 것으로 서브쿼리가 조인으로 처리됐음을 알 수 있다.  
"FirstMatch(e)" 문구는 employees 테이블 레코드에 대해 titles 테이블에 일치하는 레코드 1건만 찾으면 더 이상 titles 테이블을 검색하지 않는다는 것을 의미한다. 실제 의미론적으론 EXISTS(서브쿼리)와 동일하게 처리된 것이다.  
하지만 FirstMatch는 서브쿼리가 아니라 조인으로 풀어서 실행하면서 일치하는 첫 번째 레코드만 검색하는 최적화를 실행한 것이다.  

<br/>

FirstMatch 최적화 작동 방식  
![first-match](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcqbxcB%2FbtrYIcI0leE%2FZGYXd4vXjQjTAIShmp0J00%2Fimg.png)  

<br/>

employees 테이블에서 `e.first_name = 'Matt'`인 사원의 정보를 ix_firstname 인덱스를 사용해 레인지 스캔으로 읽은 결과가 왼쪽 employees 테이블이다.  
first_name이 'Matt'이고 사원 번호가 12302인 레코드를 titles 테이블과 조인해서 titles 테이블의 from_date가 `t.from_date BETWEEN '1995-01-01' AND '1995-01-30'` 조건을 만족하는 레코드를 찾아본다.  
조건을 만족하지 않는 레코드는 반환되는 결과가 없다.  
다음 `e.first_name = 'Matt'`을 만족하는 컬럼에 대해 titles 레코드중 from_date 조건을 만족하는지 검사해서 레코드를 찾으면 결과를 반환한다.  

FirstMatch 최적화는 5.5 버전의 IN-to-EXISTS 변환과 비슷하게 동작하며, 그에 비해 다음 장점이 있다.  

- 가끔은 여러 테이블이 조인되는 경우 원래 쿼리에는 없던 동등 조건을 옵티마이저가 자동으로 추가하는 형태의 최적화가 실행되기도 한다.  
- IN-to-EXISTS 변환 최적화 전략에서는 아무런 조건 없이 변환이 가능한 경우에는 무조건 그 최적화를 수행했지만, FirstMatch 최적화에서는 서브쿼리의 모든 테이블에 대해 FirstMatch 최적화를 수행할지 아니면 일부 테이블에 대해서만 수행할지 취사선택할 수 있다는 것이 장점이다.  

<br/>

FirstMatch 최적화 또한 특정 형태의 서브쿼리에서 자주 사용되는 최적화로 제한 사항과 특성을 알 필요가 있다.  

- FirstMatch는 서브쿼리에서 하나의 레코드만 검색되면 더이상의 검색을 멈추는 단축 실행 경로(Short-cut path)이기 때문에 FirstMatch 최적화에서 서브쿼리는 그 서브쿼리가 참조하는 모든 아우터 테이블이 먼저 조회된 이후에 실행된다.  
- FirstMatch 최적화가 사용되면 실행 계획의 Extra 컬럼에는 `FirstMatch(label-N)` 문구가 표시된다. 
- FirstMatch 최적화는 상호 연관 서브쿼리(Correlated subquery)에서도 사용될 수 있다. 
- FirstMatch 최적화는 GROUP BY나 집합 함수가 사용된 서브쿼리의 최적화에는 사용될 수 없다.  

<br/>

FirstMatch 최적화는 optimizer_switch 시스템 변수에서 semijoin 옵션과 firstmatch 옵션이 모두 ON일 때 사용할 수 있다. (비활성화는 firstmatch만 OFF 설정)  

<br/>
<br/>

### 루스 스캔 (loosescan) 

세미 조인 서브쿼리 최적화의 LooseScan은 인덱스를 사용하는 GROUP BY 최적화 방법에서 살펴본 "Using index for group-by"의 루스 인덱스 스캔(Loose Index Scan)과 비슷한 읽기 방식을 사용한다.  

```sql
EXPLAIN
SELECT * FROM departments d WHERE d.dept_no IN (
    SELECT de.dept_no FROM dept_emp de
);
```

<br/>

departments 테이블의 레코드 건수는 9건 뿐이지만 dept_emp 테이블의 레코드 건수는 33만건 가까이 저장돼 있다.  
dept_emp 테이블에는 인덱스가 (dept_no, emp_no) 조합으로 PK 인덱스가 만들어져 있다.  
PK는 전체 레코드 수는 33만 건 정도 있지만 dept_no만으로 그루핑해서 보면 9건 밖에 없다는 것을 알 수 있다.  
그래서 dept_emp 테이블에서 PK를 루스 인덱스 스캔으로 유니크한 dept_no만 읽으면 효율적으로 서브쿼리 부분을 실행할 수 있다.  

![loose-scan](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FDX40q%2FbtrYJRX30fi%2Fj3IgWVhPJ46qwkJPyvRgak%2Fimg.png)  

그림에서 서브쿼리에 사용된 dept_emp 테이블이 드라이빙 테이블로 실행되며, dept_emp 테이블의 PK를 dept_no 부분에서 유니크하게 한 건씩 읽고 있다는 것을 보여준다.  
쿼리 실행 계획을 보면 id가 1이므로 내부적으로 조인 처리됐으며 Extra 컬럼에 LooseScan가 표시된 것을 확인할 수 있다.  

LooseScan 최적화는 루스 인덱스 스캔으로 서브쿼리 테이블을 읽고 그다음으로 아우터 테이블을 드리븐으로 사용해서 조인을 수행한다. 그래서 서브쿼리 부분이 루스 인덱스 스캔을 사용할 수 있는 조건이 갖춰져야 사용할 수 있는 최적화다.  

```sql
SELECT ... FROM ... WHERE expr IN (SELECT keypart1 FROM tab WHERE ...);
SELECT ... FROM ... WHERE expr IN (SELECT keypart2 FROM tab WHERE keypart1 = '상수');
```

<br/>
<br/>

### 구체화(Materialization)

Materialization 최적화는 세미 조인에 사용된 서브쿼리를 통째로 구체화해서 쿼리를 최적화한다는 의미다.  
구체화는 내부 임시 테이블을 생성한다는 것을 의미한다.  

```sql
EXPLAIN
SELECT * FROM employees e
WHERE e.emp_no IN (
    SELECT de.emp_no FROM dept_emp de
    WHERE de.from_date = '1995-01-01'
);
```

이 쿼리는 FirstMatch 최적화를 사용하면 employees 테이블에 대한 조건이 서브쿼리 이외는 아무것도 없기 때문에 테이블을 풀 스캔해야 할 것이다. 이런 형태의 세미 조인에서는 FirstMatch 최적화가 성능 향상에 도움이 되지 않는다.  

MySQL 서버 옵티마이저는 이런 형태의 쿼리를 위해 서브쿼리 구체화라는 최적화를 도입했다.  

쿼리의 실행 계획을 확인하면 마지막 라인의 select_type 컬럼에는 "MATERIALIZED"라고 표시됐다.  
이 쿼리에서 사용되는 테이블은 2개인데 실행 계획이 3개 라인이 출력된 것을 봐도 쿼리의 실행 계획 어디선가 임시 테이블이 생성됐음을 알 수 있다.  
dept_emp 테이블을 읽는 서브쿼리가 먼저 실행되어 그 결과로 임시 테이블 `<subquery2>`가 만들어졌다. 그리고 최종적으로 서브쿼리가 구체화된 임시 테이블 `<subquery2>`과 employees 테이블을 조인해서 결과를 반환한다.  

Materialization 최적화가 사용될 수 있는 형태의 쿼리에도 제한 사항과 특성이 있다.  

- IN(서브쿼리)에서 서브쿼리는 상호 연관 서브쿼리(Correlated subquery)가 아니어야 한다.  
- 서브쿼리는 GROUP BY나 집합 함수들이 사용돼도 구체화를 사용할 수 있다.  
- 구체화가 사용된 경우에는 내부 임시 테이블이 사용된다.  

<br/>

Materialization 최적화는 optimizer_switch 시스템 변수에서 semijoin 옵션과 materialization 옵션이 모두 ON 이면 된다. (비활성화 한다면 materialization옵션만 OFF)  

<br/>
<br/>

### 중복 제거(Duplicated Weed-out)

Duplicate Weedout은 세미 조인 서브쿼리를 일반적인 INNER JOIN 쿼리로 바꿔서 실행하고 마지막에 중복된 레코드를 제거하는 방법으로 처리되는 최적화 알고리즘이다.  

```sql
EXPLAIN
SELECT * FROM employees e
WHERE e.emp_no IN (
    SELECT s.emp_no FROM salaries s WHERE s.salary > 150000
);
```

salaries 테이블의 PK가 (emp_no, from_date)이므로 salary가 150000 이상인 레코드를 조회하면 중복된 emp_no가 발생할 수 있다.  
그래서 이 쿼리를 재작성해서 GROUP BY 절을 넣어 주면 위의 세미 조인 서브쿼리와 동일한 결과를 얻을 수 있다.  

```sql
SELECT e.*
FROM employees e
JOIN salaries s 
ON e.emp_no = s.emp_no AND s.salary > 150000
GROUP BY e.emp_no;
```

Duplicate Weedout 최적화는 원본 쿼리를 INNER JOIN과 GROUP BY로 바꿔 동일한 작업으로 쿼리를 처리한다.  

Duplicate Weedout 최적화 알고리즘으로 처리하는 과정

1. salaries 테이블의 ix_salary 인덱스를 스캔해서 salary가 150000보다 큰 사원을 검색해 employees 테이블 조인을 실행
2. 조인된 결과를 임시 테이블에 저장
3. 임시 테이블에 저장된 결과에서 emp_no 기준으로 중복 제거
4. 중복을 제거하고 남은 레코드를 최종적으로 반환 

<br/>

![duplicate-weedout](https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fs3-us-west-2.amazonaws.com%2Fsecure.notion-static.com%2F27a56723-e1c5-497e-8070-6963b8ae751b%2FUntitled.png&blockId=a49203d4-ff9a-4fa2-a523-67b4ac4f129e)

<br/>

쿼리의 실행 계획을 보면 Extra 컬럼에 "Start temporary"와 "End temporary"문구가 표시된 것을 확인할 수 있다.  
1번 조인을 수행하는 작업과 2번 임시 테이블로 저장하는 작업은 반복적으로 실행되는 과정이다.  
이 반복 과정이 시작되는 테이블의 실행 계획 라인에는 "Start temporary", 반복 과정이 끝나는 라인에는 "End temporary"문구가 표시된다. 이 구간이 최적화 처리 과정이라고 보면 된다.  

<br/>

Duplicate Weedout 최적화의 장점 및 제약사항  

- 서브쿼리가 상호연관 서브쿼리라고 하더라도 사용할 수 있는 최적화다.
- 서브쿼리가 GROUP BY나 집합 함수가 사용된 경우에는 사용될 수 없다.  
- Duplicate Weedout은 서브쿼리의 테이블을 조인으로 처리하기 때문에 최적화할 수 있는 방법이 많다.  

<br/>
<br/>

### 컨디션 팬아웃(condition_fanout_filter)

조인을 실행할 때 테이블의 순서는 쿼리의 성능에 매우 큰 영향을 미친다.  
A, B 테이블이 조인할 때 A 테이블은 조건에 일치하는 레코드가 1만 건이고, B 테이블은 10건 이라고 가정한다.  
A 테이블을 드라이빙 테이블로 결정하면 테이블을 1만번 읽어야 하고, B 테이블의 인덱스를 이용해 조인을 실행한다고 해도 레코드를 읽을 때마다 B 테이블의 인덱스를 구성하는 B-Tree의 루트 노드부터 검색을 실행해야 한다.  
그래서 MySQL 옵티마이저는 여러 테이블이 조인되는 경우 가능하다면 일치하는 레코드 건수가 적은 순서대로 조인을 실행한다.  

```sql
EXPLAIN
SELECT * FROM employees e
JOIN salaries s ON s.emp_no = e.emp_no
WHERE e.first_name = 'Matt'
AND e.hire_date BETWEEN '1985-11-21' AND '1986-11-21';
```

`condition_fanout_filter` 옵티마이저 옵션을 비활성화 하고 실행 계획을 본다.  

```sql
SET optimizer_switch='condition_fanout_filter=off';
SET optimizer_switch='condition_fanout_filter=on';

EXPLAIN
SELECT * FROM employees e
JOIN salaries s ON s.emp_no = e.emp_no
WHERE e.first_name = 'Matt'
AND e.hire_date BETWEEN '1985-11-21' AND '1986-11-21';
```

<br/>

비활성화 상태의 처리 절차  

1. employees 테이블에서 ix_firstname 인덱스를 이용해 `first_name = 'Matt` 조건에 일치하는 233건의 레코드를 검색한다.
2. 검색된 233건의 레코드 중에서 hire_date가 '1985-11-21'~'1986-11-21' 사이인 레코드만 걸러내는데, 이 실행 계획에서는 filtered 컬럼의 값이 100인 것은 옵티마이저가 233건 모두 hire_date 컬럼의 조건을 만족할 것으로 예측했다는 것을 의미한다.  
3. employees 테이블을 읽은 결과 233건에 대해 salaries 테이블의 PK를 이용해 salaries 테이블 레코드를 읽는다. 이때 옵티마이저는 employees 테이블의 레코드 한 건당 salaries 테이블의 레코드 10건이 일치할 것으로 예상했다.  

중요하게 볼 것은 employees 테이블의 rows 컬럼의 값이 233이고, filtered 컬럼의 값이 100%라는 것이다.  

<br>

`condition_fanout_filter` 옵티마이저 옵션을 활성화 하고 실행 계획을 본다.  

활성화 후 조회한 실행 계획에서도 rows 컬럼의 값은 233건으로 동일하지만 filtered 컬럼의 값이 100%가 아니라 23.2%로 변경됐다.  
`condition_fanout_filter`가 활성화 되면서 옵티마이저는 인덱스를 사용할 수 있는 first_name 컬럼 조건 이외의 나머지 조건 (hire_date 컬럼의 조건)에 대해서도 얼마나 조건을 충족할 수 있는지 고려했다는 뜻이다.  
즉, `condition_fanout_filter` 최적화가 비활성화된 경우에는 employees 테이블에서 모든 조건을 충족하는 레코드가 233건일 것으로 예측한 반면, 활성화된 경우 54건(233 * 0.2320)만 조건을 충족할 것이라고 예측했다.  
옵티마이저가 조건을 만족하는 레코드 건수를 정확하게 예측할 수 있다면 더 빠른 실행 계획을 만들어낼 수 있는 것이다.  

`condition_fanout_filter` 최적화는 다음 조건을 만족하는 컬럼에 조건드렝 대해 조건을 만족하는 레코드 비율을 계산할 수 있다.  

- WHERE 조건절에 사용된 컬럼에 대해 인덱스가 있는 경우
- WHERE 조건절에 사용된 컬럼에 대해 히스토그램이 존재하는 경우

<br/>

`condition_fanout_filter` 최적화 기능을 활성화하면 옵티마이저는 더 정교한 계산을 거쳐서 실행 계획을 수립한다.  
그에 따라 쿼리의 실행 계획 수립에 더 많은 시간과 컴퓨팅 자원을 사용하게 된다.  
8.0 이전 버전에서 업그레이드 할 때 서버가 처리하는 쿼리의 빈도가 매우 높다면 실행 계획 수립에 추가되는 오버헤드가 크게 보일 수 있으므로 업그레이드 전 성능 테스트를 진행하는 것이 좋다.  

<br/>
<br/>

### 파생 테이블 머지(derived_merge)

예전 버전의 서버에서는 FROM 절에 사용된 서브쿼리는 먼저 실행해서 그 결과를 임시 테이블로 만든 후 외부 쿼리 부분을 처리했다.  

```sql
EXPLAIN
SELECT * FROM (
	SELECT * FROM employees e
	WHERE e.first_name = 'Matt'
) derived_table
WHERE derived_table.hire_date = '1986-04-03';
```

MySQL 서버에서는 FROM 절에 사용된 서브쿼리를 파생 테이블(Derived Table)이라고 부른다.  
이 경우 서버는 FROM 절의 서브쿼리로 실행하는 레코드를 읽어서 임시 테이블을 생성하고 INSERT 한다. 그리고 다시 임시 테이블을 읽으므로 MySQL 서버는 레코드를 복사하고 읽는 오버헤드가 추가된다.  
내부적으로 생성되는 임시 테이블은 처음에는 메모리에 생성되지만, 임시 테이블에 저장될 레코드 건수가 많아지면 결국 디스크로 다시 기록돼야 한다.  

<br/>

5.7 버전부터는 파생 테이블로 만들어지는 서브쿼리를 외부 쿼리와 병합해서 서브쿼리 부분을 제거하는 최적화가 도입됐는데, derived_merge 최적화 옵션은 이런 임시 테이블 최적화를 활성화할지 여부를 결정한다.  

실행 계획을 확인하면 서브쿼리 없이 employees 테이블을 조회하던 형태의 단순 실행 계획이 보인다.  

옵티마이저가 모든 쿼리에 대해서 서브쿼리를 외부 쿼리로 병합할 수는 없다.  
다음 경우에는 가능하다면 서브 쿼리는 외부 쿼리로 수동으로 병합해서 작성하는 것이 쿼리의 성능 향상에 도움이 된다.  

- SUM() 또는 MIN(), MAX() 같은 집계 함수와 윈도우 함수(Window Function)가 사용된 서브쿼리
- DISTINCT가 사용된 서브쿼리
- GROUP BY나 HAVING이 사용된 서브쿼리
- LIMIT이 사용된 서브쿼리 
- UNION 또는 UNION ALL을 포함하는 서브쿼리
- SELECT 절에 사용된 서브쿼리
- 값이 변경되는 사용자 변수가 사용된 서브쿼리

<br/>
<br/>

### 인비저블 인덱스(use_invisible_indexes)

8.0 이전까지는 인덱스가 존재하면 항상 옵티마이저가 실행 계획을 수립할 때 해당 인덱스를 검토하고 사용했다.
8.0 버전부터는 인덱스의 가용 상태를 제어할 수 있는 기능이 추가됐다.  
이제 인덱스를 삭제하지 않고, 해당 인덱스를 사용하지 못하게 제어하는 기능을 제공한다.  

```sql
-- 인덱스의 가용 상태 변경 (인덱스 사용x)
ALTER TABLE employees ALTER INDEX ix_hiredate INVISIBLE;
-- 인덱스의 가용 상태 변경 (인덱스 사용ㅐ)
ALTER TABLE employees ALTER INDEX ix_hiredate VISIBLE;
```

use_invisible_indexes 옵티마이저 옵션을 이용하면 INVISIBLE로 설정된 인덱스라 하더라도 옵티마이저가 사용하게 제어할 수 있다.  
기본값은 off로 INVISIBLE 상태의 인덱스는 옵티마이저가 볼 수 없는 상태다.  

```sql
SET optimizer_switch='use_invisible_indexes=on';
```

<br/>
<br/>

### 스킵 스캔(skip_scan)

인덱스의 핵심은 값이 정렬돼 있다는 것이며, 이로 인해 인덱스를 구성하는 컬럼의 순서가 매우 중요하다.  
인덱스가 (A, B, C)로 구성됐을 때 쿼리의 조건절이 B와 C 컬럼에 대한 조건이면 인덱스를 사용할 수 없다.  
인덱스 스캡 스캔은 제한적이긴 하지만 인덱스의 이런 제약 사항을 뛰어넘을 수 있는 최적화 기법이다.  

```sql
ALTER TABLE employees ADD INDEX ix_gender_birthdate (gender, birth_date);

-- 위 인덱스 사용을 위해서는 gender 컬럼에 대한 비교 조건이 필수적

SELECT * FROM employees WHERE birth_date >= '1965-02-01'; -- 인덱스 사용 x
SELECT * FROM employees WHERE gender ='M' AND birth_date >= '1965-02-01'; -- 인덱스 사용 o
```

인덱스 스캡 스캔은 인덱스의 선행 컬럼이 조건절에 사용되지 않더라도 후행 컬럼의 조건만으로 인덱스를 이용한 쿼리 성능이 개선이 가능하다.  
위 쿼리를 실행할 때 옵티마이저는 테이블에 존재하는 모든 gender 컬럼의 값을 가져와 두 번째 쿼리와 같이 gender 컬럼의 조건이 있는 것처럼 쿼리를 최적화한다.  
이 때 선행 컬럼의 값이 매우 다양하면 인덱스 스킵 스캔 최적화가 비효율적이기 때문에 옵티마이저는 선행 컬럼이 소수의 유니크한 값을 가질때만 인덱스 스킵 스캔 최적화를 사용한다.  

```sql
SET optimizer_switch='skip_scan=on'; -- 현재 세션에서 활성화
SET optimizer_switch='skip_scan=off'; -- 현재 세션에서 비활성화

-- 특정 테이블에 대해 인덱스 스킵 스캔을 사용하도록 힌트 적용
SELECT /*+ SKIP_SCAN(employees) */ COUNT(*)
FROM employees WHERE birth_date >= '1965-02-01';
-- 특정 테이블과 인덱스에 대해 인덱스 스킵 스캔을 사용하도록 힌트 적용
SELECT /*+ SKIP_SCAN(employees ix_gender_birthdate) */ COUNT(*)
FROM employees WHERE birth_date >= '1965-02-01';
-- 특정 테이블에 대해 인덱스 스킵 스캔을 사용하지 않도록 힌트 적용
SELECT /*+ NO_SKIP_SCAN(employees) */ COUNT(*)
FROM employees WHERE birth_date >= '1965-02-01';
```

<br/>
<br/>

### 해시조인(hash_join)

8.0.18 버전부터 해시 조인이 지원되기 시작했다.  

해시 조인을 기대하는 이유는 NL 조인보다 해시 조인이 빠르다고 생각하기 때문인데 항상 옳인 이야기는 아니다.  

![nl-hash-diff](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FQbqa7%2FbtrYIUA4Jz1%2FK70wHywfoNxeVA3oG0fhoK%2Fimg.png)  
화살표는 전체 쿼리의 실행 시간을 의미한다.  

<br/>

NL 조인과 해시 조인은 똑같은 지점에서 시작했지만 해시 조인이 먼저 끝난 것을 볼 수 있다.  
A 지점은 쿼리가 실행되면서 MySQL 서버가 첫 번째 레코드를 찾아낸 시점이며, B 지점은 마지막 레코드를 찾아낸 시점을 의미한다.  
실제로 마지막 레코드를 찾았다고 해서 항상 쿼리가 완료되는 것은 아니다.

해시 조인은 첫 번째 레코드를 찾는 데는 시간이 많이 걸리지만 최종 레코드를 찾는 데까지는 많이 걸리지 않음을 알 수 있다.  
NL 조인은 마지막 레코드를 찾는 데까지는 시간이 많이 걸리지만 첫 번째 레코드를 찾는 것은 상대적으로 훨씬 빠르다는 것을 알 수 있다.  
해시 조인은 최고 스루풋(Best Throughput) 전략에 적합하며, NL 조인은 최고 응답 속도(Best-Response-time) 전략에 적합하다는 것을 알 수 있다.  
일반적인 웹 서비스는 온라인 트랜잭션(OLTP) 서비스이기 때문에 스루풋도 중요하지만 응답 속도가 더 중요하고, 분석과 같은 서비스는 전체적인 처리 소요 시간이 중요하기 때문에 전체 스루풋이 더 중요하다.  

MySQL 서버는 범용(OLTP 처리를 위한 데이터베이스 서버) RDBMS이다. 아마 대용량 데이터 분석을 위해서 MySQL 서버를 사용하지는 않을 것이다.  
이 관점으로 보면 MySQL 서버가 응답속도와 스르풋 중 어디에 집중해서 최적화할 것인지 명확해진다. 서버는 주로 조인 조건의 컬럼이 인덱스가 없다거나 조인 대상 테이블 중 일부의 레코드 건수가 매우 적은 경우 등에 대해서만 해시 조인 알고리즘을 사용하도록 설계되어 있다.  
즉, 해시 조인 최적화는 NL 조인이 사용되기에 적합하지 않은 경우를 위한 차선책(Fallback stategy) 같은 기능으로 생각하는 것이 좋다.  

MySQL 서버는 8.0.20 버전부터 조인조건이 좋지 않은 경우 NL조인을 블록 NL 조인으로 최적화하던것을 해시 조인으로 최적화하도록 변경됐다.  
optimizer_switch 설정의 블록 NL 조인 관련(BNL) 변수는 해시 조인을 유도하는 목적으로 사용된다.  

```sql
-- IGNORE INDEX는 NL 조인이 사용되지 못하게 하기 위해 사용
-- EXPLAIN ANALYSE, EXMPLAIN FORMAT=TREE 명령 빌드 테이블, 프로브 테이블 식별 가능
EXPLAIN
SELECT * FROM employees e IGNORE INDEX (PRIMARY, ix_hiredate)
JOIN dept_emp de IGNORE INDEX (ix_empno_fromdate, ix_fromdate)
ON de.emp_no = e.emp_no AND de.from_date = e.hire_date;
```

일반적으로 해시 조인은 빌드 단계(Build-phase)와 프로브 단계(Probe-phase)로 나뉘어 처리된다.  

#### 빌드 단계  

조인 대상 테이블 중에서 레코드 건수가 적어서 해시 테이블로 만들기에 용이한 테이블을 골라서 메모리에 해시 테이블을 생성(빌드)하는 작업을 수행한다.  
빌드 단계에서 해시 테이블을 만들 때 사용되는 원본 테이블을 빌드 테이블이라고 한다.  

#### 프로브 단계  

나머지 테이블의 레코드를 읽어서 해시 테이블의 일치 레코드를 찾는 과정을 의미한다. 이 때 읽는 나머지 테이블을 프로브 테이블이라고도 한다.  

<br/>

Tree 포맷의 실행 계획을 보면 최하단의 제일 안쪽(들여쓰기가 가장 많이 된)의  테이블이 빌드 테이블로 선정된 것이다.  
옵티마이저는 해시 조인을 위해 빌드 테이블인 dept_emp 테이블의 레코드를 읽어서 메모리에 해시 테이블을 생성했고, 프로브 테이블로 선택된 employees 테이블을 스캔하면서 메모리에 생성된 해시 테이블에서 레코드를 찾아서 결과를 사용자에게 반환한 것이다.  

![hash-join-all-processing](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcTw5AI%2FbtrYG72d8iy%2FaQKPoCxBQlKyzZeWfFgwr1%2Fimg.png)  
해시 조인(메모리에서 모두 처리 가능한 경우)  

<br/>

해시 테이블을 메모리에 저장할 때 join_buffer_size 시스템 변수로 크기를 제어할 수 있는 조인 버퍼를 사용한다. (기본 256KB)  
레코드 건수가 많아 조인 버퍼의 공간이 부족하면, 서버는 빌드 테이블과 프로브 테이블을 적당한 크기(하나의 청크가 조인 버퍼보다 작도록)의 청크로 분리한 다음, 청크별로 위의 그림의 방식으로 해시조인 처리한다.  

<br/>

![hash-join-first-processing](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbgLXQ8%2FbtrYIpBibP2%2FKl8gMpMAR51N6DOWvLmE50%2Fimg.png)  
해시 조인 1차 처리(해시 테이블이 조인 버퍼 메모리보다 큰 경우)  

<br/>

만들어질 해시 테이블이 설정된 메모리 크기(join_buffer_size)보다 큰지를 알 수 없다면, 서버는 dept_emp 테이블을 읽으면서 메모리의 해시 테이블을 준비하다가 지정된 메모리 크기를 넘어서면 dept_emp 테이블으; 나머지 레코드를 디스크에 청크로 구분해서 저장한다. (그림의 1, 2 과정)  
그리고 서버는 employees 테이블의 emp_no 값을 이용해 메모리의 해시 테이블을 검색해서 1차 조인 결과를 생성하며 동시에 employees 테이블에서 읽은 레코드를 디스크에 청크로 구분해서 저장한다.  
**빌드 테이블 청크**는 dept_emp 테이블의 레코드를 저장해둔 공간이고, **프로브 테이블 청크**는 employees 테이블의 레코드들을 저장해둔 공간이다.  

<br/>

![hash-join-second-processing](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbSTyqw%2FbtrYMXQNht6%2Fkr2bDKaZJ3cAstlDKsFzak%2Fimg.png)  
해시 조인 2차 처리(해시 테이블이 조인 버퍼 메모리보다 큰 경우)  

<br/>

1차 조인이 완료되면 그림의 1번처럼 서버는 디스크에 저장된 빌드 테이블 청크에서 첫 번째 청크를 다시 읽어서 다시 메모리 해시 테이블을 구축한다.  
그리고 2번처럼 프로브 테이블 청크에서 첫 번째 청크를 읽으면서 새로 구축된 메모리 해시 테이블과 조인을 수행해 2차 결과를 가져온다.  
그림은 첫 번째 청크만 보여주지만 실제론 디스크에 저장된 청크 개수만큼 이 과정을 반복 처리해서 완성된 조인 결과를 만들어낸다.  
이렇게 청크 단위로 조인을 수행하기 위해 서버는 2차 해시 함수를 이용해 빌드 테이블과 프로브 테이블을 동일 개수의 청크로 쪼개어 디스크로 저장한다.  

옵티마이저는 빌드 테이블의 크기에 따라 해시조인(메모리에서 모두 처리 가능한 경우)은 클래식 해시 조인(Classic hash join) 알고리즘을 사용하고, 해시 조인 1차 처리의 경우 그레이스 해시 조인(Grace hash join)알고리즘을 하이브리드 하게 활용하도록 구현돼 있다.  
알고리즘은 xxHash64 해시 함수를 사용한다.  

<br/>
<br/>

### 인덱스 정렬 선호(prefer_ordering_index)

옵티마이저는 ORDER BY 또는 GROUP BY 인덱스를 사용해 처리 가능한 경우 쿼리의 실행 계획에서 이 인덱스의 가중치를 높이 설정해서 실행된다.  

```sql
EXPLAIN
SELECT * FROM employees
WHERE hire_date BETWEEN '1985-01-01' AND '1985-02-01'
ORDER BY emp_no;
```

위 쿼리는 다음 2가지 실행 계획을 선택할 수 있다.  

1. ix_hiredate 인덱스를 이용해 `hire_date BETWEEN '1985-01-01' AND '1985-02-01'` 조건에 일치하는 레코드를 찾은 다음, emp_no로 정렬해서 결과를 반환 
2. employees 테이블의 PK가 emp_no이므로 PK를 정순으로 읽으면서 hire_date 컬럼의 조건에 일치하는지 비교 후 결과를 반환  

<br/>

hire_date 컬럼의 조건에 부합되는 레코드 건수가 많지 않다면 1번이 효율적일 것 이다.  

실행 계획에서는 PK를 풀스캔 하면서 hire_date 컬럼의 값이 1985년 1월인 건만 필터링하도록 쿼리를 처리하고 있다. 이렇게 체크해야 하는 레코드 건수가 많음에도 불구하고 정렬된 인덱스 활용으로 실행 계획이 수립되지 않은 것은 옵티마이저가 실수로 잘못된 실행 꼐획을 선택한 것일 가능성이 높다. (가끔 실수를 함)  

8.0.20 버전까지는 `IGNORE INDEX`로 특정 인덱스를 사용하지 못하도록 했지만, 8.0.21 버전부터는 옵티마이저가 ORDER BY를 위한 인덱스에 너무 가중치를 부여하지 않도록 prefer_ordering_index 옵티마이저 옵션이 추가됐다.  
옵션값은 기본 ON으로 설정돼 있지만 옵티마이저가 자주 실수를 한다면 OFF로 변경하면 된다.  
```sql
SET SESSION optimizer_switch='prefer_ordering_index=OFF';

SELECT /*+ SET_VAR(optimizer_switch='prefer_ordering_index=OFF') */ *
FROM ...
```

<br/>
<br/>

### 조인 최적화 알고리즘

MySQL에는 조인 최적화는 많이 개선됐지만, 테이블 개수가 많아지면 최적화된 실행 계획을 찾는 것이 상당히 어려워지고, 하나의 쿼리에서 조인되는 테이블의 개수가 많아지면 실행 계획을 수립하는 데만 몇 분이 걸릴 수도 있다.  

MySQL에는 조인 쿼리의 실행 계획 최적화를 위한 알고리즘이 2개 있다.  
4개 테이블을 조인하는 쿼리 문장이 조인 옵티마이저 알고리즘에 따른 처리를 확인한다.  

<br/>
<br/>

```sql
SELECT * 
FROM t1 JOIN t2 ON ... JOIN t3 ON ... JOIN t4 ON ... WHERE ...
```

### Exhaustive 검색 알고리즘

![Exhaustive-algorithm](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FeolJzc%2FbtrYToWjcAa%2FHNoflQ6XN7RKR3VnANilcK%2Fimg.png)  

<br/>

Exhaustive 검색 알고리즘은 5.0 이전 버전에서 사용되던 조인 최적화 기법으로, FROM 절에 명시된 모든 테이블의 조합에 대해 실행 계획의 비용을 계산해서 최적의 조합 1개를 찾는 방법이다.  
이 알고리즘에서 테이블 개수당 가능 조인 조합은 Table!(Factorial) 이된다.  
Exhaustive 검색 알고리즘에서 테이블이 10개만 넘어도 실행 계획을 수림하는데 몇 분이 걸린다.  

<br/>
<br/>

### Greedy 검색 알고리즘

![Greedy-algorithm](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcRKXxe%2FbtrYXjsUf2d%2FCgKVbQx17QKUfklp5k5Ckk%2Fimg.png)  

Greedy 검색 알고리즘은 Exhaustive 검색 알고리즘의 시간 소모적인 문제점을 해결하기 위해 5.0부터 도입된 조인 최적화 기법이다.  
그림은 테이블(t1~t4)이 Greedy 검색 알고리즘으로 처리될 때(optimizer_search_depth값은 2로 가정) 최적의 조인 순서를 검색하는 방법을 보여준다.  

1. 전체 N개의 테이블 중에서 optimizer_search_depth 시스템 설정 변수에 정의된 개수의 테이블로 가능한 조인 조합을 생성 
2. 1번에서 생성된 조인 조합 중에서 최소 비용의 실행 계획 하나를 선정
3. 2번에서 전정된 실행 꼐획의 첫 번째 테이블을 "부분 실행 계획(그림의 실행 계획 완료 대상)"의 첫 번째 테이블로 선정
4. 전체 N-1개의 테이블 중(3번에서 선택된 테이블 제외)에서 optimizer_search_depth 시스템 설정 변수에 정의된 개수의 테이블로 가능한 조인 조합을 생성
5. 4번에서 생성된 조인 조합들을 하나씩 3번에서 생성된 "부분 실행 계획"에 대입해 실행 비용을 계산 
6. 5번의 비용 계산 결과, 최적의 실행 계확애소 두 번째 테이블을 3번에서 생성된 "부분 실행 계획"의 두 번째 테이블로 선정 
7. 남은 테이블이 모두 없어질 때까지 4~6번까지의 과정을 반복 실행하면서 "부분 실행 계획"에 테이블의 조인 순서를 기록
8. 최종적으로 "부분 실행 계획"이 테이블의 조인 순서로 결정됨  

<br/>

Greedy 검색 알고리즘은 optimizer_search_depth 시스템 변수에 설정된 값에 따라 조인 최적화의 비용이 줄어들 수 있다. (기본값 62)

MySQL에서는 조인 최적화를 위한 시스템 변수로 optimizer_prune_lovel과 optimizer_search_depth가 제공된다.  

#### optimizer_search_depth 시스템 변수  

Greedy검색 알고리즘과 Exhaustive 검색 알고리즘 중에서 어떤 알고리즘을 사용할지 결정하는 시스템 변수다.  
0~62 정수값을 설정할 수 있는데 1~62는 검색 대상을 지정된 개수로 한정해서 최적의 실행 계획을 산출하고, 0은 최적의 검색 테이블 개수를 옵티마이저가 자동으로 결정한다.  
조인에 사용된 테이블 개수가 optimizer_search_depth 설정값보다 크면 optimizer_search_depth 만큼 Exhaustive 검색이 사용되고, 나머지 테이블은 Greedy 검색이 사용된다.  
조인에 사용된 테이블 개수가 optimizer_search_depth 설정 값보다 작다면 Exhaustive 검색만 사용된다.  

<br/>

#### optimizer_prune_level 시스템 변수
5.0 버전부터 추가된 Heuristic 검색이 작동하는 방식을 제어한다.  
우리가 Exhaustive 검색 알고리즘과 Greedy 검색 알고리즘 중에서 어떤 알고리즘을 사용하더라도 옵티마이저는 여러 테이블의 조인 순서를 결정하기 위해 많은 조인 경로를 비교한다.  
Heuristic 검색의 가장 핵심 내용은 다양한 조인 순서의 비용을 계산하는 도중 이미 계산했던 조인 순서의 비용보다 큰 경우에는 언제든지 중간에 포기할 수 있다는 것이다.  
optimizer_prune_level 값이 1이면 Heuristic 검색을 사용한다. (0으로 설정을 가능하면 하지 말 것)  

<br/>

8.0 버전의 조인 최적화는 많이 개선돼 optimizer_search_depth 변수의 값에는 크게 영향받지 않지만, optimizer_prune_level을 0으로 설정하면 optimizer_search_depth 값 변화에 실행 계획 수립 시간이 급증하는 것을 확인할 수 있다.  

<br/>
<br/>

## 쿼리 힌트

버전이 업그레이드 되며 옵티마이저의 최적화 방법들도 업그레이드 됐지만 MySQL 서버는 우리가 서비스하는 비즈니스를 100% 이해하지는 못한다.  
그래서 개발자나 DBA보다 MySQL 서버가 부족한 실행 계획을 수립하기 위해 옵티마이저 힌트를 제공한다.  

MySQL 서버에서 사용 가능한 쿼리 힌트는 인덱스 힌트, 옵티마이저 힌트로 구분된다.  

<br/>
<br/>

### 인덱스 힌트

"STRAIGHT_JOIN", "USE INDEX"를 포함한 인덱스 힌트들은 모두 MySQL 서버에 옵티마이저 힌트가 도입되기 전에 사용되던 기능들이다.  
이들은 모두 SQL 문법에 맞게 사용해야 하기 때문에 사용하게 되면 ANSI-SQL 표준 문법을 준수하지 못하게 되는 단점이 있다.  
5.6부터 추가되기 시작한 옵티마이저 힌트들은 모두 MySQL 서버를 제외한 다른 RDBMS에서는 주석으로 해석하기 때문에 ANSI-SQL 표준을 준수한다고 볼 수 있다. 가능한 인덱스 힌트(SELECT ,UPDATE에서만 가능)보단 옵티마이저 힌트를 이용하는 것이 좋다.

<br/>
<br/>

### STRAIGHT_JOIN

STRAIGHT_JOIN은 옵티마이저 힌트인 동시에 조인 키워드이기도 하다.  
SELECT, UPDATEm DELETE 쿼리에서 여러 개의 테이블이 조인되는 경우 조인 순서를 고정하는 역할을 한다.  

```sql
-- 3개 테이블을 조인하지만 어느 테이블이 드라이빙 테이블이고 어느 테이블이 드리븐 테이블인지 알 수 없다.
-- 그때그때 각 테이블의 통계 정보와 쿼리의 조건을 기반으로 가장 최적이라고 판단되는 순서로 조인한다. 
EXPLAIN 
SELECT * FROM employees e, dept_emp de, departments d
WHERE e.emp_no = de.emp_no AND d.dept_no = de.dept_no;
```

일반적으로 조인을 하기 위한 컬럼들의 인덱스 여부로 조인의 순서가 결정되며, 조인 컬럼의 인덱스에 아무런 문제가 없는 경우에는(WHERE 조건이 있는 경우 WHERE 조건을 만족하는) 레코드가 적은 테이블을 드라이빙으로 선택한다.  
이 쿼리는 departments 테이블이 레코드 건수가 가장 적어서 드라이빙으로 선택됐을 것으로 보인다.  

이 쿼리의 조인 순서를 변경하려는 경우에는 STARIGHT_JOIN 힌트를 사용할 수 있다. 두 예제 모두 STRAIGHT_JOIN 키워드가 SELECT 키워드 뒤에 사용됐다는 것에 주의하자.  

```sql
EXPLAIN STRAIGHT_JOIN
SELECT * FROM employees e, dept_emp de, departments d
WHERE e.emp_no = de.emp_no AND d.dept_no = de.dept_no;

EXPLAIN /*! STRAIGHT_JOIN */
SELECT * FROM employees e, dept_emp de, departments d
WHERE e.emp_no = de.emp_no AND d.dept_no = de.dept_no;
```

STRAIGHT_JOIN 힌트는 옵티마이저가 FROM 절에 명시된 테이블의 순서대로 조인을 수행하도록 유도한다.  

다음 기준에 맞게 조인 순서가 결정되지 않을 때만 사용하자.  

#### 임시 테이블(인라인 뷰 또는 파생된 테이블)과 일반 테이블의 조인  

이 경우에는 일반적으로 임시 테이블을 드라이빙 테이블로 선정하는 것이 좋다.  
일반 테이블의 조인 컬럼에 인덱스가 없는 경우에는 레코드 건수가 작은 쪽을 먼저 읽도록 드라이빙으로 선택하는 것이 좋은데, 대부분 옵티마이저가 제대로 선택한다. 성능 저하가 심각할 때 사용하자.  

<br/>

#### 임시 테이블끼리 조인  

임시 테이블은 항상 인덱스가 없기 때문에 어느 테이블을 먼저 드라이빙으로 읽어도 무관하므로 크기가 작은 테이블을 드라이빙으로 선택해주는 것이 좋다.  

<br/>

#### 일반 테이블끼리 조인  

양쪽 테이블 모두 조인 컬럼에 인덱스가 있거나 없는 경우에는 레코드 건수가 적은 테이블을 드라이빙으로 선택해주는 것이 좋으며, 그 외의 경우에는 조인 컬럼에 인덱스가 없는 테이블을 드라이빙으로 선택하는 것이 좋다.  

<br/>

언급되는 레코드 건수는 인덱스를 사용할 수 있는 WHERE 조건까지 포함해서 그 조건을 만족하는 레코드 건수다.  

JOIN_FIXED_ORDER 옵티마이저 힌트는 STRAIGHT_JOIN 힌트와 동일한 효과를 낸다.  
JOIN_ORDER, JOIN_PREFIX, JOIN_SUFFIX는 일부 테이블의 조인 순서에 대해서만 제인하는 힌트다.  

<br/>
<br/>

### USE INDEX / FORCE INDEX / IGNORE INDEX

인덱스 힌트는 STRAIGHT_JOIN 힌트와는 달리 사용하려는 인덱스를 가지는 테이블 뒤에 힌트를 명시해야 한다.  
3~4개 이상의 컬럼을 포함하는 비슷한 인덱스가 여러개 존재 시 옵티마이저가 가끔 실수를 하는데 그때 강제로 특정 인덱스를 사용하도록 힌트를 사용한다.  

3종류 인덱스 힌트 모두 키워드 뒤에 사용할 인덱스 이름을 괄호로 묶어서 사용하며, 별도로 사용자가 부여한 이름이 없는 PK는 "PRIMARY"라고 명시하면 된다.  

<br/>

#### USE INDEX  

MySQL 옵티마이저에게 특정 테이블의 인덱스를 사용하도록 권장하는 힌트정도로 생각하면 된다. 힌트가 주어지면 항상 그 인덱스를 사용하는 것은 아니다.  

<br/>

#### FORCE INDEX  

USE INDEX와 다른 점은 없으며, USE 인덱스보다 옵티마이저에게 미치는 영향이 더 강한 힌트로 생각하면 된다.  
USE 인덱스도 충분히 영향력이 커서 잘 사용할 필요가 없다.  

<br/>

#### IGNORE INDEX  

특정 인덱스를 사용하지 못하게 하는 용도로 사용하는 힌트다. 풀 테이블 스캔을 유도할 때 사용할 수도 있다.  

<br/>

위의 인덱스 힌트는 모두 용도를 명시할 수 있다. 용도는 선택 사항으로 명시되지 않으면 주어진 인덱스를 3가지 용도로 사용한다.  

- USE INDEX FOR JOIN: 테이블 간의 조인뿐만 아니라 레코드를 검색하기 위한 용도까지 포함하는 용어다. 서버에서는 하나의 테이블로부터 데이터를 검색하는 작업도 JOIN이라고 표현해서 JOIN이 붙었다. 
- USE INDEX FOR ORDER BY: 명시된 인덱스를 OREDR BY 용도로만 사용할 수 있게 제한한다.  
- USE INDEX GROUP BY: 명시된 인덱스를 GROUP BY 용도로만 사용할 수 있게 제한한다.  

<br/>

대부분 옵티마이저가 최적으로 선택하기 때문에 용도까지는 고려하지 않아도 된다.  

```sql
-- 예제
SELECT * FROM employees WHERE emp_no = 10001;
SELECT * FROM employees FORCE INDEX(primary) WHERE emp_no = 10001;
SELECT * FROM employees USE INDEX(primary) WHERE emp_no = 10001;

SELECT * FROM employees IGNORE INDEX(primary) WHERE emp_no = 10001;
SELECT * FROM employees FORCE INDEX(ix_firstname) WHERE emp_no = 10001;
```

전문 검색(Full Text search) 인덱스가 있는 경우에는 옵티마이저는 다른 일반 보조 인덱스(B-Tree 인덱스)를 사용할 수 있는 상황이라고 하더라도 전문 검색 인덱스를 선택하는 경우가 많다.  
옵티마이저는 PK나 전문 검색 인덱스와 같은 인덱스에 대해서는 선택시 가중치를 두고 실행 계획을 선택하기 때문이다.  

인덱스의 사용법이나 좋은 실행 계획이 어떤 것인지 판단하기 힘든 상황이라면 힌트를 사용해 강제로 옵티마이저의 실행 계획에 영향을 미치는 것은 피하는 것이 좋다.  
지금 PK로 사용하는 것이 좋은 계획이어도 내일은 달라질 수 있기 때문에 가능하다면 그때그때 옵티마이저가 당시 통계정보를 가지고 선택하게 하는 것이 좋다.  

<br/>
<br/>

### SQL_CALC_FOUND_ROWS

MySQL의 LIMIT을 사용하는 경우, 조건을 만족하는 레코드가 LIMIT에 명시된 수보다 많다고 하더라도 명시된 수만큼 만족하는 레코드를 찾으면 즉시 검색 작업을 멈춘다.  
SQL_CALC_FOUND_ROWS 힌트가 포함된 쿼리의 경우에는 LIMIT을 만족하는 수만큼 레코드를 찾았다고 하더라도 끝까지 검색을 수행한다.  
SQL_CALC_FOUND_ROWS가 사용된 쿼리가 실행된 경우 FOUND_ROWS()라는 함스를 이용해 LIMIT을 제외한 조건을 만족하는 레코드가 전체 몇건이었는지를 알아낼 수 있다.  

```sql
-- SELECT SQL_CALC_FOUND_ROWS * FROM employees LIMIT 5;
SELECT SQL_CALC_FOUND_ROWS * FROM employees WHERE first_name = 'Georgi' LIMIT 0, 20;

SELECT FOUND_ROWS() AS total_record_count;
```

이 힌트를 사용하지 말아야 하는 이유를 확인한다.  

위 쿼리에서 한 번위 쿼리 실행으로 필요한 정보 2가지를 모두 가져오는 것처럼 보이지만 FOUND_ROWS() 함수의 실행을 위해 또 한번의 쿼리가 필요하기 때문에 쿼리를 2번 실행해야 한다.  
실제 조건을 만족하는 레코드는 253건 이다. LIMIT 조건이 처음 20건만 가져오도록 했지만 SQL_CALC_FOUND_ROWS 때문에 조건을 만족하는 레코드를 전부 읽어봐야 한다. 그래서 ix_firstname 인덱스를 통해 실제 데이터 레코드를 찾아가는 작업을 253번 실행해야 하며, 디스크 헤드가 특정 위치로 움직일 때까지 기다려야 하는 랜덤 I/O가 253번 일어난다.  

전기적 처리인 메모리나 CPU의 연산 작업에 비해 기계적 처리인 디스크 작업이 얼마나 느린지를 고려하면 SQL_CALC_FOUND_ROWS를 사용하는 경우가 매우 느림을 알 수 있다.  
SELECT 쿼리 문장이 UNION(UNION DISTINCT)으로 연결된 경우 힌트를 사용해도 FOUND_ROWS() 함수로 정확한 레코드 건수를 가져올 수 없다는 것도 문제다.  
결론적으로 성능 향상을 위해 만들어진 힌트가 아니라 개발자 편의를 위해 만들어진 힌트지만 사용하지 않는 것이 좋다.  

<br/>
<br/>

## 옵티마이저 힌트

### 옵티마이저 힌트 종류  
- 인덱스: 특정 인덱스의 이름을 사용할 수 있는 옵티마이저 힌트 
- 테이블: 특정 테이블의 이름을 사용할 수 있는 옵티마이저 힌트
- 쿼리 블록: 특정 쿼리 블록에 사용할 수 있는 옵티마이저 힌트로서 특정 쿼리 블록의 이름을 명시하는 것이 아니라 힌트가 명시된 쿼리 블록에 대해서만 영향을 미치는 옵티마이저 힌트
- 글로벌(쿼리 전체): 전체 쿼리에 대해서 영향을 미치는 힌트

<br/>

이 구분으로 힌트의 사용 위치가 달라지는 것은 아니다. 
힌트에 인덱스 이름이 명시될 수 있는 경우를 인덱스 수준의 힌트로 구분하고, 테이블 이름까지만 명시될 수 있는 경우를 테이블 수준의 힌트로 구분한다.  
또한 특정 힌트는 테이블과 인덱스 이름을 모듀 명시할 수도 있지만 테이블 이름만 명시할 수도 있는데, 이 경우 인덱스와 테이블 수준의 힌트가 된다.  

<br/>

```sql
-- 인덱스 수준의 힌트는 반드시 테이블명이 선행돼야 한다.
EXPLAIN
SELECT /*+ INDEX(employees ix_firstname) */ *
FROM employees
WHERE first_name = 'Matt'
```

하나의 SQL 문장에서 SELECT 키워드는 여러 번 사용될 수 있다. 이때 각 SELECT 키워드로 시작하는 서브쿼리 영역을 쿼리 블록이라고 한다.  
특정 쿼리 블록 내에서 사용될 수도 있지만 외부 쿼리 블록에서 사용할 수도 있다.  
특정 쿼리 블록을 외부 쿼리 블록에서 사용하려면 "QB_NAME()" 힌트를 이용해 해당 쿼리 블록에 이름을 부여해야 한다.  

```sql
-- 서브쿼리에 subq1 이름을 부여하고 쿼리블록을 힌트에 사용
EXPLAIN
SELECT /*+ JOIN_ORDER(e, s@subq1) */ COUNT(*)
FROM employees e
WHERE e.first_name = 'Matt'
AND e.emp_no IN (
    SELECT /*+ QB_NAME(subq1) */ s.emp_no
    FROM salaries s
    WHERE s.salary BETWEEN 50000 AND 50500
);
```

예제에서 서브쿼리에 사용된 salaries 테이블이 세미 조인 최적화를 통해 조인으로 처리될 것을 예상하고 JOIN_ORDER 힌트를 사용한 것이며, 조인의 순서로 외부 쿼리 블록의 employees 테이블과 서브 쿼리 블록의 salaries 테이블을 순서대로 조인하게 힌트를 사용한 것이다.  
이 예제같은 힌트 사용을 일반적이지는 않지만 쿼리 블록에 대한 이름 유여와 그 쿼리 블록 내부의 테이블을 외부 쿼리 불록에서 사용하기 위해서는 이처럼 사용해야 한다.  

<br/>
<br/>

### MAX EXECUTION_TIME

옵티마이저 힌트 중 유일하게 쿼리의 실행 계획에 영향을 미치지 않는 힌트이며, 단순히 쿼리의 최대 실행 시간을 설정하는 힌트다. 밀리초 단위의 시간을 설정하고ㅡ 쿼리가 지정 시간을 초과하면 쿼리는 실패하게 된다.  

```sql
SELECT /*+ MAX_EXECUTION_TIME(100) */ *
FROM employees ORDER BY last_name LIMIT 1;
```

<br/>
<br/>

### SET_VAR

옵티마이저 힌트뿐만 아니라 서버의 시스템 변수들 또한 쿼리의 실행 계획에 상당한 영향을 미친다.  
조인 버퍼 크기를 지정하는 join_buffer_size 시스템 변수의 경우 쿼리에 아무런 영향을 미치지 않을 것처럼 보이지만, 옵티마이저는 조인 퍼버 공간이 충분하면 조인 버퍼를 활용하는 형태의 실행 계획을 선택할 수도 있다.  
그뿐만 아니라 옵티마이저 힌트로 부족한 경우 optimizer_switch 시스템 변수를 제어해야 할 수도 있다. 이런 경우에는 SET_VAR를 사용하면 된다.  

```sql
EXPLAIN
SELECT /*+ SET_VAR(optimizer_switch='index_merge_intersection=off') */ *
FROm employees first_name = 'Georgi' AND emp_no BETWEEN 10000 AND 20000;
```

SET_VAR 힌트는 실행 계획을 바꾸는 용도 뿐만 아니라 조인 버퍼나 정렬용 버퍼(소트 버퍼)의 크기를 일시적으로 증가시켜 대용량 처리 쿼리의 선ㅇ능을 향상시키는 용도로도 사용할 수 있다.  
다양한 시스템 변수 조정을 할 수 있지만 모든 시스템 변수 조정이 가능한건 아니다.  

<br/>
<br/>

### SEMIJOIN & NO_SEMIJOIN 

최적화 전략 | 힌트 
:-- | :--
Duplicate Weed-out | SEMIJOIN(DUPSWEEDOUT)
First Match | SEMIJOIN(FIRSTMATCH)
Loose Scan | SEMIJOIN(LOOSESCAN)
Materialization | SEMIJOIN(MATERIALIZATION)
Table Pull-out | 없음

<br/>

"Table Pull-out" 전략은 그 전략을 사용할 수 있다면 항상 더 나은 성능을 보장하므로 별도 힌트를 사용할 수 없다.  
다른 최적화 저녉은 상황에 따라 다른 전략으로 우회하는 것이 더 나을 수 있기 때문에 NO_SEMIJOIN 힌트도 제공되는 것이다.  

```sql
EXPLAIN
SELECT * FROM departments d
WHERE d.dept_no IN (
    SELECT /*+ SEMIJOIN(MATERIALIZATION) */ de.dept_no 
    FROM dept_emp de
);
```

세미조인 최적화 힌트는 외부 쿼리가 아니라 서브쿼리에 명시해야 한다.  

다른 방법은 우선 서브쿼리에 쿼리 블록 이름을 정의하고 실제 세미 조인 힌트는 외부 쿼리 블록에 명시하는 방법이 있다. 

```sql
EXPLAIN
SELECT /*+ SEMIJOIN(@subq1 MATERIALIZATION)  */ * 
FROM departments d
WHERE d.dept_no IN (
    SELECT /*+ QB_NAME(subq1) */ de.dept_no 
    FROM dept_emp de
);
```

세미 조인 최적화 전략을 사용하지 않으려면 NO_SEMIJOIN 힌트를 명시하면 된다.  


```sql
EXPLAIN
SELECT * FROM departments d
WHERE d.dept_no IN (
    SELECT /*+ NO_SEMIJOIN(DUPSWEEDOUT, FIRSTMATCH) */ de.dept_no 
    FROM dept_emp de
);
```

<br/>
<br/>

### SUBQUERY 

서브쿼리 최적화는 세미 조인 최적화가 사용되지 못할 때 사용하는 최적화 방법이다.  

최적화 방법 | 힌트
:-- | :--
IN-to-EXISTS | SUBQUERY(INTOEXISTS)
Materialization | SUBQUERY(MATERIALIZATION)

<br/>

세미 조인 최적화는 주로 IN(subquery) 형태의 쿼리에 사용될 수 있지만 안티 세미 조인(Anti Semi-Join)의 최적화에는 사용될 수 없다.  
그래서 주로 안티 세미 조인 최적화에는 위의 두 가지 최적화가 사용된다.  
서브쿼리 최적화 힌트는 세미 조인 최적화 힌트와 비슷한 형태로, 서브쿼리에 힌트를 사용하거나 서브쿼리에 쿼리 블록 이름을 지정해서 외부 쿼리 블록에서 최적화 방법을 명시하면 된다.  

<br/>
<br/>

### BNL & NO_BNL & HASHJOIN & NO_HASHJOIN

8.0.18 버전부터 도입된 해시 조인이 블록 NL 조인을 대체하게 됐다.  
8.0.20 버전부터 블록 NL 조인 관련 힌트를 사용하면 해시 조인 힌트를 유도하게 됐다.  
대신 HASHJOIN, NO_HASHJOIN 조인은 8.0.20 버전부터 효력이 없고 BNL, NO_BNL 힌트를 사용해야 한다.  

```sql
SELECT /*+ BNL(e, de) */ *
FROM employees e
JOIN dept_emp de ON de.emp_no = e.emp_no;
```

<br/>
<br/>

### JOIN_FIXED_ORDER & JOIN_ORDER & JOIN_PREFIX & JOIN_SUFFIX

조인 순서를 결정할 때 STRAIGHT_JOIN를 사용했었는데, STRAIGHT_JOIN 힌트는 우선 쿼리의 FROM 절에 사용된 테이블의 순서를 조인 순서에 맞게 변경해야 하는 번거로움이 있다.  
또한 한번 사용되면 FROM 절의 모든 테이블의 조인 순서가 결정됐다.  

이 단점을 보완하기 위해 다음 4개의 힌트를 제공한다.  
- JOIN_FIXED_ORDER: STRAIGHT_JOIN 힌트와 동일하게 FROM 절의 테이블 순서대로 조인을 실행하게 하는 힌트
- JOIN_ORDER: FROM 절에 사용된 테이블의 순서가 아니라 힌트에 명시된 테이블의 순서대로 조인을 실행하는 힌트
- JOIN_PREFIX: 조인에서 드라이빙 테이블만 강제하는 힌트
- JOIN_SUFFIX: 조인에서 드리븐 테이블(마지막에 조인돼야 할 테이블들)만 강제하는 힌트  

<br/>

```sql
SELECT /*+ JOIN_FIXED_ORDER() */ *
FROM employees e
JOIN dept_emp de ON de.emp_no = e.emp_no
JOIN departments ON d.dept_no = de.dept_no;

SELECT /*+ JOIN_ORDER(d, de) */ *
FROM employees e
JOIN dept_emp de ON de.emp_no = e.emp_no
JOIN departments ON d.dept_no = de.dept_no;

SELECT /*+ JOIN_PREFIX(e, de) */ *
FROM employees e
JOIN dept_emp de ON de.emp_no = e.emp_no
JOIN departments ON d.dept_no = de.dept_no;

SELECT /*+ JOIN_SUFFIX(de, e) */ *
FROM employees e
JOIN dept_emp de ON de.emp_no = e.emp_no
JOIN departments ON d.dept_no = de.dept_no;
```

<br/>
<br/>

### MERGE & NO_MERGE

예전 버전의 서버에서는 FROM 절에 사용된 서브쿼리를 항상 내부 임시 테이블로 생성했다. 이 내부 임시 테이블을 파생 테이블이라고 하는데, 불필요한 자원 소모를 유발한다.  
현재 버전에서는 임시 테이블을 사용하지 않게 FROM 절의 서브쿼리를 외부 쿼리와 병합하는 최적화를 도입했다.  

옵티마이저가 내부 쿼리를 외부 쿼리와 병합하는 것이 나을 수도 있고, 때로는 내부 임시 테이블을 생성하는 것이 더 나은 선택일 수도 있다.  
옵티마이저가 최적을 선택하지 못할 때 힌트를 사용하면 된다.  

```sql
EXPLAI
SELECT /*+ MERGE(sub) */ *
FROM (
    SELECT *
    FROM employees
    WHERE first_name = 'Matt'
) sub LIMIT 10;

EXPLAI
SELECT /*+ NO_MERGE(sub) */ *
FROM (
    SELECT *
    FROM employees
    WHERE first_name = 'Matt'
) sub LIMIT 10;
```

<br/>
<br/>

### INDEX_MERGE & NO_INDEX_MERGE

서버는 가능하면 테이블당 하나의 인덱스만으로 쿼리를 처리하려고한다.  
하나의 인덱스만으로 검색 대상 범위를 충분히 좁힐 수 없다면 옵티마이저는 사용 가능한 다른 인덱스를 이용하기도 한다.  
여러 인덱스를 통해 검색된 레코드로부터 교집합 또는 합집합만을 구해서 그 결과를 반환한다.  
이처럼 하나의 테이블에 대해 여러 개의 인덱스를 동시에 사용하는 것을 인덱스 머지라고 한다.  
인덱스 머지 실행 계획은 때로는 성능 향상에 도움되지만 안될 수도 있다.  

```sql
EXPLAIN
SELECT /*+ NO_INDEX_MERGE(employees PRIMARY) */ *
FROm employees first_name = 'Georgi' AND emp_no BETWEEN 10000 AND 20000;

EXPLAIN
SELECT /*+ INDEX_MERGE(employees ix_firstname, PRIMARY) */ *
FROm employees first_name = 'Georgi' AND emp_no BETWEEN 10000 AND 20000;
```

<br/>
<br/>

### NO_ICP

인덱스 컨디션 푸시다운 최적화는 사용 가능하다면 항상 성능 향상에 도움이 되므로 옵티마이저는 최대한 사용하는 방향으로 실행 계획을 수립한다. 그래서 옵티마이저는 ICP 힌트는  제공되지 않는다.  
그런데 인덱스 컨디션 푸시다운으로 인해 여러 실행 계획의 비용 계산이 잘못된다면 결가적으로 잘못된 실행 계획을 수립하게 될 수도 있다.  

A, B 인덱스가 있을 때 A 인덱스를 사용하는 것이 비용이 적게 나왔지만, 실제 서비스에서는 B 인덱스가 효율적일 수 있다.  
이때 인덱스 컨디션 푸시다운 최적화만 비활성화해서 A또는 B 인덱스가 유연하게 선택할 수 있게 할 수 있다.  

```sql
EXPLAIN
SELECT /*+ NO_ICP(employees ix_lastname_firstname) */ *
FROM employees
WHERE last_name = 'Action' AND first_name LIKE '%sal';
```

<br/>
<br/>

### SKIP_SCAN & NO_SKIP_SCAN

인덱스 스킵 스캔은 인덱스의 선행 컬럼에 대한 조건이 없어도 옵티마이저가 해당 인덱스를 사용할 수 있게 해주는 훌륭한 최적화 기능이다.  
하지만 조건이 누락된 선행 컬럼이 가지는 유니크한 값의 개수가 많아진다면 인덱스 스킵 스캔의 성능은 오히려 더 떨어진다.  
옵티마이저가 유니크한 값의 개수를 제대로 분석 못하거나 잘못된 경로로 비효율이 발생하면 인덱스 스킵 스캔을 사용하지 않도록 할 수 있다.

```sql
EXPLAIN
SELECT /*+ NO_SKIP_SCAN(employees ix_gender_birthdate) */ gender, birth_date
FROM employees
WHERE birth_date >= '1965-02-01';
```

### INDEX & NO_INDEX

두 힌트는 예전 MySQL 서버에서 사용되던 인덱스 힌트를 대체하는 용도로 제공된다.  

인덱스 힌트 | 옵티마이저 힌트
:-- | :--
USE INDEX | INDEX
USE INDEX FOR GROUP BY | GROUP_INDEX
USE INDEX FOR ORDER BY | ORDER_INDEX
IGNORE INDEX | NO_INDEX
IGNORE INDEX FOR GROUP BY | NO_GROUP_INDEX
IGNORE INDEX FOR ORDER BY | NO_ORDER_INDEX

<br/>

인덱스 힌트는 특정 테이블 뒤에 사용했기 때문에 별도로 힌트 내에 테이블명 없이 인덱스 이름만 나열했지만, 옵티마이저 힌트에는 테이블명과 인덱스 이름을 함께 명시해야 한다.  

```sql
-- 인덱스 힌트
EXPLAIN
SELECT *
FROM employees USE INDEX(ix_firstname)
WHERE first_name = 'Matt';

-- 옵티마이저 힌트
EXPLAIN
SELECT /*+ INDEX(employees ix_firstname)*/ *
FROM employees
WHERE first_name = 'Matt';
```