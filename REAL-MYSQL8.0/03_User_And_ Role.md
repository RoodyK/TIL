# 사용자 및 권한

MySQL 사용자 계정은 사용자의 아이디뿐 아니라 해당 사용자가 어느 IP에서 접속하고 있는지도 확인한다.  
8.0 버전 부터는 권한을 묶어서 관리하는 역할(Role)의 개념이 도입됐기 때문에 각 사용자의 권한으로 미리 준비된 권한 세트를 부여하는 것도 가능하다.  
<br/>
<br/>

## 사용자 식별

MySQL의 사용자는 다른 DBMS와는 조금 다르게 사용자의 계정뿐 아니라 사용자의 접속 지점(클라이언트가 실행된 호스트명이나 도메인 또는 IP주소)도 계정의 일부가 된다. 따라서 MySQL에서 계정을 언급할 때는 항상 아이디와 호스트를 함꼐 명시해야 한다.  
`'svc_id'@'127.0.0.1'`  
위의 계정은 자신의 컴퓨터에서만 접속 가능(다른 컴퓨터 불가)한 계정이다.
<br/>

```sql
-- 비밀번호 123
'svc_id'@'192.168.0.10' 

-- 모든 외부 컴퓨터에서 접속이 가능(모든 IP, 모든 호스트), 비밀번호 abc
'svc_id'@'%'
```  
<br/>

IP 주소가 192.168.0.10인 PC에서 MySQL 서버에 접속할 때 서버는 권한이나 계정 정보에 대해 범위가 가장 작은 것을 항상 먼저 선택한다.  
즉, 두 계정 정보중 범위가 좁은 것은 `'svc_id'@'192.168.0.10'` 로 IP가 명시된 계정 정보로 사용자를 인증하게 된다.  
여기서 `'svc_id'@'192.168.0.10'` 계정의 비밀번호를 abc를 입력하면 비밀번호 불일치로 접속이 거절될 것이다.  
<br/>

## 사용자 계정 관리

### 시스템 계정과 일반 계정

8.0부터 계정은 SYSTEM_USER 권한을 가지고 있느냐에 따라 시스템 계정(System Account)과 일반 계정(Regular Account)으로 구분된다.  
이 장에서 시스템 계정은 서버의 내부적으로 실행되는 백그라운드 스레드와는 무관하며, 시스템 계정도 일반 계정과 같이 사용자를 위한 계정이다.  
시스템 계정은 데이터베이스 서버 관리자를 위한 계정이며, 일반 계정은 응용 프로그램이나 개발자를 위한 계정 정도로 보면 된다.  

시스템 계정은 시스템 계정, 일반 계정을 관리할 수 있지만 일반 계정은 시스템 계정을 관리할 수 없다.  
데이터베이스 서버 관리와 관련된 중요 작업은 시스템 계정으로만 수행할 수 있다.  
- 계정 관리(계정 생성 및 삭제, 그리고 계정의 권한 부여 및 제거)
- 다른 세션(Connection) 또는 그 세션에서 실행 중인 쿼리를 강제 종료
- 스토어드 프로그램 생성 시 DEFINER를 타 사용자로 설정  
<br/>

시스템 계정, 일반 계정 개념이 도입된 것은 DBA(데이터베이스 관리자) 계정에는 SYSTEM_USER 권한을 할당하고 일반 사용자를 위한 계정에는 권한을 부여하지 않게 하기 위해서다.

- 사용자: 서버를 이용하는 주체(사람 또는 프로그램)
- 계정: 서버에 로그인하기 위한 식별자(로그인 아이디)  
<br/>

MySQL 서버에는 내장된 계정이 존재하는데 `'root'@'localhost'` 를 제외한 3개의 계정은 내부적으로 다른 목적으로 사용되므로 삭제되지 않는다.  
- `'mysql.sys'@'localhost'`: 8.0부터 기본으로 내장된 sys 스키마의 객체(뷰나 함수, 프로시저)들의 DEFINER로 사용되는 계정
- `'mysql.session'@'localhost'`: MySQL 플러그인이 서버로 접근할 때 사용되는 계정
- `'mysql.infschema'@'localhost'`: information_schema에 정의된 뷰의 DEFINER로 사용되는 계정  
<br/>

