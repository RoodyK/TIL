# 실행 계획

DBMS는 많은 데이터를 안전하게 저장 및 관리하고 사용자가 원하는 데이터를 빠르게 조회할 수 있게 해주는 것이 목적이다.  
이를 달성하려면 옵티마이저가 사용자의 쿼리를 최적으로 처리될 수 있게 하는 쿼리의 실행 계획을 수립할 수 있어야 한다.  
옵티마이저가 항상 최적의 실행 계획을 만드는 것은 아니므로 `EXPLAIN` 명령으로 실행 계획을 확인할 수 있게 해준다.  

실행 꼐획을 읽고 이해하려면 서버가 데이터를 처리하는 로직을 이해해야 한다.  

<br/>
<br/>

## 통계 정보

5.7 버전까지는 인덱스에 대한 개괄적인 정보를 가지고 실행 계획을 수립했다.  
이는 테이블 컬럼의 값들이 실제 어떻게 분포돼 있는지에 대한 정보가 없기 때문에 실행 계획의 정확도가 떨어지는 경우가 많았다.  

8.0 버전부터는 인덱스되지 않은 컬럼들에 대해서도 데이터 분포도를 수집해서 저장하는 히스토그램(Histogram) 정보가 도입됐다.  
히스토그램이 있어도 통계정보는 필요하다.  

<br/>
<br/>

### 테이블 및 인덱스 통계 정보

비용 기반 최적화에서 가장 중요한 것은 통계 정보다. 통계 정보가 정확하지 않으면 엉떵한 방향으로 쿼리를 실행할 수 있다.  
1억 건의 레코드가 갱신되지 않아서 10건 미만이것처럼 돼 있다면 옵티마이저는 인덱스를 사용하지 않고 풀 테이블 스캔을 할 것이다.  

MySQL 또한 다른 DBMS와 같이 비용 기반의 최적화를 사용하지만, 다른 DBMS보다 통계 정보의 정확도가 높지 않고 통계 정보의 휘발성이 강했다.  
그래서 쿼리의 실행 계획을 수립할 때 실제 테이블의 데이터를 일부 분석해서 통계 정보를 보완해서 사용했다.  

<br/>
<br/>

### MySQL 서버의 통계 정보

5.5 버전까지는 각 테이블의 통계 정보가 메모리에만 관리되고, SHOW INDEX 명령으로만 테이블의 인덱스 컬럼의 분포도를 볼 수 있었다. (휘발성)  

5.6 버전부터는 InnoDB 스토리지 엔진을 사용하는 테이블에 대한 통계 정보를 영구적으로(Persistent) 관리할 수 있게 개선됐다.  
각 테이블의 통계 정보를 mysql 데이터베이스의 innodb_index_stats와 index_table_stats 테이블로 관리할 수 있게 개선됐다.  

```bash
SHOW TABLES LIKE '%_stats';

+---------------------------+
| Tables_in_mysql (%_stats) |
+---------------------------+
| innodb_index_stats        |
| innodb_table_stats        |
+---------------------------+
```

<br/>

5.6 에서 테이블을 생성할 때는 STATS_PERSISTENT 옵션을 설정할 수 잇는데, 이 값에 따라 테이블 단위로 영구적인 통계 정보를 보관할지 말지를 결정할 수 있다.  

```sql
CREATE TABLE tab_test (
    fd1 INT, 
    fd2 VARCHAR(20),
    PRIMARY KEY(fd1)
) ENGINE=InnoDB STATS_PERSISTENT={ DEFAULT | 0 | 1 }
```

- STATS_PERSISTENT=0: 테이블의 통계 정보를 5.5 이전 방식대로 관리하며 따로 저장 x
- STATS_PERSISTENT=1: 테이블의 통계 정보를 mysql 데이터베이스의 innodb_index_stats와 innodb_table_stats 테이블에 저장
- STATS_PERSISTENT=DEFAULT: 옵션을 설정하지 않은 것과 동일하며, 테이블 통계를 영구적으로 관리할지 말지 innodb_stats_persistent 시스템 변수의 값으로 결정한다.  

<br/>

`innodb_stats_persistent` 시스템 설정 변수는 기본적으로 ON(1)으로 설정돼 있으며, STATS_PERSISTENT 옵션 없이 테이블을 생성하면 영구적인 통계 정보를 사용하면서 `innodb_index_stats` 테이블과 `innodb_stable_stats` 테이블에 통계 정보를 저장한다.  

테이블을 조회하면 `STATS_PERSISTENT=0`이 아닌 값만 통계 정보가 조회 가능한 것을 볼 수 있다.  

```sql
SELECT * FROM innodb_index_stats;
SELECT * FROM innodb_table_stats;
```

<br/>

#### 통계 정보의 각 컬럼 정보  

- innodb_index_stats.stat_name='n_diff_pfx%': 인덱스가 가진 유니크한 값의 개수
- innodb_index_stats.stat_name='n_leaf_pages': 인덱스의 리프 노드 페이지 개수
- innodb_index_stats.stat_name='size': 인덱스 트리의 전체 페이지 개수
- innodb_table_stats.n_rows: 테이블의 전체 레코드 건수
- innodb_table_stats.clustered_index_size: 프라이머리 키의 크기(InnoDB 페이지 개수)
- innodb_table_stats.sum_of_other_index_sizes: 프라이머리 키를 제외한 인데긋의 크기(InnoDB 페이지 개수)

<br/>
<br/>

5.5 버전까지는 테이블 통계 정보가 메모리에 저장되며, 서버 재시작 시 초기화 되어 다시 수집돼야 했다.  
그리고 사용자나 관리자가 알지 못하는 순간 이벤트 발생 시 자동으로 통계 정보가 갱신됐다.  

- 테이블이 새로 오픈되는 경우
- 테이블의 레코드가 대량으로 변경되는 경우(테이블의 전체 레코드 중 1/16 정도의 DML 처리)
- ANALYZE TABLE 명령이 실행되는 경우
- SHOW TABLE STATUS 명령이나 SHOW INDEX FROM 명령이 실행되는 경우
- InnoDB 모니터가 활성화되는 경우
- innodb_stats_on_metadata 시스템 설정이 ON인 상태에서 SHOW TABLE STATUS 명령이 실행되는 경우

이렇게 자주 테이블의 통계 정보가 갱신되면 응용 프로그램의 쿼리를 인덱스 레인지 스캔으로 잘 처리하던 서버가 갑자기 풀 테이블 스캔으로 실행되는 상황이 발생할 수도 있다.  
이 문제를 영구적인 통계 정보가 도입되면서 이렇게 의도하지 않은 통계 정보 변경을 막을 수 있게 됐다.  
innodb_stats_auto_recalc 시스템 설정 변수 값(기본값 ON)을 OFF로 설정해서 통계 정보가 자동으로 갱신되는 것을 막을 수 있다.  
테이블을 생성할때 STATS_AUTO_RECALC 옵션을 이용해서 통계 정보를 자동으로 수집할지 여부도 테이블 단위로 조정할 수 있다.  

- STATS_AUTO_RECALC=1: 테이블 통계 정보를 MySQL 5.5 이전 방식대로 자동 수집한다. 
- STATS_AUTO_RECALC=1: 테이블 통꼐 정보는 ANALYZE TABLE 명령을 실행할 때만 수집된다.
- STATS_AUTO_RECALC=DEFAULT: 테이블을 생성할 때 별도로 STATS_AUTO_RECALC 옵션을 설정하지 않은 것과 동일하며, 테이블 통꼐 정보 수집을 innodb_stats_auto_recalc 시스템 설정 변수의 값으로 결정한다.  

<br/>

영구적인 통계 정보를 사용하면 서버의 점검이나 사용량이 많지 않은 시간을 이용해 더 정확한 통계 정보를 수집할 수도 있다. 더 정확한 통계 정보 수집에는 많은 시간이 소요되겠지만, 이 통계 정보의 정확성에 의해 쿼리의 성능이 결정되기 때문에 시간을 투자할 가치가 있다.  
정확한 통계 정보를 수집하고자 한다면 `innodb_stats_persistent_sample_pages` 시스템 변수 값을 높이면 된다.  

<br/>
<br/>

### 히스토그램(Histogram)

5.7 버전까지는 통계 정보는 단순히 인덱스된 컬럼의 유니크한 값 개수 정도만 가지고 있어서, 옵티마이저가 최적의 실행 계획을 수립하긴 부족했다.  

8.0 버전부터 컬럼의 데이터 분포도를 참조할 수 있는 히스토그램 정보를 활용할 수 있게 됐다.  

<br/>
<br/>

### 히스토그램 정보 수집 및 삭제

히스토그램 정보는 컬럼 단위로 관리되는데, 이는 자동으로 수집되지 않고 `ANALYZE TABLE ... UPDATE HISTOGRAM` 명령을 실행해 수동으로 수집 및 관리된다.  
수집된 히스토그램 정보는 시스템 딕셔너리에 함께 저장되고, MySQL 서버가 시작될 때 딕셔너리의 히스토그램 정보 information_schema 데이터베이스의 column_statistics 테이블로 로드한다.  

```sql
ANALSE TABLE emp.employees
UPDATE HISTOGRAM ON gender, hire_date;

SELECT * 
FROM information_schema.COLUMN_STATISTICS
WHERE SCHEMA_NAME='emp' AND TABLE_NAME='employees';
```

8.0 버전에는 2종류의 히스토그램 타입을 지원한다.

- Singleton(싱글톤 히스토그램): 컬럼값 개별로 레코드 건수를 관리하는 히스토그램으로, Value-Based 히스토그램 또는 도수 분포도라고도 불린다.  
- Equi-Height(높이 균형 히스토그램): 컬럼값의 범위를 균등한 개수로 구분해서 관리하는 히스토그램으로, Height-Balanced 히스토그램이라고도 불린다.  

<br/>

