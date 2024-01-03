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