```sql
SELECT user, host, account_locked  FROM mysql.user;
```
<br/>

### 계정 생성
8.0버전 부터는 계정 생성은 CREATE USER 명령으로, 권한 부여는 GRANT 명령으로 구분해서 실행도록 바뀌었다.  

#### 계정 생성 시 옵션
- 계정의 인증 방식과 비밀번호
- 비밀번호 관련 옵션(비밀번호 유효 기간, 비밀번호 이력 개수, 비밀번호 재사용 불가 기간)  
- 기본 역할(Role)
- SSL 옵션
- 계정 잠금 여부  
<br/>

```sql
CREATE USER 'user'@'%'
	IDENTIFIED WITH 'mysql_native_password' BY 'password'
	REQUIRE NONE 
	PASSWORD EXPIRE INTERVAL 30 DAY 
	ACCOUNT UNLOCK
	PASSWORD HISTORY DEFAULT
	PASSWORD REUSE INTERVAL DEFAULT
	PASSWORD REQUIRE CURRENT DEFAULT;
```
<br/>

### IDENTIFIED WITH
사용자의 인증 방식과 비밀번호를 생성한다.  
WITH 뒤에는 반드시 인증 방식(인증 플러그인의 이름)을 명시해야 하는데, 서버의 기본 인증 방식을 사용하려면 `IDENTIFIED BY 'password'` 형식으로 명시해야 한다.  

- Native Pluggable Authentication: 5.7 버전가지 기본으로 사용되던 방식으로, 단순히 비밀번호에 대한 해시(SHA-1)값을 저장해두고, 클라이언트가 보낸 값과 해시값이 일치하는지 비교하는 인증 방식이다.
- Caching SHA-2 Pluggable Authentication: 5.6 버전에 도입되고 8.0 버전에서는 조금 더 보완된 인증 방식으로, 암호화 해시값 생성을 위해 SHA-2(256비트) 알고리즘을 사용한다.  
- PAM Pluggable Authentication: 유닉스나 리눅스 패스워드 또는 LDAP(Lightweight Directory Access Protocal) 같은 외부 인증을 사용할 수 있게 해주는 인증 방식. 엔터프라이즈 에디션에서만 사용 가능하다. 
- LDAP Pluggable Authentication: LDAP을 이용한 외부 인증을 사용할 수 있게 해주는 인증 방식. 엔터프라이즈 에디션에서만 사용 가능하다.  
<br/>

8.0부터 기본 인증은 Caching SHA-2 Authentication 방식인데 SSL/TLS 또는 REA 키페어를 필요로 하기 때문에 5.7 이전의 방식과 다른 방식으로 접속해야 한다.  
보안 수준은 낮아지겠지만 이전 버전과의 호환성을 위한다면 Native Pluggable Authentication 방식을 사용해야할 수도 있다.  

```sql
SHOW GLOBAL VARIABLES LIKE 'default_authentication_plugin';

-- Native Authentication을 기본 인증 방식으로 설정
SET GLOBAL default_authentication_plugin="mysql_native_password'
```
<br/>

Caching SHA-2 Authentication은 SCRAM(Salted Challenge Response Authentication Mechanism) 인증 방식을 사용한다. SCRAM 인증 방식은 평문 비밀번호를 이용해서 5000번 이상 암호화 해시 함수를 실행해야 서버로 로그인 요청을 보낼 수 있기 때문에 무작위 비밀번호를 입력하는 무차별 대입 공격(brute-force attack)을 어렵게 만든다. 하지만 이런 방식은 악의가 없는 정상적인 유저나 응용 프로그램의 연결도 느리게 만든다.  
<br/>

### REQUIRE
MySQL 서버에 접속할 때 암호화된 SSL/TLS 채널을 사용할지 여부를 설정한다. 별도로 설정하지 않으면 비암호화 채널로 연결하게 된다.  
SSL로 설정하지 않더라도 Caching SHA-2 Authentication 인증 방식을 사용하면 암호화된 채널 만으로 서버에 접속할 수 있게 된다.  
<br/>

### PASSWORD EXPIRE
비밀번호의 유효 기간을 설정하는 옵션이며, 별도로 명시하지 않으면 default_password_lifetime 시스템 변수에 저장된 유효 기간으로 유효기간에 설정된다.  