히스토그램은 버킷(Bucket) 단위로 구분되어 레코드 건수나 컬럼값의 범위가 관리된다.  
싱글톤 히스토그램은 컬럼이 가지는 값별로 버킷이 할당되며 높이 균형 히스토그램에서는 개수가 균등한 컬럼의 값이 범위별로 하나의 버킷이 할당된다.  
싱글톤 히스토그램은 각 버킷이 컬럼의 값과 발생 빈도의 비율의 2개 값을 가지며, 높이 균형 히스토그램은 각 버킷이 범위 시작 값과 마지막 값, 발생 빈도율과 각 버킷에 포함된 유니크한 값의 개수 등 4개의 값을 가진다.  

<br/>

#### gender, hire_date 컬럼의 히스토그램 데이터

![singleton-histogram](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FrrgFY%2FbtrN666y1Mi%2FHCIRcxtbDCvKvhtkGeuzRK%2Fimg.png)  
싱글톤 히스토그램

<br/>

![singleton-histogram](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbDBCrl%2FbtrN79onByZ%2FlF8LHBDZ5hRFu7JwbAQOg0%2Fimg.png)  
높이 균형 히스토그램

<br/>

싱글톤 히스토그램은 주로 코드 값과 같이 유니크한 값의 개수가 상대적으로 적은(히스토그램의 버킷 수보다 적은) 경우 사용된다.  

높이 균형 히스토그램은 컬럼값의 각 범위에 대해 레코드 건수 비율이 누적으로 표시된다.  
히스토그램의 버킷 범위가 뒤로 갈수록 비율이 높아지는 것으로 보이지만, 사실 범위별로 비율이 같은 수준에서 hire_date 컬럼의 범위가 선택된 것이다.  

information_schema.column_statistics 테이블의 HISTOGRAM 컬럼이 가진 나머지 필드  
- sampling-rate: 히스토그램 정보를 수집하기 위해 스캔한 페이지의 비율을 저장한다. 샘플링 비율이 0.35%라면 전체 데이터 페이지의 35%를 스캔해서 이 정보가 수집됐다는 것을 의미한다. 샘플링 비율이 높을수록 정확하지만 시스템 자원을 많이 소모하고 부하가 높다.  
- histogram-type: 히스토그램의 종류를 저장한다. 
- number-of-buckets-specified: 히스토그램을 생성할 때 설정했던 버킷의 개수를 저장한다. 버킷의 기본 개수는 100개이며 최대 1024개 설정 가능하다.  

<br/>

```sql
-- 히스토그램 제거
ANALYZE TABLE emp.employees
DROP HISTOGRAM ON gender, hire_date;

-- 히스토그램을 삭제하지 않고 옵티마이저가 히스토그램을 사용하지 않게 변경
-- 영향받는 다른 최적화 기능들이 사용되지 않을 수 있으므로 주의해야 함
SET GLOBAL optimizer_switch ='condition_fanout_filter=off';
SET SESSION optimizer_switch ='condition_fanout_filter=off';

-- 현재 쿼리만 히스토그램을 사용하지 않도록 변경
SELECT /*+ SET_VAR(optimizer_switch='condition_fanout_filter=off') */ +
FROM ...
```

<br/>
<br/>

### 히스토그램의 용도

히스토그램 도입 이전에도 테이블과 인덱스에 대한 통계정보는 존재했지만, 기존 서버가 가지고 있던 통계 정보는 테이블의 전체 레코드 건수와 인덱스된 컬럼이 가지는 유니크한 값의 개수 정도였다.  

실제 응용프로그램의 데이터는 항상 균등한 분포도를 가지지 않는다. 어떤 사용자는 컬럼의 값이 존재할수도 없을 수도 있다. 이런 단점을 보완하기 위해 히스토그램이 도입됐다.  
히스토그램은 특정 컬럼이 가지는 모든 값에 대한 분포도 정보를 가지지는 않지만 각 범위(버킷)별로 레코드의 건수와 유니크한 값의 개수 정보를 가지기 때문에 전보다 정확한 예측을 할 수 있다.  

employees 테이블의 birth_date 컬럼에 대해 히스토그램 유무에 따른 예측치를 확인해본다.  
```sql
-- 히스토그램 생성 전 후 확인해보기
ANALYZE TABLE employees 
UPDATE HISTOGRAM ON first_name, birth_date

EXPLAIN 
SELECT * FROM employees
WHERE first_name='Zita'
AND birth_date BETWEEN '1950-01-01' AND '1960-01-01'
```

히스토그램 생성 전은 `filtered` 값이 11.11이 나왔고 생성 후는 60.94가 나왔다.  
실제 실행 계획은 대략 224명중 63.84%인 143명이 1950년대 출생임을 확인할 수 있다.  
단순 통계 정보만 봐도 히스토그램 생성 전후 차이가 매우 크다.  

히스토그램 정보가 없으면 옵티마이저는 데이터가 균등하게 분포돼 있을 것으로 예측한다. 하지만 히스토그램이 있으면 특정 범위의 데이터가 많고 적음을 식별할 수 있다.  

히스토그램이 없으면 옵티마이저는 테이블의 전체 레코드 건수나 크기 등 단순한 정보만으로 조인의 드라이빙 테이블을 결정하게 되어 쿼리의 성능 차이가 많이 날 수 있다.  
InnoDB 버퍼 풀에 데이터가 존재하지 않아서 디스크에서 데이터를 읽어야 하는 경우라면 몇 배의 차이가 발생할 수 도 있다.  

<br/>
<br/>

### 히스토그램과 인덱스

히스토그램과 인덱스는 완전히 다른 객체라 비교 대상은 아니지만, 서버에서 인덱스는 부족한 통계 정보를 수집하기 위해 사용된다는 측면에서 어느 정도 공통점을 가진다고 볼 수 있다.  
실행 계획에서 조건절에 일치하는 레코드 건수를 예측하기 위해 옵티마이저는 실제 인덱스의 B-Tree를 샘플링해서 살펴본다. 이 작업을 메뉴얼에서는 "인덱스 다이브"라고 한다.  

MySQL 서버에서는 인덱스된 컬럼을 검색 조건으로 사용하는 경우 그 컬럼의 히스토그램을 사용하지 않고 실제 인덱스 다이브를 통해 직접 수집한 정보를 활용한다.  
이는 실제 검색 조건의 대상 값에 대한 샘플링을 실행하는 것이므로 항상 히스토그램보다 정확한 결과를 기대할 수 있기 때문이다.  
히스토그램은 주로 인덱스되지 않은 컬럼에 대한 분포도를 참조하는 용도로 사용된다.  

인덱스 다이브 작업은 어느 정도 비용이 필요하며. 때로는 (IN절 값이 많이 명시된 경우) 실행 꼐획 수립만으로도 상당한 인덱스 다이브를 실행하고 비용도 커진다. (조만간 인덱스 다이브 실행보다 히스토그램을 활용하는 최적화 기능이 생길 수도 있다.)  

<br/>
<br/>

### 코스트 모델(Cost Model)

MySQL 서버가 쿼리를 처리하려면 다음 작업들을 필요로 한다.  

- 디스크로부터 데이터 페이지 읽기
- 메모리(InnoDB 버퍼 풀)로부터 데이터 페이지 읽기
- 인덱스 키 비교
- 레코드 평가
- 메모리 임시 테이블 작업
- 디스크 임시 테이블 작업

서버는 사용자의 쿼리에 대해 위 작업이 얼마나 필요한지 예측하고 전체 작업 비용을 계산한 결과를 바탕으로 최적의 실행 계획을 찾는다.  
전체 쿼리 비용을 계산하는데 필요한 단위 작업들의 비용을 코스트 모델이라고 한다.  

5.7 이전에는 작업들의 비용을 서버 소스 코드에 상수화해서 사용했지만 현재는 상수화 돼있던 각 단위의 비용을 DBMS 관리자가 조정할 수 있게 개선됐지만 메모리에 상주 중인 페이지의 비율 등 비용 계산 관련 정보가 부족했다.  
8.0 버전부터는 컬럼의 데이터 분포를 위한 히스토그램과 각 인덱스별 메모리에 적재된 페이지 비율이 관리되고 옵티마이저의 실행 계획 수립에 사용되기 시작했다.  

MySQL 서버의 코스트 모델은 다음 2개의 테이블에 저장돼 있는 설정값을 사용(mysql DB에 존재)  

- server_cost: 인덱스를 찾고 레코드를 비교하고 임시 테이블 처리에 대한 비용 관리
- engine_cost: 레코드를 가진 데이터 페이지를 가져오는데 필요한 비용 관리

```sql
use mysql;
SELECT * FROM server_cost;
SELECT * FROM engine_cost;
```

<br/>

server_cost 테이블과 engine_cost 테이블은 공통으로 다음 컬럼을 갖는다.

- cost_name: 코스트 모델의 각 단위 작업
- default_value: 각 단위 작업의 비용(기본값이며, 이 값은 MySQL 서버 소스 코드에 설정된 값)
- DBMS 관리자가 설정한 값(이 값이 NULL이면 서버는 default_value 컬럼의 비용 사용)
- last_updated(단순 정보성 컬럼): 단위 작업의 비용이 변경된 시점
- commsent(단순 정보성 컬럼): 비용에 대한 추가 설명

<br/>

engine_cost 테이블의 추가 컬럼
- engine_name: 비용이 적용된 스토리지 엔진
- device_type: 디스크 타입 

<br/>

engine_name 컬럼은 스토리지 엔진(MEMORY, MyISAM, InnoDB)별로 각 단위 작업의 비용을 설정할 수 있다.(기본값: default)  
default는 특정 스토리지 엔진의 비용이 설정되지 않았다면 해당 스토리지 엔진의 비용으로 이 값을 적용한다.  
`device_type`은 디스크 타입을 설정할 수 있는데, 아직 8.0 버전에서는 사용하지 않아 0만 설정할 수 있다.

#### 코스트 모델에서 지원하는 작업 단위  

<table>
    <thead>
        <tr>
            <th></th>
            <th>cost_name</th>
            <th>default_value</th>
            <th>설명</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan="2">engine_cost</td>
            <td>io_block_read_cost</td>
            <td>1</td>
            <td>디스크 데이터 페이지 읽기</td>
        </tr>
        <tr>
            <td>memory_block_read_cost</td>
            <td>0.25</td>
            <td>메모리 데이터 페이지 읽기</td>
        </tr>
        <tr>
            <td rowspan="6">server_cost</td>
            <td>disk_temptable_cost</td>
            <td>20</td>
            <td>디스크 임시 테이블 생성</td>
        </tr>
        <tr>
            <td>disk_temptable_row_cost</td>
            <td>0.5</td>
            <td>디스크 임시 테이블의 레코드 읽기</td>
        </tr>
        <tr>
            <td>key_compare_cost</td>
            <td>0.05</td>
            <td>인덱스 키 비교</td>
        </tr>
        <tr>
            <td>memory_temptable_create_cost</td>
            <td>1</td>
            <td>메모리 임시 테이블 생성</td>
        </tr>
        <tr>
            <td>memory_temptable_row_cost</td>
            <td>0.1</td>
            <td>메모리 임시 테이블의 레코드 읽기</td>
        </tr>
        <tr>
            <td>row_evaluate_cost</td>
            <td>0.1</td>
            <td>레코드 비교</td>
        </tr>
    </tbody>
</table>

<br/>

row_evaluate_cost는 스토리지 엔진이 반환한 레코드가 쿼리의 조건에 일치하는지를 평가하는 단위 작업을 의미하는데, 이 값이 증가할수록 풀 테이블 스캔과 같이 많은 레코드를 처리하는 쿼리의 비용이 높아지고, 레인지 스캔과 같이 상대적으로 적은 수의 레코드를 처리하는 쿼리의 비용이 낮아진다.  
key_compare_cost는 키 값의 비교 작업에 필요한 비용을 의미하는데, 이 값이 증가할수록 레코드 정렬과 같이 키 값 비교 처리가 많은 경우 쿼리의 비용이 높아진다.  

```sql
-- 서버에서 각 실행계획의 계산된 비용(Cost)
EXPLAIN FORMAT=TREE
SELECT * FROM employees WHERE first_name = 'Matt';

EXPLAIN FORMAT=JSON
SELECT * FROM employees WHERE first_name = 'Matt';
```

<br/>

각 작업 비용을 이용해 서버의 실행 계획을 직접 계산하는 것은 어렵다.  
코스트 모델에서 중요한 것은 각 단위 작업에 설정되는 비용 값이 커지면 어떤 실행 계획들이 고비용으로 바뀌고 어떤 실행 계획들이 저비용으로 바뀌는지를 파악하는 것이다.  

각 단위 작업의 비용이 변경되면 예살할 수 있는 결과의 일부  

- key_compare_cost 비용을 높이면 MySQL 서버 옵티마이저 가능하면 정렬을 수행하지는 않는 방향의 실행 계획을 선택할 가능성이 높아진다.  
- row_evaluate_cost 비용을 높이면 풀 스캔을 실행하는 쿼리들의 비용이 높아지고 MySQL 서버 옵티마이저는 가능하면 인덱스 레인지 스캔을 사용하는 실행 계획을 선택할 가능성이 높아진다.  
- disk_temptable_create_cost와 disk_temptable_row_cost 비용을 높이면 MySQL 옵티마이저는 디스크에 임 시 테이블을 만들지 않는 방향의 실행 계획을 선택할 가능성이 높아진다.
- memory_temptable_create_cost와 memory_temptable_row_cost 비용을 높이면 MySQL 서버 옵티마이저는 메모리 임시 테이블을 만들지 않는 방향의 실행 계획을 선택할 가능성이 높아진다.
- io_block_read_cost 비용이 높아지면 MySQL 서버 옵티마이저는 가능하면 InnoDB 버퍼 풀에 데이터 페이지가 많이 적재돼 있는 인덱스를 사용하는 실행 계획을 선택할 가능성이 높아진다.  
- memory_block_read_cost 비용이 높아지면 MySQL 서버는 InnoDB 버퍼 풀에 적재된 데이터 페이지가 상대적으로 적다고 하더라도 그 인덱스를 시용할 가능성이 높아진다.  

<br/>

각 단위 작업의 비용을 사용자가 변경할 수 있는 기능을 제공한다고 꼭 바꿔서 사용해야 하는 것은 아니다. (전문 지식 필요)  

<br/>
<br/>

## 실행 계획 확인 

실행 계획은 DESC 또는 EXPLAIN 명령으로 확인할 ㅅ ㅜ있다.  

<br/>
<br/>

### 실행 계획 출력 포맷

이전 버전은 `EXPLAIN EXTENDED`, `EXPLAIN PARTITIONS` 명령이 구분되어 있었다.  
8.0 버전부터는 EXPLAIN으로 통합되고 FORMAT 옵션을 사용해 실행 계획 표시방법을 JSON이나 TREE, 단순 테이블 형태로 선택할 수 있다.  

```sql
-- 단순 테이블
EXPLAIN SELECT ...

-- 트리 포맷
EXPLAIN FORMAT=TREE SELECT ...

-- JSON 포맷
EXPLAIN FORMAT=JSON SELECT ...
```

<br/>
<br/>

### 쿼리의 실행 시간 확인

8.0.18 버전부터 실행 계획과 단계별 소요된 시간 정보를 알 수 있는 `EXPLAIN ANALYZE` 기능이 추가됐다.  
`SHOW PROFILE` 명령으로 어떤 부분에서 시간이 많이 소요되는지 확인 가능하시만 단계별로 소요된 시간 정보를 보여주진 않는다.  
`EXPLAIN ANALYZE` 명령은 항상 결과를 Tree 포맷으로 보여주기 때문에 FORMAT 옵션은 사용할 수 없다.  

```sql
EXECUTE ANALYZE SELECT ...
```

<br/>

TREE 포맷의 실행 계획에서 들여쓰기는 호출 순서를 의미한다.  
- 들여쓰기가 같은 레벨에서 상단에 위치한 라인이 먼저 실행
- 들여쓰기가 다른 레벨에서는 가장 안쪽에 위치한 라인이 먼저 실행  

<br/>

```sql
EXPLAIN ANALYZE
SELECT e.hire_date, avg(s.salary)
FROM employees e
JOIN salaries s 
ON e.emp_no = s.emp_no 
AND s.salary > 50000 
AND s.from_date <= '1990-01-01'
AND s.to_date = '1990-01-01'
WHERE e.first_name='Matt'
GROUP BY e.hire_date;
```
<br/>

실행 계획 수행 후 각 필드의 의미 확인  

#### actual   time=0.001..0.010

테이블에서 읽은 레코들 검색하는 데 걸린 시간(밀리초)을 의미한다. 이 때 첫 번째 식은 첫 번쩨 걸린 시간이고, 두 번째는 마지막 레코드를 가져오는 데 걸린 시간이다.  

#### rows

테이블에서 읽은 조건에 일치하는 평균 레코드 건수를 의미한다.  

#### loops  

테이블에서 읽은 레코드를 찾는 작업이 반복된 휫수

<br/>

`EXPLAIN ANALYZE` 명령은 EXPLAIN 명령과 달리 실행 계획만 추출하는 것이 아니라 실제 쿼리를 실행하고 사용된 실행 계획과 소요된 시간을 보여주는 것이다.  
`EXPLAIN` 으로 실행 계획만 확인해서 어느 정도 튜닝 후 `EXPLAIN ANALYZE`를 사용하는 것이 좋다.  

<br/>
<br/>

## 실행 계획 분석

`EXPLAIN` 명령으로 기존 테이블 포맷으로 출력되던 실행 계획을 이해할 수 있다면 포맷을 사용해도 어렵지 않게 실행 계획을 이해할 수 있을 것이다.  

아무 옵션 없이 `EXPLAIN` 명령을 실행하면 쿼리 문장의 특성에 따라 표 형태로 된 결과가 표시된다.  
표의 각 레코드는 쿼리 문장에서 사용된 테이블(서브쿼리 사용 시 임시테이블 포함)의 개수만큼 출력된다.  
실행 순서는 위에서 아래로 순서대로 표시된다.(UNION이나 상호연관 서브쿼리는 순서대로가 아닐 수 있음)  
출력된 실행 계획에서 위쪽 출력된 결과일 수록(id 값이 작을 수록) 쿼리의 바깥(outer) 부분이거나 먼저 접근한 테이블이고, 아래쪽 출력된 결과일수록 쿼리의 안쪽(inner) 부분 또는 나중에 접근한 테이블에 해당한다.  

<br/>
<br/>

### id 컬럼

```sql
SELECT ...
FROM (SELECT ... FROM tb_test1) tb1, tb_test2 tb2
WHERE tb1.id = tb2.id;

SELECT ... FROM tb_test1

SELECT ... FROM tb1, tb_test2 tb2
WHERE tb1.id = tb2.id;
```

쿼리를 SELECT 키워드 단위로 구분한 것을 단위(SELECT) 쿼리라고 표현하겠다.  

실행 계획에서 id 컬럼은 단위 SELECT 쿼리별로 부여되는 식별자 값이다.  
여러 테이블이 조인되는 경우 조인되는 테이블의 개수만큼 실행 계획 레코드가 출력되지만 id 값이 부여된다.  

주의해야 할 것은 실행 계획으 id 컬럼이 테이블 접근 순서를 의미하지 않는다는 것이다.  

```sql
EXPLAIN -- FORMAT=TREE
SELECT * FROM dept_emp de
WHERE de.emp_no = (
    SELECT e.emp_no FROM employees e 
    WHERE e.first_name = 'Georgi'
    AND e.last_name = 'Facello' LIMIT 1
);
```

실제 employees 테이블을 읽은 결과로 dept_emp 테이블을 조회하는데 id 값은 순서대로 나타나지 않은 것을 알 수 있다.  

<br/>
<br/>

### select_type 컬럼

각 단위 SELECT 쿼리가 어떤 타입의 쿼리인지 표시되는 컬럼이다.  

#### SIMPLE  

UNION이나 서브쿼리를 사용하지 않는 단순한 SELECT 쿼리인 경우 표시된다. (조인이 포함된 경우도 마찬가지)  
쿼리 문장이 아무리 복잡해도 실행 계획에서 select_type이 SIMPLE인 단위 쿼리는 하나만 존재한다.  

<br/>

#### PRIMARY  

UNION이나 서브쿼리를 가지는 SELECT 쿼리의 실행 계획에서 가장 바깥쪽에 있는 단위 쿼리인 경우 표시된다.  
SIMPLE과 마찬가지로 select_type이 PRIMARY인 단위 쿼리는 하나만 존재한다.  