- PASSWORD EXPIRE: 계정 생성과 동시에 비밀번호의 만료 처리
- PASSWORD EXPIRE NEVER: 계정 비밀번호의 만료 기간 없음
- PASSWORD EXPIRE DEFAULT: default_password_lifetime 시스템 변수에 저장된 기간으로 비밀번호의 유효 기간을 설정
- PASSWORD EXPIRE INTERVAL n DAY: 비밀번호의 유효 기간을 오늘부터 n일자로 설정
<br/>

### PASSWORD HISTORY 
한번 사용했던 비밀번호르 재사용하지 못하게 설정하는 옵션이다.  

- PASSWORD HISTORY DEFAULT: password_history 시스템 변수에 저장된 개수만큼 비밀번호의 이력을 저장하며, 저장된 이력에 남아있는 비밀번호는 재사용할 수 없다.  
- PASSWORD HISTORY n: 비밀번호의 이력을 최근 n개까지만 저장하며, 저장된 이력에 남아있는 비밀번호는 재사용할 수 없다.  

`mysql.password_history` 테이블은 이전 비밀번호 이력을 관리한다.  

### PASSWROD REUSE INTERVAL 
한 번 사용했던 비밀번호의 재사용 금지 기간을 설정하는 옵션이며, 별도로 명시하지 않으면 password_reuse_interval 시스템 변수에 저장된 기간으로 설정된다.  

- PASSWORD REUSE INTERVAL DEFAULT: password_reuse_interval 변수에 저장된 기간으로 설정
- PASSWORD REUSE INTERVAL n DAY: n일자 이후에 비밀번호를 재사용할 수 있게 설정  

### PASSWORD REQUIRE

비밀번호가 만려되어 새로운 비밀번호로 변경할 떄 현재 비밀번호(변경하기 전 만료된 비밀번호)를 필요로 할지 말지를 결정하는 옵션이며, 별도로 명시되지 않으면 passwrod_require_current 시스템 변수의 값으로 설정된다.  

- PASSWROD REQUIRE CURRENT: 비밀번호를 변경할 떄 현재 비밀번호를 먼저 입력하도록 설정
- PASSWORD REQUIRE OPTIONAL: 비밀번호를 변경할 때 현재 비밀번호를 입력하지 안하도 되도록 설정
- PASSWORD REQUIRE DEFAULT: password_require_current 시스템 변수의 값으로 설정
<br/>

### ACCOUNT LOCK / UNLOCK

계정 생성 시 또는 ALTER USER 명령을 사용해 계정 정보를 변경할 때 계정을 사용하지 못하게 잠글지 여부를 결정한다.  

- ACCOUNT LOCK: 계정을 사용하지 못하게 잠금
- ACCOUNT UNLOCK: 잠긴 계정을 다시 사용 가능 상태로 잠금 해제  
<br/>
<br/>

## 비밀번호 관리

### 고수준 비밀번호
비밀번호는 유효기간이나 이력 관리를 통한 재사용 금지 기능뿐만 아니라 비밀빈호를 쉽게 유추할 수 있는 단어들이 사용되지 않게 글자의 조합을 강제하거나 금칙어를 설정하는 기능도 있다.    
서버에서 비밀번호 유효성 체크 규칙을 적용하려면 validate_password 컴포넌를 설치 후 이용하면 된다. (서버 프로그램에 내장되어 있음)

```sql
-- 서버 프로그램에 내장돼 있기 때문에 file:// 부분에 별도의 파일 경로를 지정하지 않아도 된다.
INSTALL COMPONENT 'file://component_validate_password';
UNINSTALL COMPONENT 'file://component_validate_password';

-- 설치된 컴포넌트 확인
SELECT * FROM mysql.component;
-- 시스템 변수 확인
SHOW GLOBAL VARIABLES LIKE 'validate_password%';
```

#### 비밀번호 정책 3가지(기본값 MEDIUM)
- LOW: 비밀번호의 길이만 검증
- 비밀번호의 길이를 검증하며, 숫자와 대소문자, 그리고 특수문자의 배합을 검증
- STRONG: MEDIUM 레벨의 검증을 모두 수행하며, 금칙어가 포함됐는지 여부까지 검증
<br/>