<br/>

#### UNION  

UNION으로 결합하는 단위 SELECT 쿼리 가운데 첫 번째를 제외한 두 번째 이후 단위 쿼리인 경우 표시된다.  
UNION의 첫 번째 단위 SELECT는 select_type이 UNION이 아니라 UNION되는 쿼리 결과들을 모아서 저장하는 임시 테이블(DERIVED) select_type으로 표시된다.  

```sql
EXPLAIN
SELECT * FROM (
    (SELECT emp_no FROM employees e1 LIMIT 5) UNION ALL
    (SELECT emp_no FROM employees e2 LIMIT 5) UNION ALL
    (SELECT emp_no FROM employees e3 LIMIT 5) 
) tb
```

위 쿼리의 실행 계획은 UNION이 되는 단위 쿼리 3개 중에서 첫 번째 테이블(e1)만 UNION이 아니고 나머지 2개는 모두 UNION이 표시된다.  
첫 번째 쿼리는 전체 UNION의 결과를 대표하는 select_type으로 설정됐다.  
세 개의 서브쿼리로 조회된 결과를 UNION ALL로 결합해 임시 테이블을 만들어서 사용하고 있으므로 UNION ALL의 첫 번째 쿼리는 DERIVED select_type을 갖는 것이다.  

<br/>

#### DEPENDENT UNION  

DEPENDENT UNION 또한 UNION과 같이 UNION ALL로 집합을 결합하는 쿼리에 표시된다.  
DEPENDENT는 UNION이나 UNION ALL로 결합된 단위 쿼리가 외부 쿼리에 의해 영향을 받는 것을 의미한다.  

```sql
EXPLAIN
SELECT *
FROM employees e1 WHERE e1.emp_no IN (
    SELECT e2.emp_no FROM employees e2 WHERE e2.first_name='Matt' 
    UNION
    SELECT e3.emp_no FROM employees e3 WHERE e3.first_name='Matt' 
);
```

위 쿼리의 경우 옵티마이저는 IN 내부의 서브 쿼리를 먼저 처리하지 않고, 외부의 e1 테이블을 먼저 읽은 다음 서브쿼리를 실행하는데 이때 employees 테이블의 컬럼값이 서브쿼리에 영향을 준다.  
이렇게 내부 쿼리가 외부의 값을 참조해서 처리될 때 DEPENDENT 키워드가 표시된다.  

내부적으로 UNION에 사용된 SELECT 쿼리의 WHERE 조건에 `e2.emp_no=e1.emp_no`와 `e3.emp_no=e1.emp_no`라는 조건이 자동 추가되어 실행된다.  
외부에 정의된 employees 테이블의 emp_no 컬럼이 서브 쿼리에 사용되기 때문에 DEPENDENT UNION이 표시된 것이다.  

<br/>

#### UNION RESULT  

UNION 결과를 담아두는 테이블을 의미한다.  
8.0 이전에는 UNION, UNION(= UNION DISTINCT),  쿼리는 모두 UNION의 결과를 임시 테이블로 생성했다.  
8.0 버전부터는 UNION ALL의 경우 임시 테이블을 사용하지 않도록 개선됐다.  
실행 계획상에서 임시 테이블을 가리키는 라인의 select_type이 UNION RESULT다. 
UNION RESULT는 실제 쿼리에서 단위 쿼리가 아니기 때문에 별도의 id 값은 부여되지 않는다.  

```sql
EXPLAIN
SELECT emp_no FROM salaries WHERE salary > 100000
UNION
SELECT emp_no FROM dept_emp WHERE from_date > '2001-01-01';
```

실행 계획에서 `UNION RESULT` 라인의 table 컬럼은 `<union1,2>`로 표시돼 있는데, id 값이 1인 단위 쿼리 조회 결과와 id 값이 2인 단위 쿼리의 조회 결과를 UNION 했다는 의미다.  

예제에서 UNION을 UNION ALL로 변경하면 `UNION RESULT`가 사라짐을 알 수 있다.  

<br/>

#### SUBQUERY

select_type의 SUBQUERY는 FROM 절 이외에서 사용되는 서브쿼리만을 의미한다.  

```sql
EXPLAIN
SELECT e.first_name, (SELECT COUNT(*) FROM dept_emp de, dept_manager dm WHERE dm.dept_no=de.dept_no) AS cnt
FROM employees e WHERE e.emp_no = 10001;
```

실행 계획에서 FROM 절에 사용된 서브쿼리는 select_type이 DERIVED(파생 테이블)로 표시되고, 그 밖에는 SUBQUERY로 표시된다.  

<br/>

#### DEPENDENT SUBQUERY  

서브쿼리 바깥쪽 SELECT 쿼리에 정의된 컬럼을 사용하는 경우에 표시된다.  

```sql
EXPLAIN
SELECT 
	e.first_name, 
	(SELECT COUNT(*) FROM dept_emp de, dept_manager dm WHERE dm.dept_no=de.dept_no AND de.emp_no=e.emp_no) AS cnt
FROM employees e WHERE e.first_name = 'Matt';
```

예제 쿼리처럼 안쪽의 서브쿼리 결과가 바깥쪽 SELECT 쿼리의 컬럼에 의존적이기 때문에 DEPENDENT 키워드가 붙는다.  
그리고 외부쿼리 수행 후 내부 쿼리가 실행되야 하므로 DEPENDENT 키워드가 없는 일반 서브쿼리보다 처리 속도가 느릴때가 많다. 

<br/>

#### DERIVED  

5.5 버전까지는 서브쿼리가 FROM 절에 사용된 경우 항상 DERIVED인 실행 계획을 만든다.  
5.6부터 옵티마이저 옵션(optimizer_swtich)에 따라 FROM 절의 서브쿼리를 외부 쿼리와 통합하는 형태의 최적화가 수행되기도 한다.  
DERIVED는 단위 SELECT 쿼리의 실행 결과로 메모리나 디스크에 임시 테이블을 생성하는 것을 의마한다.  
5.6 버전부터는 옵티마이저 옵션에 ㄷ따라 쿼리의 특성에 맞게 임시 테이블에도 인덱스를 추가해서 만들 수 있게 최적화 됐다.  

```sql
EXPLAIN
SELECT * FROM (
    SELECT de.emp_no FROM dept_emp de GROUP BY de.emp_no
) tb, employees e
WHERE e.emp_no = tb.emp_no;
```

예제는 서브쿼리를 없에고 조인 처리할 수 있지만 임시 테이블을 확인하기 위함이다.  

MySQL 서버는 버전이 업그레이드되면서 조인 쿼리에 대한 최적화는 많이 성숙된 상태다.  
파생 테이블 형태의 실행 계획을 조인으로 해결살 우 있게 쿼리를 바꿔주는 것이 좋다.  

<br/>

#### DEPENDENT DERIVED  

8.0 이전에는 FROM 절의 서브쿼리는 외부 컬럼을 사용할 수가 없었다.  
8.0 버전부터는 래터럴 조인(LATERAL JOIN) 기능이 추가되면서 FROM 절의 서브쿼리에서도 외부 컬럼을 참조할 수 있게 됐다.  

```sql
SELECT *
FROM employees e
LEFT JOIN LATERAL (
    SELECT * FROM salaries
    WHERE s.emp_no = e.emp_no
    ORDER BY s.from_date DESC LIMIT 2) AS s2 
ON s2.emp_no = e.emp_no;
```

래터럴 조인의 경우 `LATERAL` 키워드를 사용해야 하며, `LATERAL` 키워드가 없는 서브쿼리에서 외부 컬럼을 사용하면 오류가 발생한다.  

<br/>

#### UNCACHEABLE SUBQUERY  
하나의 쿼리 문장에 서브쿼리가 하나만 있더라도 실제 그 서브쿼리가 한 번만 실행되는 것은 아니다.  
그런데 조건이 똑같은 서브쿼리가 실행될 때는 다시 실행하지 않고 이전 실행 결과를 그대로 사용할 수 있게 서브쿼리의 결과를 내부적인 캐시 공간에 담아둔다.  
여기서 서브쿼리 캐시는 쿼리 캐시나 파생 테이블과는 전혀 무관한 기능이다.  

- SUBQUERY 는 바깥쪽의 영향을 받지 않으므로 처음 한 번만 실행해서 그 결과를 캐시하고 필요할 때 캐시된 결과를 이용한다.  
- DEPENDENT SUBQUERY는 의존하는 바깥쪽 쿼리의 컬럼의 값 단위로 캐시해두고 사용한다.  

![cache](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FBt4W6%2FbtrZxeTJ0wQ%2FhZeLObKks5kB9DtX7K9YE0%2Fimg.png)  
SUBQUERY의 결과 캐시

<br/>

그림에서 캐시는 처음 한번만 생성됨을 알 수 있다. 하지만 DEPENDENT SUBQUERY는 서브 쿼리 결과가 그림 같이 캐시는 되지만, 딱 한버만 캐시되는 것이 아니라 위부 쿼리의 값 단위로 캐시가 만들어지는 방식으로 처리된다.  

select_type이 SUBQUERY인 경우와 UNCACHEABLE SUBQUERY는 이 캐시를 사용할 수 있느냐 없느냐의 차이가 있다.  
서브쿼리에 포함된 요소에 의해 캐시 자체가 불가능할 수 있는데, 이때 `UNCACHEABLE SUBQUERY`로 표시된다.  

캐시를 사용하지 못하는 대표적인 예이다.  
- 사용자 변수가 서브쿼리에 사용된 경우
- NOT-DETERMINISTIC 속성의 스토어드 루틴이 서브쿼리 내에 사용된 경우
- UUID()나 RAND()와 같이 결과값이 호출할 때마다 달라지는 함수가 서브쿼리에 사용된 경우 

<br/>

```sql
-- 사용자 변수 사용
EXPLAIN 
SELECT * FROM employees e
WHERE e.emp_no = (
    SELECT @status FROM dept_emp de WHERE de.dept_no = 'd005'
);
```

<br/>

#### UNCACHEABLE UNION
UNION 을 사용할 때 캐시할 수 없을 때 표시된다.  

<br/>

#### MATERIALIZED  

5.6 버전부터 도입됐으며, 주로 FROM 절이나 IN(subquery) 형태의 쿼리에 사용된 서브쿼리의 최적화를 위해 사용된다.  

```sql
EXPLAIN
SELECT * FROM employees
WHERE emp_no IN (SELECT emp_no FROM salaries WHERE salary BETWEEN 100 AND 1000);
```

5.6 까지는 employees 테이블을 읽어서 레코드마다 salaries 테이블을 읽는 서브쿼리가 실행되는 형태로 처리됐다.  
5.7 버전부터 서브쿼리의 내용을 임시 테이블로 구체화한 후, 임시 테이블과 employees 테이블을 조인하는 형태로 최적화되어 처리된다.  

<br/>
<br/>

### table 컬럼

실행 계획은 단위 쿼리 기준이 아니라 테이블 기준으로 표시된다. 테이블 이름에 별칭이 부여된 경우에는 별칭이 표시된다.  

table 컬럼에 `<>`로 둘러싸인 이름이 명시되는 경우는 임시 테이블을 의미한다.  
그리고 `<>`안에 항상 표시되는 숫자는 단위 쿼리의 id값을 지칭한다.  

<br/>
<br/>

### partitions 컬럼

5.7 버전까지는 파티션들의 목록은 EXPLAIN PARTITION 명령으로 확인해야 했다.  

8.0 버전부터는 EXPLAIN 명령으로 파티션 관련 실행 계획까지 모두 확인할 수 있게 변경됐다.  

파티션이 여러 개인 테이블에서 불필요한 파티션을 빼고 쿼리를 수행하기 위해 접근해야 할 것으로 판단되는 테이블만 골라내는 과정을 파티션 프루닝(Partition pruning)이라고 한다.  

실행 계획의 `partitions` 컬럼은 옵티마이저가 쿼리 처리를 위해 필요한 파티션들의 목록만 모아서 표시해준다.  
파티션 관련 실행 계획을 보면 type이 ALL로 되어 있는데 MySQL을 포함한 대부분의 RDBMS에서 지원하는 파티션은 물리적으로 개별 테이블처럼 별도의 저장 공간을 가지기 때문에 테이블의 일부만 읽을 수 있다.  

<br/>
<br/>

### type 컬럼

실행 계획에서 type 이후의 컬럼은 MySQL 서버가 각 테이블의 레코드를 어떤 방식으로 읽었는지를 나타낸다.  
방식은 인덱스를 사용해 레코드를 읽었는지, 아니면 풀 테이블 스캔으로 레코드를 읽었는지 등을 의미한다.  
일반적으로 쿼리를 튜닝할 때 인덱스를 효울적으로 사용하는지 확인하는 것이 중요하므로 실행 꼐획에서 type 컬럼은 반드시 체크해야 할 중요한 정보다.  

type 컬럼은 각 테이블의 접근 방법(Access Type)으로 해석하면 된다.  

type 컬럼의 값 중 ALL(풀 테이블 스캔)을 제외하면 나머지는 모두 인덱스를 사용하는 접근 방법이다.  
하나의 쿼리는 접근 방법 중 단 하나만 사용할 수 있으며, index_merge를 제외한 나머지 접근 방법은 하나의 인덱스만 사용한다.  

다음은 성능 순으로 정렬한 type 컬럼의 표시값이다. (죄측이 가장 빠름)

system > const > eq_ref > ref > fulltext > ref_or_unll > unique_subquery > index_subquery > range > index_merge > index > ALL

<br/>

#### system  
레코드가 1건만 존재하는 테이블 또는 한 건도 존재하지 않는 테이블을 참조하는 형태의 접근 방법이다.  
이 접근 방법은 MEMORY, MyISAM 스토리지 엔진을 사용하는 테이블에서만 사용되는 방식이다.  

```sql
CREATE TABLE tb_dual(fd1 INT NOT NULL) ENGINE=MyISAM;
INSERT INTO tb_dual VALUES(1);

EXPLAIN SELECT * FROM tb_dual;
```

InnoDB 스토리지 엔진을 사용하면 type 컬럼이 ALL 또는 index로 표시될 가능성이 크다.  

<br/>

#### const  

테이블의 레코드 건수와 관계없이 쿼리가 프라이머리 키나 유니크 키 컬럼을 이용하는 WHERE 조건을 가지고 있으며, 반드시 1건을 반환하는 쿼리 방식을 const라고 한다.  
다른 DBMS에선 유니크 인덱스 스캔이라고도 표현한다.  

```sql
EXPLAIN SELECT * FROM employees WHERE emp_no = 10001;
```

<br/>

PK나 유니크 키 중 인덱스의 일부 컬럼만 조건으로 사용할 때는 const 타입의 접근 방법을 사용할 수 없다.  레코드가 1건임을 확신할 수 없기 때문이다.  

```sql
-- ref로 표시
EXPLAIN SELECT * FROM demp_emp WHERE dept_no = 'd005';
```

<br/>

실행 계획의 type 컬럼이 const 인 실행 계획은 옵티마이저가 쿼리를 최적화하는 단계에서 쿼리를 먼저 통째로 상수화한다.  

<br/>

#### eq_ref  

eq_ref 접근 방법은 여러 테이블이 조인되는 쿼리의 실행 계획에서만 표시된다.  
조인에서 처음 읽은 테이블의 컬럼 값을, 그다음 읽어야 할 테이블의 PK나 유니크 키 컬럼의 검색 조건에 사용할때를 가리켜 eq_ref라고 한다. 이때 두 번째 이후에 읽는 테이블의 type 컬럼에 eq_ref가 표시된다.  
즉, 조인에서 두 번째 이후에 읽는 테이블에서 반드시 1건만 존재한다는(유니크한) 보장이 있어야 사용할 수 있는 접근 방법이다.  

```sql
EXPLAIN
SELECT * FROM dept_emp de, employees e
WHERE e.emp_no = de.emp_no AND de.dept_no = 'd005';
```

<br/>

#### ref  

ref 접근 방법은 eq_ref와는 달리 조인의 순서와 관계없이 사용하며, PK나 유니크 키 등의 제약조건도 없다.  
인덱스의 종류와 관계없이 동등(Equal) 조건으로 검색할 때는 ref 접근 방법이 사용된다.  
ref 타입은 반환되는 레코드가 반드시 1건이라는 보장이 없으므로 const나 eq_ref보다는 느리지만, 동등 조건으로 비교되므로 빠른 조회 방법이다.  

```sql
EXPLAIN
SELECT * FROM dept_emp WHERE dept_no = 'd005';
```

const, eq_ref, ref 세 가지 방법은 실제 데이터 분포나 레코드 건수에 따라 순서는 달라질 수 있으나 모두 좋은 접근 방법이다. (따로 튜닝하지 않아도 됨)  

<br/>

#### fulltext

fulltext 접근 방법은 서버의 전문 검색(Full-text Search) 인덱스를 사용해 레코드를 읽는 접근 방법을 의미한다.  
전문 검색 인덱스는 통계 정보가 관리되지 않으며, 전문 검색 인덱스를 사용하려면 전혀 다른 SQL 문법을 사용해야 한다.  

전문 검색 인덱스는 우선 순위가 높으며 반드시 테이블에 전문 검색 인덱스가 존재해야 하고 `MATCH (...) AGAINST (...)` 구문을 사용해서 실행한다.  


```sql
-- 전문 검색 인덱스가 있다고 가정
CREATE FULLTEXT INDEX idx_name ON employees (first_name, last_name) WITH PARSER ngram;

EXPLAIN
SELECT * FROM employees
AND emp_no BETWEEN 10001 AND 10005
AND MATCH(first_name, last_name) AGAINST('Facello') IN BOOLEAN MODE;
```

전문 검색 인덱스보단 일반 인덱스를 이용하는 range 접근이 더 빨리 처리되는 경우가 많으므로 전문 검색 쿼리를 사용할 때는 조건별로 성능을 확인하자.  

<br/>

#### ref_or_null  

ref와 접근 방법은 같은데, NULL 비교가 추가된 형태다. 이름대로 ref방식 또는 비교(IS NULL) 접근 방법을 의미한다.  

```sql
EXPLAIN SELECT * FROM titles
WHERE to_date = '1985-03-01' OR to_date IS NULL;
```

<br/>

#### unique_subquery  

WHERE 조건절에서 사용될 수 있는 IN(subquery) 형태의 쿼리를 위한 접근이다. 서브쿼리에서 중복되지 않는 유니크한 값만 반환할 때 이 접근 방법을 사용한다.  

```sql
EXPLAIN SELECT & FROM  departments
WHERE dept_no IN (SELECT dept_no FROM dept_emp WHERE emp_no = 100001);
```

<br/>

#### index_subquery

IN 연산자의 특성상 IN(subquery) 또는 IN(상수 나열) 형태의 조건은 괄호 안에 있는 값의 목록에서 중복된 값이 먼저 제거되야 한다.  
index_subquery는 서브쿼리 결과의 중복된 값을 인덱스를 이용해서 제거할 수 있을 때 사용된다.  

- umique_subquery: IN(subquery) 형태의 조건에서 서브쿼리의 반환 값에는 중복이 없으므로 별도의 중복 제거 작업이 필요하지 않음
- index_subquery: IN(subquery) 형태의 조건에서 서브쿼리의 반환 값이 중복된 값이 있을 수 있지만 인덱스를 이용해 중복된 값을 제거할 수 있음  

<br/>

#### range

range는 인덱스 레인지 스캔 형태의 접근 방법으로, 인덱스를 하나의 값이 아니라 범위로 검색하는 경우를 의미한다. 주로 `<, >, IS NULL, BETWEEN, INm LIKE` 등의 연산자를 이용해 인덱스를 검색할 때 사용한다.  
우선순위 중 낮은 편이지만 range 접근도 빠른편이며, 이 접근 방법만 사용해도 최적의 성능이 보장된다.  

```sql
EXPLAIN 
SELECT * FROM employees WHERE emp_no = BETWEEN 10002 AND 10004;
```

<br/>