비밀번호의 길이는 validate_password.length 시스템 변수에 설정된 길이 이상의 비밀번호가 사용됐는지를 검증하며, 숫자와 대소문자, 특수문자는 validate_password.mixed_case_count와 validate_password.number_count, validate_password.special_char_count 시스템 변수에 설정된 글자 수 이상을 포함하고 있는지 검증한다.  

금칙어는 validate_password.dictionary_file 시스템 변수에 설정된 단어를 포함하는지 검증한다.  
금칙어 파일은 금칙어들을 한 줄에 하나씩 기록해서 저장한 텍스트 파일로 작성하면 된다.  

```sql
SET GLOBAL validate_password.dictionary_file='prohibitive_word.data';
SET GLOBAL validate_password.policy='STRONG';
```
<br/>

### 이중 비밀번호
DB 서버 계정은 애플리케이션 서버로부터 공용으로 사용되는 경우가 많은데, 계정의 비밀번호는 초기 비밀번호를 그대로 사용하는 경우가 많기 때문에 보안을 위해서 계정의 비밀번호로 2개의 값을 동시에 사용할 수 있는 기능을 추가했다.  

이중 비밀번호는 최근에 설정된 비밀번호인 Primary와 이전 비밀번호인 Secondary로 구분된다.  
이중 비밀번호를 사용하려면 기존 구문에 RETAIN CURRENT PASSWORD 옵션만 추가하면 된다.  

```sql
-- 비밀번호 설정 ex) abcdef, 아직 세컨더리 x
ALTER USER 'root'@'localhost' IDENTIFIED BY 'old_password';

-- 비밀번호를 qwerty로 변경함녀서 기존 비밀번호를 세컨더리 비밀번호로 설정, 이전 비밀번호가 세컨더리가 됨
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password' RETAIN CURRENT PASSWORD;
```
<br/>

위의 상태에서 root 계정은 두 비밀번호 중 아무거나 입력해도 로그인 된다. 그 후 데이터베이스에 연결하는 애플리케이션 소스코드나 설정 파일의 비밀번호를 새로운 비밀번호인 'new_password'로 변경하고 배포 및 재시작을 순차적으로 실행한다.  

MySQL 서버에 접속하는 모든 애플리케이션의 재시작이 완료되면 이제 다음 명령으로 세컨더리 비밀번호는 삭제한다.  

`ALTER USER 'root'@'localhost' DISCARD OLD PASSWORD`;

세컨더리 비밀번호는 계정의 보안을 위해서 삭제하는 것이 좋으며 이렇게 되면 새로운 비밀번호로만 로그인할 수 있게 된다.  
<br/>
<br/>

## 권한(Privilege)
5.7 버전까지는 글로벌 권한과 객체 단위의 권한으로 구분됐다.  
데이터베이스나 테이블 이외의 객체에 적용되는 권한을 글로벌 권한이라고 하며, 데이터베이스나 테이블을 제어하는 데 필요한 권한을 객체 권한이라고 한다.  
객체 권한은 GRANT 명령으로 권한을 부여할 때 반드시 특정 객체를 명시해야 하며, 글로벌 권한은 GRANT 명령에서 특정 객체를 명시하지 말아야 한다.  
예외적으로 ALL(or ALL PRIVILEGES)은 글로벌과 객체 권한 두 가지 용도로 사용될 수 있는데, 특정 객체에 ALL 권한이 부여되면 해당 객체에 적용될 수 있는 모든 객체 권한을 부여하며, 그러벌로 ALL이 사용되면 글로벌 수준에서 가능한 모든 권한을 부여하게 된다.  

8.0 버전부터는 5.7 버전의 권한에 다음의 동적 권한이 더 추가됐다.  
정적 권한은 소스코드에 고정적으로 명시돼 있는 권한을 의미하며, 동적 권한은 서버가 시작되면서 동적으로 생성하는 권한을 의미한다.  

5.7 버전까지는 SUPER라는 권한이 데이터베이스 관리를 위해 꼭 필요한 권한이었지만, 8.0부터는 SUPER 권한은 잘게 쪼개어져 동적 권한으로 분산됐다.  