보통 실무에서 const, ref, range 접근 방법을 모두 인덱스 레인지 스캔, 레인지 스캔이라고 말한다.  

<br/>

#### index_merge 

2개 이상의 인덱스를 이용해 각각의 검색 결과를 만들어낸 후, 그 결과를 병합해서 처리하는 방식이다. 이름만큼 효율적으로 작동하지는 않는다.   

- 여러 인덱스를 읽어야 하므로 일반적으로 range 접근 방법보다 효율성이 떨어진다.  
- 전문 검색 인덱스를 사용하는 쿼리에서는 index_merge가 적용되지 않는다.  
- index_merge 접근 방법으로 처리된 결과는 항상 2개 이상의 집합이 되기 때문에 그 두 집합의 교집합이나 합집합, 또는 중복 제거와 같은 부가적인 작업이 더 필요하다.  

index_merge 접근 방법이 사용될 때는 실행 계획에 조금 더 보완적인 내용이 표시된다.(Extra 부분 학습)  

```sql
EXPLAIN
SELECT * FROm employees
WHERE emp_no BETWEEN 10001 AND 11000
OR first_name = 'Smith';
```

<br/>

#### index

index 접근 방법은 인덱스를 처음부터 끝까지 읽는 인덱스 풀 스캔을 의미한다.  
range 접근 방법과 같이 효율적으로 인덱스의 필요한 부분만 읽는 것이 아니다.  

index 접근 방법은 테이블을 처음부터 끝까지 읽는 풀 테이블 스캔 방식과 비교하는 레코드 건수는 같지만 인덱스가 전반적으로 데이터 파일 전체보다 크기가 작으므로 인덱스 풀 스캔이 테이블 풀 스캔보다 빠르게 처리되며, 쿼리의 내용에 따라 인덱스의 장점을 이용할 수 있으므로 효율적이다.  

인덱스 접근 방식이 사용되는 쿼리의 예시이며, 1, 2 조건을 충족하거나 1, 3 조건을 충족하는 쿼리에서 사용된다.  

- range나 const, ref 접근 방법으로 인덱스를 사용하지 못하는 경우
- 인덱스에 포함된 컬럼만으로 처리할 수 있는 쿼리인 경우(데이터 파일을 읽지 않아도 되는 경우)
- 인덱스를 이용해 정렬이나 그루핑 작업이 가능한 경우(별도의 정렬 작업을 피할 수 있는 경우)  

```sql
-- 조건은 없지만 정렬 조건의 컬럼에 대한 인덱스가 존재하여 인덱스 스캔
-- LIMIT 조건으로 인해 효율적이지만 LIMIT 가 없으면 상당히 느린처리가 됨
EXPLAIN
SELECT * FROM departments ORDER BY dept_name DESC LIMIT 10;
```

<br/>

#### ALL

풀 테이블 스캔을 의미하는 접근 방법이다. 테이블을 처움브터 끝까지 전부 읽는 방식으로 가장 비효율 적인 방식이다.  

다른 DBMS와 같이 InnoDB도 풀 테이블 스캔이나 인덱스 풀 스캔과 같은 대량의 디스크 I/O를 유발하는 작업을 위해 한꺼번에 많은 페이지를 읽어들이는 기능을 제공한다. 이 기능을 리드 어헤드(Read Ahead)라고 하며, 한 번에 여러 페이지를 읽어서 처리할 수 있다.  
배치 프로그램처럼 대량의 레코드를 처리하는 쿼리에서 잘못 튜닝된 쿼리보다는 더 나은 접근 방법이다.  

일반적으로 index와 ALL 접근 방법은 작업 범위를 제한하는 조건이 아니므로 빠른 응답을 사용자에게 보내야 하는 웹 서비스 등과 같은 온라인 트랜잭션 처리 환경에서는 적합하지 않다.  

<br/>

MuSQL 서버에서는 인접한 페이지가 연속해서 몇 번 읽히면 백그라운드로 작동하는 읽기 스레드가 최대 64개의 페이지씩 한꺼번에 디스크로부터 읽어 들이기 때문에 한 번에 페이지 하나씩 읽어 들이는 작업보다는 상당히 빠르게 레코드를 읽을 수 있는데, 이 작업을 리드 어헤드라고 한다.  

<br/>
<br/>

#### possible_keys 컬럼

이 컬럼의 내용은 옵티마이저가 최적의 실행 계획을 만들기 위해 후보로 선정했던 접근 방법에서 사용되는 인덱스 목록일 뿐이다.  
즉, 말 그대로 "사용될법했던 인덱스의 목록"이다.  

<br/>
<br/>

### key 컬럼

key 컬럼에 표시되는 인덱스는 최종 선택된 실행 계획에서 사용하는 인덱스를 의미한다.  
쿼리를 튜닝할 때 key 컬럼에 의도했던 인덱스가 표시되는지 확인하는 것이 중요하다.  

type이 index_merge인 경우 인덱스가 쉼표로 구분되어 여러개 표시된다.  

<br/>
<br/>

### key_len 컬럼

key_len 컬럼은 쿼리를 처리하기 위해 다중 컬럼으로 구성된 인덱스에서 몇 개의 컬럼까지 사용했는지 알려준다.  
정확하게는 인덱스의 각 레코드에서 몇 바이트까지 사용했는지 알려주는 값이다. 다중 컬럼 인덱스뿐 아니라 단일 컬럼으로 만들어진 인덱스에서도 같은 지표를 제공한다. 

```sql
EXPLAIN 
SELECT * FROM dept_emp WHERE dept_no = 'd005';
```

쿼리를 실행하면 key_len 값이 16으로 표시되는데 dept_no 컬럼의 타입이 `CHAR(4)`이기 때문에 PK에서 앞쪽 16바이트만 유효하게 사용했다는 의미다.  
테이블의 dept_no는 unf8mb4 문자 집합(문자 하나당 1~4 바이트)을 사용하는데, MySQL 서버가 utf8mb4 문자를 위해 메모리 공간을 할당해야할 때는 고정적으로 4바이트로 계산한다.  

<br/>

```sql
EXPLAIN 
SELECT * FROM dept_emp WHERE dept_no = 'd005' AND emp_no = 10001;
```

key_len에서 데이터가 NULL을 허용하면 NULL이 저장될 수 있는 컬럼으로 정의되서 1바이트가 추가적으로 사용된다.  
date 타입이 3바이트인데 nullable 컬럼이면 4바이트의 key_len 값이 나타난다.  

<br/>
<br/>

### ref 컬럼

접근 방법이 ref면 참조 조건(Equal 비교 조건)으로 어떤 값이 제공댔는지 보여준다.  상수값을 지정했다면 ref 컬럼의 값은 const로 표시되고, 다른 테이블의 컬럼값이면 그 테이블명과 컬럼명이 표시된다.  
이 컬럼의 출력 내용은 신경쓰지 않아도 무방하지만, 예외 케이스가 있다.  

실행 계획에서 ref 컬럼 값이 func라고 표시될 때가 있는데 `FUNCTION`의 줄임말로 참조용으로 사용되는 값을 그대로 사용한 것이 아닌 콜레이션 변환이나 값 자체의 연산을 거쳐서 참조됐는 것을 의미한다.  

```sql
EXPLAIN
SELECT * FROM employees e, dept_emp ed WHERE e.emp_no = (de.emp_no - 1); 
```

사용자가 명시적으로 값을 변활할 때뿐만 아니라 MySQL 서버가 내부적으로 값을 변환해야할 때도 ref 컬럼에는 func가 출력된다.  

<br/>
<br/>

### rows 컬럼 

옵티마이저는 각 조건에 대해 가능한 처리 방식을 나열하고, 각 처리 방식의 비용을 비교해 최종적으로 하나의 실행 계획을 수립한다.  
이때 각 처리 방식이 얼마나 많은 레코드를 읽고 비교해야 하는지 예측해서 비용을 산정한다.  
대상 테이블에 얼마나 많은 레코드가 포함돼 있는지 또는 각 인덱스 값의 분포도가 어떤지를 통계 정보를 기준으로 조사해서 예측한다.  

rows 컬럼값은 실행 계획 효율성 판단을 위해 예측했던 레코드 건수를 보여준다.  
이 값은 각 스토리지 엔진별로 가지고 있는 통계 정보를 참조해 MySQL 옵티마이저가 산출해 낸 예상값이라서 정확하지는 않다.  
또한 rows 컬럼에 표시되는 값은 반환하는 레코드의 예측치가 아니라 쿼리를 처리하기 위해 얼마나 많은 레코드를 읽고 체크해야 하는지를 의미한다. 그래서 rows 컬럼의 표시값은 실제 쿼리에 반환된 레코드 수는 일치하지 않는 경우가 많다.  

```sql
EXPLAIN
SELECT * FROM dept_emp WHERE from_date >= '1985-01-01';

EXPLAIN
SELECT * FROM dept_emp WHERE from_date >= '2002-07-01';
```

<br/>

옵티마이저가 예측한 값은 틀릴 가능성이 높다. 옵티마이저가 예측하는 수치는 대략의 값이지 정확한 값을 산출하기 위한 기능은 아니다. 하지만 대략의 수치에는 어느 정도 근접해야 하며, 그래야 옵티마이저는 제대로 된 실행 계획을 수립할 수 있다. 이를 위해 히스토그램이 도입됐다.  

<br/>
<br/>

#### filtered 컬럼

rows 컬럼의 값은 인덱스를 사용하는 조건에만 일치하는 레코드 건수를 예측한 것이다.  
하지만 대부분 쿼리에서 WHERE 절에 사용되는 조건이 모두 인덱스를 사용할 수 있는 것은 아니다.  
특히 조인이 사용되는 겨웅에는 WHERE 절에서 인덱스를 사용할 수 있는 조건도 중요하지만 인덱스를 사용하지 못하는 조건에 일치하는 레코드 건수를 파악하는 것도 중요하다.  

```sql
EXPLAIN
SELECT * FROM employees e, salaries s
WHERE e.first_name = 'Matt'
AND e.hire_date BETWEEN '1990-01-01' AND '1991-01-01'
AND s.emp_no = e.emp_no
AND s.from_date BETWEEN '1990-01-01' AND '1991-01-01'
AND s.salary BETWEEN 50000 AND 60000;
```