사용자에게 권한을 부여할 때는 `GRANT` 명령어를 사용한다.  
각 권한의 특성(범위)에 따라 GRANT 명령의 ON 절에 명시되는 오브젝트(DB나 테이블)의 내용이 바뀌어야 한다.  

`GRANT privilege_list ON db.table TO 'user'@'host';`  
8.0 버전 부터는 존재하지 않는 사용자에 대해 GRANT 명령이 실행되면 에러가 발생하므로 반드시 사용자를 먼저 생성하고 GRANT 명령으로 권한을 부여해야 한다.  
GRANT OPTION 권한은 GRANT 명령의 마지막에 WITH GRANT OPTION을 명시해서 부여한다.  
privilege_list 에는 구분자(,)를 써서 앞의 표에 명시된 권한 여러 개를 동시에 명시할 수 있다.  
TO 키워드 뒤에는 권한을 부여할 대상 사용자를 명시하고, ON 키워드 뒤에는 어떤 DB의 어떤 오브젝트에 권한을 부여할지 결정할 수 있는데, 권한의 범위에 따라 사용하는 방법이 달라진다.  
<br/>

**글로벌 권한**  
```sql
GRANT SUPER ON *.* TO 'user'@'localhost';
```


글로벌 권한은 특정 DB나 테이블에 부여될 수 없기 때문에 글로벌 권한을 부옂할 때 GRANT 명령의 ON 절에는 항상 *.*를 사용하게 된다.  
*.*은 DB의 모든 오브젝트(테이블과 스토어드 프로시저, 함수 등)를 포함해서 MySQL 서버 전체를 의미한다.  
CREATE USER나 CREATE ROLE과 같은 글로벌 권한은 DB 단위나 오브젝트 단위로 브여할 수 있는 권한이 아니므로 항상 *.*로만 대상을 사용할 수 있다.  
<br/>

**DB 권한**  
```sql
GRANT EVENT ON *.* TO 'user'@'localhost';
GRANT EVENT ON emp.* TO 'user'@'localhost';
```

DB 권한은 특정 DB에 대해서만 권한을 부여하거나 서버에 존재하는 모든 DB에 대해 권한을 부여할 수 있기 때문에 위의 예제와 같이 ON 절에 *.*이나 emp.* 모두 사용할 수 있다.  
여기서 DB는 DB 내부에 존재하는 테이블뿐만 아니라 스토어드 프로그램들도 모두 포함한다.  
하지만 DB 권한만 부여하는 경우 emp.department 처럼 테이블까지 명시할 수 없다.  
특정 DB에 권한을 부여할 때 db.*로 설정하는 것이 가능하며, db.table로 오브젝트(테이블)까지 명시할 수는 없다.  
<br/>

**테이블 권한**
```sql
GRANT SELECT, INSERT, UPDATE, DELETE ON *.* TO 'user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON emp.* TO 'user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON emp.department TO 'user'@'localhost';
```

테이블 권한은 서버의 모든 DB에 대해 권한을 부여하는 것도 가능하며, 특정 DB의 오브젝트에 대해서만 권한을 부여하는 것도 가능하고, DB의 특정 테이블에 대해서만 권한을 부여하는 것도 가능하다.  

테이블의 특정 컬럼에 대해서만 권한을 부여하는 경우는 DELETE를 제외하며, 각 컬럼 뒤에 권한을 명시하는 형태로 부여한다.
```sql
GRANT SELECT, INSERT, UPDATE(dept_name) ON emp.department TO 'user'@'localhost';
```

컬럼 단위의 권한을 설정하면 권한이 하나라도 설정되면 나머지 모든 테이블의 모든 컬럼에 대해서도 권한 체크를 하기때문에 컬럼 하나에 대해서만 권한을 설정하더라도 전체적인 성능에 영향을 미칠 수 있기 때문에 잘 사용하지 않는다.  
컬럼 단위의 접급 권한이 필요하다면 권한을 허용하고자 하는 컬럼만으로 VIEW를 만들어 사용하는 방법을 생각하자.  

권한에 대해서 확인하고자 한다면 다음의 테이블을 참고하자
```sql
-- 정적 권한
mysql.user
mysql.db
mysql.tables_priv
mysql.columns_priv
mysql.procs_priv

-- 동적 권한
mysql.global_grants
```
<br/>
<br/>