filtered 컬럼의 값은 필터링되어 버려지는 레코드의 비율이 아니라 필터링되고 남은 레코드의 비율을 의미한다.  
employees 테이블에서 salaries 테이블로 조인을 수행한 레코드 건수는 37(233 * 0.1604)건 정도였음을 알 수 있다.  

```sql
-- 조인 순서 변경 후 확인
EXPLAIN
SELECT /*+ JOIN_ORDER(s, e) */ * FROM employees e, salaries s
WHERE e.first_name = 'Matt'
AND e.hire_date BETWEEN '1990-01-01' AND '1991-01-01'
AND s.emp_no = e.emp_no
AND s.from_date BETWEEN '1990-01-01' AND '1991-01-01'
AND s.salary BETWEEN 50000 AND 60000;
```

히스토그램은 filtered 컬럼의 값을 더 정확히 예측할 수 있게 해준다.  

<br/>
<br/>

### Extra 컬럼

Extra 컬럼은 성능에 관련된 중요 내용이 표시되며, 고정된 몇 개의 문장이 표시되는데 일반적으로 2~3개씩 함께 표시된다.  
Extra 컬럼에는 주로 내부적인 처리 알고리즘에 대해 조금 더 깊이 있는 내용을 보여주는 경우가 많다.  

<br/>

#### const row not found  

쿼리 실행 계획에서 const 접근 방법으로 테이블을 읽었지만 실제로 해당 테이블에 레코드가 1건도 존재하지 않으면 이 내용이 표시된다.  
이 메시지가 표시되는 경우 테이블에 테스트 데이터를 저장하고 다시 실행 계획을 확인해 보는 것이 좋다.  

<br/>

#### Deleting all rows  

MyISAM 스토리지 엔진과 같이 스토리지 엔진의 핸들러 차원에서 테이블의 모든 레코드를 삭제하는 기능을 제공하는 스토리지 엔진 테이블인 경우 이 문구가 표시된다.  
`Deleting all rows` 문구는 WHERE 조건절이 없는 DELETE 문장의 실행 계획에서 자주 표시되며, 이 문구는 테이블의 모든 레코드를 삭제하는 핸들러 기능(API)을 한번 호출함으로써 처래됐다는 것을 의미한다.  
기존에는 테이블의 레코드를 삭제하기 위해 각 스토리지 엔진의 핸들러 함수를 레코드 건수만큼 호출해서 삭제헤야 했는데, `Deleting all rows` 처리 방식은 한 번의 핸들러 함수 호출로 간단하고 빠르게 처리할 수 있다.

8.0 버전부터는 더이상 표시되지 않고 테이블의 모든 레코드를 삭제시 DELETE 문이 아닌 TRUNCATE TABLE 명령을 사용할 것을 권장한다.  

<br/>

#### Distinct

```sql
EXPLAIN 
SELECT DISTINCT d.dept_no
FROm departments d, dept_emp de WHERE de.dept_no = d.dept_no;
```

Distinct 문을 사용한 쿼리에 나타나며 원하는 컬럼을 중복 없이 유니크하게 가져오기 위해 사용한다.  

<br/>

#### FirstMatch

세미 조인의 여러 최적화 중에서 FirstMatch 전략이 사용되면 Extra 컬럼에 `FirstMatch(테이블명)` 메시지를 출력한다.  

```sql
EXPLAIN
SELECT * FROM employees e
WHERE e.first_name = 'Matt'
AND e.emp_no IN (
    SELECT t.emp_no FROM titles t
    WHERE t.from_date BETWEEN '1995-01-01' AND '1995-01-30'
);
```

FirstMAtch 메시지에 함께 표시되는 테이블명은 기준 테이블을 의미하는데, 위 실행 계획의 경우 employees 테이블 기준으로 titles 테이블에서 첫 번째로 일치하는 한 건만 검색한다는 것을 의미한다.  

<br/>

#### Full scan on NULL key  

이 처리는 `col1 IN (SELECT col2 FROm ...)`과 같은 조건을 가진 쿼리에서 자주 발생할 수 있는데, col1의 값이 NULL이 된돠면 결과적으로 조건은 `NULL IN (SELECT col2 FROm ...)`과 같이 바뀐다.  
SQL 표준은 NULL을 "알 수 없는 값"으로 정의하고 있으며, NULL에 대한 연산의 규칙까지 정의하고 있다.  정의대로 연산을 수행하기 위해서는 다음처럼 비교돼야 한다.  

- 서브쿼리가 1건이라도 결과 레코드를 가진다면 최종 비교 결과는 NULL
- 서브쿼리가 1건도 결과 레코드를 가지지 않는다면 최종 비교 결과는 FALSE  

<br/>

이 비교 과정에서 col1이 NULL이면 서브쿼리에 사용된 테이블에 대해서 풀 테이블 스캔을 해야만 결과를 알아낼 수 있다.  
`Full scan on NULL key`는 서버가 쿼리를 실행하는 중 col1이 NULL을 만나면 차선책으로 서브쿼리 테이블에 대해서 풀 테이블 스캔을 사용할 것이라는 사실을 알려주는 키워드다.  

```sql
EXPLAIN
SELECT d1.dept_no, NULL IN (SELECT d2.dept_name FROM departments d2) FROM departments d1;
```

NULL 비교 규칙을 무시해도 된다면 이를 옵티마이저에게 알려주면 된다. 주로 사용되는 방법은 `col1 IS NOT NULL`조건을 지정하는 것이다.  

`Full scan on NULL key` 코멘트가 실행 계획의 Extra 컬럼에 표시됐다고 하더라도 IN이나 NOT IN 연산자의 왼쪽에 있는 값이 실제로 NULL이 없다면 서브쿼리의 테이블에 대한 풀 테이블 스캔은 발생하지 않으니 걱정안해도 된다.  

<br/>

#### Impossible HAVING  

쿼리에 사용된 HAVING 절의 조건을 만족하는 레코드가 없을 때 표시된다.

<br/>

#### Impossible WHERE

쿼리에 사용된 WHERE 조건이 항상 FALSE가 될 수밖에 없는 경우 표시된다.  

<br/>

#### LooseScan  

세미 조인 최적화 중에서 LooseScan 최적화 전략이 사용되면 표시된다.  

<br/>

#### No matching min/max row  

MIN()이나 MAX()와 같은 집합 함수가 있는 쿼리의 조건절에 일치하는 코드가 한 건도 없을때 Extra 컬럼에 표시되며 결과로 NULL이 반환된다.  

```sql
EXPLAIN 
SELECT MIN(dept_no), MAX(dept_no) FROM dept_emp WHERE dept_no = '';
```

<br/>

#### no matching row in const table  

조인이 사용된 테이블에서 const 방법으로 접근할 때 일치하는 레코드가 없다면 표시된다.  

```sql
SELECT * FROM dept_emp de, (SELECT emp_no FROM employees WHERE emp_no = 0) tb1
WHERE tb1.emp_no = de.emp_no AND de.dept_no = 'd0005';
```

<br/>

#### No matching rows after partition pruning  

파티션된 테이블에 대한 UPDATE 또는 DELETE 명령의 실행 계획에서 표시될 수 있는데, 해당 파티션에서 UPDATE, DELETE 할 대상 레코드가 없을 때 표시된다.  

<br/>

#### No tables used  

FROM 절이 없는 쿼리 문장이나 `FROM DUAL` 형태의 쿼리 실행 계획에서 표시된다.  

<br/>

#### NOot exists  

프로그램을 개발하다 보면 A 테이블에는 존재하지만 B 테이블에는 없는 값을 조회해야 하는 쿼리가 자주 사용된다. 이 때 주로 `NOT IN(subquery)` 형태나 `NOT EXISTS` 연산자를 주로 사용한다. 이 형태를 안티-조인이라고 한다.  
똑같은 처리를 아우터 조인(LEFT OUTER JOIN)으로 할 수도 있다.  
일반적으로 안티-조인으로 처리해야 하지만 레코드의 건수가 많을 때는 아우터 조인을 이용하면 빠른 성능을 낼 수 있다.  

```sql
EXPLAIN
SELECT * FROM dept_emp de
LEFT JOIN departments d ON de.dept_no = d.dept_no
WHERE d.dept_no IS NULL;
```

아우터 조인을 이용해 안티-조인을 수행하는 쿼리에서는 실행 계획의 Extra 컬럼에 "Not exists" 메시지가 표시된다. 이는 옵티마이저가 dept_emp 테이블의 레코드를 이용해 departments 테이블을 조인할 때 departments 테이블의 레코드가 존재하는지 아닌지만 판단한다는 것을 의미한다.  
즉, departments 테이블의 조인 조건에 일치하는 레코드가 여러 건 있다고 하더라도 딱 1건만 조회해보고 처리를 완료하는 최적화를 의미한다.  

<br/>

#### Plan isn't ready yet  

```sql
-- 실행중인 쿼리의 실행 계획 확인
SHOW PROCESSLIST;

-- 옵티마이저가 의도된 인덱스를 사용하지 못해서 풀 스캔을 한다거나 잘못된 실행 계획을 선택한 것이 아닌지 확인할 때 사용할 수 있는 명령어
EXPLAIN FOR CONNECTION [id];
```

`Plan isn't ready yet`은 EXPLAIN FOR CONNECTION 명령을 실행했을 때 해당 커넥션에서 아직 쿼리의 실행 계획을 수립하지 못한 상태에서 EXPLAIN FOR CONNECTION 명령이 실행된 것을 의미한다.  

<br/>

#### Range checked for each record(index map:N)  

```sql
EXPLAIN 
SELECT * FROM employees e1, employees e2
WHERE e2.emp_no >= e1.emp_no;
```

employees 테이블의 emp_no가 1번부터 1억번까지 있다면 e1.emp_no가 1인 경우 e2 테이블의 데이터를 1억건 읽어야 한다. 반대로 e1.emp_no가 1억이면 e2 테이블을 1건만 읽으면 된다.  