## 역할(Role)

8.0 버전부터 권한을 묶어서 역할(Role)을 사용할 수 있게 됐다.  
실제 서버 내부적으로 역할(Role)은 계정과 똑같은 모습을 하고 있다.  

```sql
-- Role 정의
CREATE ROLE role_emp_read, role_emp_write;
```

위의 정의는 빈 껍데기만 있는 역할을 정의한 것이며 GRANT 명령으로 역할에 실질적인 권한을 부여하면 된다.   
<br/>

```sql
GRANT SELECT ON employees.* TO role_emp_read;
GRANT INSERT, UPDATE, DELETE ON employees.* TO role_emp_write;
```

권한을 사용하기 위해서는 계정에 부여해야 한다. 
```sql 
CREATE USER reader@'localhost' IDENTIFIED BY 'asdf1234'
CREATE USER writer@'localhost' IDENTIFIED BY 'asdf1234'

GRANT role_emp_read TO reader@'127.0.0.1'
GRANT role_emp_write TO wirter@'127.0.0.1'
```
<br/>

`SHOW GRANTS` 명령으로 계정이 가진 권한을 확인할 수 있다.  

이 상태에서 reader, writer 계정으로 로그인해서 DB 데이터를 읽기, 쓰기를 해도 권한이 없다는 오류를 만나게 된다.

계정이 역할을 사용할 수 있게 하려면 SET ROLE 명령을 실행해 해당 역할을 활성화 해야 한다.

```sql
SELECT current_role();
SELECT 'role_emp_read';
SELECT current_role();

SELECT COUNT(*) FROM emp.employees;
```
<br/>

MySQL 서버의 역할이 수동적으로 보이는데, 서버에 로그인할 때 역할을 자동으로 활성화할지 여부를 activate_all_roles_on_login 시스템 변수로 설정할 수 있다.  
시스템 변수가 ON으로 설정되면 매번 SET ROLE 명령으로 역할을 활성화하지 않아도 로그인과 동시에 부여된 역할이 자동으로 활성화된다.  

`SET GLOBAL activate_all_roles_on_login=ON;`  
<br/>

MySQL 서버의 역할은 사용자 계정과 거의 같은 모습을 하고 있으며, 서버 내부적으로 역할과 계정은 동일한 객체로 취급한다. 단지 하나의 사용자 계정에서 다른 사용자 계정이 가진 권한을 병합해서 권한 제어가 가능해졌을 뿐이다.  

```sql
-- 권한과 사용자 계정이 구분없이 저장된 것을 확인 가능
SELECT user, host, account_locked FROM mysql.user;
```

서버에서는 하나의 계정에 다른 계정의 권한을 병합하기만 하면 되므로 MySQL 서버는 역할과 계정을 구분할 필요가 없다.  

계정을 생성할 때는 계정명@호스트 형식으로 함꼐 명시하지만, ROLE을 생성할 떄는 호스트 부분을 명시하지 않는다.  이 때 호스트 부분을 따로 명시하지 않으면 모든 호스트(%)가 자동으로 추가된다.  

```sql
CREATE ROLE role_emp_read;
CREATE ROLE role_emp_read@'%';
```

역할과 계정을 명확하게 구분하고자 한다면 데이터베이스 관리자가 식별할 수 있는 prefix나 키워드를 추가해 역할의 이름을  선택하는 방법을 권장한다.  

역할과 계정은 내외부적으로 동일한 객체라고 했는데, `CREATE ROL` 명령어와 `CREATE USER` 를 구분해서 지원하는 이유는 데이터베이스 관리의 직무를 분리할 수 있게 해서 보안을 강화하는 용도로 사용될 수 있게 하기 위해서다.  
CREATE USER 명령에 대해서는 권한이 없지만 CREATE ROLE 명령만 실행 가능한 사용자는 역할을 생성할 수 있다. 이렇게 생성된 역할은 계정과 동일한 객체를 생성하지만 실제 이 역할은 account_locked 컬럼의 값이 'Y'로 설정돼 있어서 로그인 용도로 사용할 수 없게 된다.  

`mysql.default_roles` : 계정별 기본 역할  
`mysql.role_edges` : 역할에 부여된 역할 관계 그래프  