이처럼 emp_no가 작을때는 e2 테이블을 풀 테이블 스캔으로 접근하고 반대일 때는 인덱스 레인지 스캔으로 접근하는 형태가 최적의 방법이다.  
이처럼 레코드마다 인덱스 레인지 스캔을 체크할 떄 `Range checked for each record(index map:N)`가 표시된다.  

Extra 컬럼에 `Range checked for each record`가 표시되면 type 컬럼에는 ALL 로 표시된다. 즉, "index map"에 표시된 후보 인덱스를 사용할지 여부를 검토해서 이 후보 인덱스가 별로 도움이 되지 않는다면 최종적으로 풀 테이블 스캔을 사용하기 때문에 ALL로 표시된 것이다.  

<br/>

#### Recursive  

8.0 부터 CTE(Common Table Expression)을 이용해서 재귀 쿼리를 작성할 수 있게 됐다.  

```sql
WITH RECURSIVE cte (n) AS
(
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte WHERE n < 5
)
SELECT * FROM cte;
```

위 쿼리처럼 CTE을 이용한 재귀 쿼리의 실행 계획은 `Recursive`가 표시된다.  

<br/>

#### Rematerialize  

8.0 부터 래터럴 조인 기능이 추가됐는데, 이 경우 래터럴로 조인되는 테이블은 선행 테이블의 레코드별로 서브쿼리를 실행해서 그 결과를 임시 테이블에 저장한다. 이 과정을 "Rematerializing"이라고 한다.  

```sql
EXPLAIN
SELECT * FROM employees e
LEFT JOIN LATERAL (
    SELECT * FROM salaries s
    WHERE s.emp_no = e.emp_no
    ORDER BY s.from_date DESC LIMIT 2
) s2 
ON s2.emp_no = e.emp_no
WHERE e.first_name = 'Matt';
```

쿼리 실행해서 매번 임시 테이블이 새로 생성되는 경우 "Rematerialize" 문구가 표시된다.  

<br/>

#### Select table optimized away  

MIN() 또는 MAX()만 SELECT 절에 사용되거나 GROUP BY로 MIN(), MAX()를 조회하는 쿼리가 인덱스를 오름차순 또는 내림차순으로 1건만 읽는 형태의 최적화가 사용되면 표시된다.  

MyISAM 테이블에서는 GROUP BY 없이 COUNT(*)만 SELECT할 때도 이런 형태의 최적화가 적용된다.  

```sql
EXPLAIN
SELECT MAX(emp_no), MIN(emp_no) FROM employees;
```

<br/>

#### Start temporary, End temporary  

세미 조인 최적화 중에서 Duplicate Weed-out 최적화 전략이 사용되면 MySQL 옵티마이저는 `Start temporary`나 `End temporary`를 표시한다.  

```sql
EXPLAIN
SELECT * FROM employees e
WHERE e.emp_no IN (SELECT s.emp_no FROM salaries s WHERE s.salary > 150000);
```

Duplicate Weed-out 최적화 전략은 불필요한 중복 건을 제거하기 위해서 내부 임시 테이블을 사용하는데, 이때 조인되어 내부 임시 테이블에 저장되는 테이블을 식별할 수 있게 첫 번째 테이블에는 "Start temporary" 문구를 보여주고 조인이 끝나는 부분에 "End temporary" 문구를 보여준다.  

<br/>

#### unique row not found  

두 개의 테이블이 각각 유니크(PK 포함) 컬럼으로 아우터 조인을 수행하는 쿼리에서 아우터 테이블에 일치하는 레코드가 존재하지 않을 때 표시된다.  

<br/>

#### Using filesort  

ORDER BY를 처리하기 위해 인덱스를 이용할 수도 있지만 적절한 인덱스를 사용하지 못할 때는 서버가 조회된 레코드를 다시 한번 정렬해야 한다.  
ORDER BY 처리가 인덱스를 사용하지 못할 때만 실행 계획에 `Using filesort`가 표시되며, 이는 조회된 레코드를 정렬용 메모리 버퍼에 복사해 퀵 소트 또는 힙 소트 알고리즘을 이용해서 정렬을 수행하게 된다는 의미다.  

```sql
EXPLAIN
SELECT * FROM employees
ORDER BY last_name DESC;
```

실행 계획에 `Using filesort`가 출력되는 쿼리는 많은 부하를 일으키므로 쿼리 튜닝을하거나 인덱스를 생성하는 것이 좋다.  

<br/>

#### Using index(커버링 인덱스)  

데이터 파일을 전혀 읽지 않고 인덱스만 읽어서 쿼리를 모두 처리할 수 있을 때 표시된다.  
인덱스를 이용해 처리하는 쿼리에서 가장 큰 부하를 차지하는 부분은 인덱스 검색에서 일치하는 키 값들의 레코드를 읽기 위해 데이터 파일을 검색하는 작업이다.  
쿼리에서 조회하는 컬럼이 데이터 파일이 아닌 인덱스로만 읽을 수 있을때 나타난다고 보면 된다.  

<br/>

#### Using index condition  

옵티마이저가 인덱스 컨티션 푸시 다운 최적화를 사용하면 `Using index condition`이 표시된다.  

<br/>

#### Using index for group-by  

GROUP BY 처리를 위해 서버는 그루핑 기준 컬럼을 이용해 정렬 작업을 수행하고 다시 정렬된 결과를 그루핑하는 형태의 고부하 작업을 필요로 한다.  
GROUP BY 처리를 인덱스를 사용하면 정렬된 인덱스 컬럼을 순서대로 읽으면서 처리하는데 이를 통해 레코드 정렬이 필요하지 않고 필요한 부분만 읽으면 되기 때문에 작업이 빨라진다.  
GROUP BY가 인덱스를 이용할 때 쿼리의 실행 계획에 `Using index for group-by`가 표시된다.  

<br/>

**타이트 인덱스 스캔(인덱스 스캔)을 통한 GROUP BY 처리**  

인덱스를 이용해 GROUP BY를 처리할 수 있어도 AVG(), SUM(), COUNT()처럼 조회하려는 값이 모든 인덱스를 다 읽어야 할 때는 필요한 레코드만 읽을 수 없다. 이는 GROUP BY를 사용하지만, 루스 인덱스 스캔이라고 하지는 않는다.  
그리고 이 쿼리의 실행 계획에는 `Using index for group-by` 메시지가 출력되지 않는다.  

<br/>

**루스 인덱스 스캔을 통한 GROUP BY 처리**  

단일 컬럼으로 구성된 인덱스에는 그루핑 컬럼 말고는 아무것도 조회하지 않는 쿼리에서 루스 인덱스 스캔을 사용할 수 있다.  
그리고 다중 컬럼으로 만들어진 인덱스에는 GROUP BY 절이 인덱스를 사용할 수 있어야 함은 물론이고 MIN(), MAX() 같이 인덱스의 첫 번째 또는 마지막 레코드만 읽어도 되는 쿼리는 루스 인덱스 스캔이 사용될 수 있다.  

<br/>

#### Using index for skip scan

옵티마이저가 인덱스 스킵 스캔 최적화를 사용하면 표시된다.  

<br/>

#### Using join buffer(Block Nested Loop), Using join buffer(Batched Key Access), Using join buffer(hash join)  

조인이 수행될 때 드리븐 테이블의 조인 컬럼에 적절한 인덱스가 있다면 아무런 문제가 되지 않는다.  
하지만 드리븐 테이블에 검색을 위한 적절한 인덱스가 없다면 서버는 블록 NL 조인이나 해시 조인을 사용한다.  
블록 NL 조인이나 해시 종니을 사용하면 서버는 조인 버퍼를 사용한다. 이 실행 계획에 `Using join buffer`가 표시된다.  

<br/>

#### Using MRR

MRR 최적화가 사용된 쿼리에 표시된다.  

<br/>

#### Using sort_union(...), Using union(...), Using intersect(...)  

- Using intersect(...): 각각의 인덱스를 사용할 수 있는 조건이 AND로 연결된 경우 각 처리 결과에서 교집합을 추출해 내는 작업을 수행했다는 의미다.
- Using union(...): 각 인덱스를 사용할 수 있는 조건이 OR로 연결된 경우 각 처리 결과에서 합집합을 추출해내는 작업을 수행했다는 의미다.  
- Using sort_union(...): Using union과 같은 작업을 수행하지만 Using union으로 처리될 수 없는 경우(OR로 연결된 상대적으로 대량의 range 조건들) 이 방식으로 처리된다. Using union과 차이점은 PK만 먼저 읽어서 정렬하고 병합한 이후 비로소 레코드를 읽어서 반환할 수 있다는 것이다.  

<br/>

#### Using temporary  

서버에서 쿼리를 처리하는 동안 중간 결과를 담아두기 위해 임시 테이블을 사용한다.  
임시 테이블은 메모리상에 생성될 수도 있고 디스크상에 생성될 수도 있다.  
`Using temporary`가 표시되면 임시 테이블이 사용된 것이며, 메모리에 생성됐는지 디스크에 생성됐는지는 알 수 없다.  

<br/>

#### Using where

MySQL 서버는 내부적으로 크게 MySQL 엔진과 스토리지 엔진이라는 두 레이어로 나눠볼 수 있다.  
각 스토리지 엔진은 디스크나 메모리상에서 필요한 레코드를 읽거나 저장하는 역할을 하며, MySQL 엔진은 스토리지 엔진으로부터 받은 레코드를 가공 또는 연산하는 작업을 수행한다.  MySQL 엔진 레이어에서 별도의 가공을 해서 필터링 작업을 처리한 경우에만 `Using where` 가 표시된다.  

<br/>

#### Zero limit  
때로는 MySQL 서버에서 데이터 값이 아닌 쿼리 결과값의 메타데이터만 필요한 경우도 있다.  
즉, 쿼리의 결과가 몇 개의 컬럼을 가지고, 각 컬럼의 타입은 무엇인지 등의 정보만 필요한 경우가 있다.  이런 경우에는 쿼리의 마지막에 "LIMIT 0"을 사용하면 되는데, 이 때 옵티마이저는 사용자의 의도(메타 정보만 조회하고자 하는 의도)를 알아채고 실제 테이블의 레코드는 전혀 읽지 않고 결과값의 메타 정보만 반환한다.  
이 때 `Zero limit` 메시지가 표시된다.  