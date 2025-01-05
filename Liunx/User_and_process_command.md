# 사용자와 프로세스 및 시스템 관리 명령어 정리

## su와 sudo

`su` 명령어는 "switch user"의 약자로 다른 사용자로 전환하는 명령어다. `sudo`는 "superuser do"의 약자로 사용자가 특정 명령을 다른 사용자의 권한으로 실행할 수 있게 한다.  

`su` 명령어보다 `sudo` 명령어를 선호하는데 sudo는 특정 사용자에게 제한된 권한을 부여하고, su는 사용자가 특정 사용자(루트 등)으로 전환하게 되므로 보안적으로 좋지 않다. 그리고 sudo는 실행 내역을 로그로 남기기 때문에, 어떤 사용자가 어떤 명령어를 실행했는지 추적할 수 있다. 
그리고 sudo는 사용자가 자신의 비밀번호로 인증할 수 있다.

```bash
# su 사용법
# 루트 사용자로 전환
su -

# 특정 사용자(exampleuser)로 전환
su exampleuser


# sudo 사용법
# sudo 명령어는 관리자 권한으로 명령을 실행할 때 사용한다.
sudo [명령어]

# -u <사용자>: 특정 사용자의 권한으로 명령을 실행한다.
# 명령을 실행할 때 지정된 사용자의 권한을 사용한다. (루트가 기본값)
sudo -u [사용자] [명령어]

# -i: 로그인 셸 환경을 시뮬레이트한다.
# 루트 사용자의 로그인 셸 환경을 설정하고 명령을 실행한다.
sudo -i [명령어]

# test 사용자와 설정된 환경변수로 로그인
sudo -u test -i
```

<br/>
<br/>

## 시스템 서비스 관리 service, systemctl

`service` 명령어는 과거 SysVinit 시스템에서 서비스를 관리하는데 사용했다. 현대의 리눅스 오픈 소스에서는 주로 `systemd`라는 새로운 시스템 및 서비스 관리자에서 사용하는 명령어인 `systemctl`를 사용한다.

### service 명렁어

```bash
# 서비스 목록 보기
sudo service --status-all

# 지정한 서비스를 시작한다.
sudo service [서비스명] start  

# 지정한 서비스를 중지한다.
sudo service [서비스명] stop   

# 지정한 서비스를 중지한 후 다시 시작한다.
sudo service [서비스명] restart 

# 지정한 서비스의 현재 상태를 확인한다.
sudo service [서비스명] status  

# 지정한 서비스를 부팅 시 자동으로 시작하도록 설정한다.
sudo service [서비스명] enable  

# 지정한 서비스를 부팅 시 자동으로 시작하지 않도록 설정한다.
sudo service [서비스명] disable 
```

### systemctl 명령어
```bash
# 시스템 서비스 시작
sudo systemctl start [서비스명]

# 시스템 서비스 중지
sudo systemctl stop [서비스명]

# 시스템 서비스 재시작
sudo systemctl restart [서비스명]

# 시스템 서비스 다시 로드 (재시작 없이 설정 파일만 다시 로드)
sudo systemctl reload [서비스명]

# 시스템 서비스 다시 로드 또는 재시작 (필요에 따라)
sudo systemctl reload-or-restart [서비스명]

# 시스템 서비스 활성화 (부팅 시 자동 시작)
sudo systemctl enable [서비스명]

# 시스템 서비스 비활성화 (부팅 시 자동 시작 안 함)
sudo systemctl disable [서비스명]

# 시스템 서비스 활성화 상태 확인
sudo systemctl is-enabled [서비스명]

# 시스템 서비스 상태 확인
sudo systemctl status [서비스명]

# 시스템 서비스 목록 보기
sudo systemctl list-units --type=service

# 모든 서비스 (활성 및 비활성 포함) 목록 보기
sudo systemctl list-unit-files --type=service
```

<br/>
<br/>

## 네트워크 관련 설정 및 상태 확인

리눅스에서 `net-tools` 명령어는 네트워크 관련 설정 및 상태 확인을 위한 다양한 명령어를 제공하는 도구 모음인데, `apt` 패지키 관리 명령어로 설치할 수 있다. 이 명령어에서 주로 `ifconfig`, `netstat`, `hostname`을 사용한다.  
네트워크 관련 명령어인 `netstat`를 설명한다.

```bash
# 설치
sudo apt install net-tools

# -a : 모든 연결 및 수신 대기 포트를 표시합니다.
netstat -a   # 모든 연결 및 수신 대기 포트를 나열합니다.

# -n : 호스트 이름을 해석하지 않고 숫자 형태로 IP 주소를 표시합니다.
netstat -n   # IP 주소를 숫자 형태로 표시합니다.

# -l : 수신 대기 중인 포트만 표시합니다.
netstat -l   # 현재 수신 대기 중인 포트만 나열합니다.

# -p : 각 연결에 대한 프로세스 ID 및 프로그램 이름을 표시합니다.
netstat -p   # 연결과 관련된 프로세스 정보를 표시합니다.

# -t : TCP 연결만 표시합니다.
netstat -t   # TCP 연결만 표시합니다.

# -u : UDP 연결만 표시합니다.
netstat -u   # UDP 연결만 표시합니다.

# -r : 라우팅 테이블을 표시합니다.
netstat -r   # 현재의 라우팅 테이블을 나열합니다.

# -i : 네트워크 인터페이스에 대한 정보를 표시합니다.
netstat -i   # 모든 네트워크 인터페이스 정보를 나열합니다.

# 주로 nlpt를 같이 사용한다.
netstat -nlpg
```

<br/>
<br/>

## 사용자 관련 명렁어

### `useradd`: 사용자 생성
`adduser` 명령어는 `useradd` 명령어의 심볼릭 링크이므로 useradd 사용을 권장한다.

```bash
-g # 기본그룹 없이 특정 그룹에 포함시킴(특정 그룹이 미리 생성되어 있어야 함)
-G # 기본그룹 외에 추가로 특정 그룹에 포함시킴
-d # 별도로 사용자의 홈 디렉토리를 지정하는데 지정하지 않으면 /home/계정명 이 된다.
-s # 별로도 사용자 쉘을 지정하는데 지정하지 않으면 /bin/bash 가 된다.
-m # 사용자를 생성할 때 홈디렉토리 자동 생성
-u # 별도로 사용자 uid를 지정하는데 지정하지 않으면 500번부터 순차적으로 지정됨
-M # 홈 디렉토리가 없는 임시 사용자를 만듬
-N # 자신 이름의 그룹을 만들지 않는 사용자를 만드는데 users 그룹으로 들어가게 됨
-D # 사용자 추가에 관련된 정보를 보임
-p # 평문장 패스워드를 지정해서 만듬

# 기본 사용자 생성 
useradd test

# test 사용자 생성 후 홈디렉토리를 자동생성하고 쉘 지정
useradd test -m -s /bin/bash

# 사용자 생성 및 디렉토리 지정
useradd roody -d /ex/roody # 지정안하면 /home/roody

# 사용자 id는 505 홈디렉토리, 비밀번호, 쉘 지정
useradd roody -u 505 -d /ex/roody -p roody11 -s /bin/bash
```

<br/>

### `passwd`: 비밀번호 설정
```bash
# 사용자 비밀번호 변경
# sudo를 통해 관리자 권한으로 변경시키는게 일반적이다. 사용자를 입력하지 않으면 루트 사용자의 비밀번호를 설정한다.
passwd
# 이후 패스워드 두 번 입력해서 지정

# sudo를 통해 관리자 권한으로 변경시키는게 일반적
passwd roody

# 사용자 확인
# 사용자명:암호화된 패스워드:UserID:GroupID:사용자 설명: 작업 디렉토리:사용하는 쉘
cat /etc/passwd 

# 암호화된 패스워드 확인
# 사용자명$암호화기법$패스워드/:패스워드를 변경한 날짜 수:다음번 패스워드 변경까지 날짜 수:현재 패스워드 유효 날짜:패스워드 만료 경고까지 날짜 수:패스워드 만료 시 사용자 계정이 불가능한 날까지 날짜:계정이 불가능하게 되는 날까지의 날짜 수:예약된 공간
cat /etc/shadow
```

<br/>

### `usermod`: 사용자 정보 수정

```bash
usermod [사용자명]

# 사용자의 로그인 이름 변경
usermod -l [새로운 사용자명] [기존 사용자명]

# 사용자의 UID 변경
usermod -u [새 UID] [사용자명]

# 사용자의 기본 그룹 변경
usermod -g [새로운 그룹] [사용자명]

# 사용자를 추가 그룹에 추가 (기존 그룹은 유지)
usermod -aG [추가할 그룹] [사용자명]

# 사용자의 홈 디렉토리 변경
usermod -d [새 홈 디렉토리] [사용자명]

# 사용자의 로그인 쉘 변경
usermod -s [새 로그인 쉘] [사용자명]

# 사용자의 계정 만료 날짜 설정 (YYYY-MM-DD 형식)
usermod -e [만료 날짜] [사용자명]

# 사용자의 비밀번호 만료 기간 설정 (일 단위)
usermod -f [만료 기간] [사용자명]

# 사용자의 계정을 잠금
usermod -L [사용자명]

# 사용자의 계정 잠금 해제
usermod -U [사용자명]

# 사용자에게 sudo 권한 부여 (sudo 그룹에 추가), 기존 그룹은 유지
sudo usermod -aG sudo roody

# username의 홈 디렉토리를 /new/home/directory로 변경한다. 단, 디렉토리의 내용은 이동되지 않음
sudo usermod -d /new/home/directory username

# username을 보조 그룹 group1과 group2에 추가한다. -a 옵션은 기존 그룹을 유지하면서 새로운 그룹을 추가하는 데 사용
sudo usermod -aG group1,group2 username 
```

<br/>

### `userdel`: 사용자 정보 삭제

```bash
userdel [사용자명]

-r # 사용자가 만든 파일이나 홈 디렉토리도 제거
userdel -r [사용자명]
```

<br/>

### 그룹 관련 명령어
그룹 관련 명령어들은 주로 그룹을 생성, 수정, 삭제하거나 그룹에 사용자를 추가 및 제거하는 데 사용된다.  
`/etc/group` 파일은 시스템의 그룹 정보를 저장하는 파일이다. 직접 편집하여 그룹 정보를 수정할 수 있다.  

- `groups`: 사용자가 속한 그룹들을 출력한다.
- `groupadd`: 새로운 그룹을 생성한다.
- `groupmod`: 기존 그룹의 속성을 수정한다.
- `groupdel`: 기존 그룹을 삭제한다.
- `gpasswd`: 그룹의 비밀번호를 설정하거나 그룹 관리자를 지정하며, 그룹에 사용자를 추가 또는 제거한다.

```bash
# 현재 사용자가 속한 그룹들을 출력한다
groups

# 특정 사용자 roody이 속한 그룹들을 출력한다
groups roody


# 그룹 이름이 mygroup인 그룹을 생성한다
groupadd mygroup

# 그룹 ID를 1001로 지정하여 mygroup을 생성한다
groupadd -g 1001 mygroup


# 그룹 이름을 mygroup에서 newgroup으로 변경한다
groupmod -n newgroup mygroup

# 그룹 ID를 1002로 변경한다
groupmod -g 1002 mygroup


# 그룹 이름이 mygroup인 그룹을 삭제한다
groupdel mygroup


# 그룹 mygroup의 비밀번호를 설정한다
gpasswd mygroup

# 그룹 mygroup에 사용자 roody을 추가한다
gpasswd -a roody mygroup

# 그룹 mygroup에서 사용자 roody을 제거한다
gpasswd -d roody mygroup

# 그룹 mygroup의 관리자를 roody로 지정한다
gpasswd -A roody mygroup
```

<br/>

### `chown`
`chown` 명령어는 "change owner"의 줄임말로 리눅스에서 파일이나 디렉토리의 소유자(user)와 그룹(group)을 변경하는 데 사용된다.

```bash
# 소유자는 변경하려는 파일의 새로운 소유자를 나타내고, 그룹은 새로운 그룹을 나타낸다. 그룹은 생략할 수 있다.
chown [옵션] [소유자][:그룹] [파일/디렉터리]

# -R: 재귀적으로 하위 디렉터리 및 파일의 소유자를 변경한다.
# -f: 오류 메시지를 표시하지 않고 강제로 수행한다.
# -v: 변경 사항을 자세히 출력한다.
# --reference=파일: 지정한 파일의 소유자와 그룹으로 변경한다.

# file.txt의 소유자를 roody로 변경한다.
chown roody file.txt

# file.txt의 소유자를 roody로, 그룹을 group1으로 변경한다.
chown roody:group1 file.txt

# /path/to/directory 및 그 하위 모든 파일과 디렉터리의 소유자를 roody로 변경한다.
chown -R roody /path/to/directory

# file.txt의 소유자를 roody로 변경하며, 오류가 발생해도 메시지를 출력하지 않는다.
chown -f roody file.txt

# file.txt의 소유자를 roody로 변경하며, 변경 사항을 자세히 출력한다.
chown -v roody file.txt

# file.txt의 소유자와 그룹을 ref_file.txt와 동일하게 변경한다.
chown --reference=ref_file.txt file.txt
```

<br/>

### `chmod`

`chmod` 명령어는 리눅스에서 파일 또는 디렉터리의 접근 권한을 변경하는 데 사용된다. 접근 권한에는 읽기, 쓰기, 실행 권한이 포함되며, 이를 소유자, 그룹, 기타 사용자에 대해 설정할 수 있다.  
기호적(sybmolic) 표현과 숫자적(octal) 표현으로 설정 가능하다. 

```bash
chmod [옵션] [권한] [파일/디렉터리]

## 기호적 표현
# r: 읽기 권한 (read)
# w: 쓰기 권한 (write)
# x: 실행 권한 (execute)
# u: 소유자 (user)
# g: 그룹 (group)
# o: 기타 사용자 (others)
# a: 모든 사용자 (all)

## 숫자적 표현
# r: 4
# w: 2
# x: 1

## 옵션
# R: 디렉터리와 그 하위 파일들에 대해 재귀적으로 권한을 변경한다.
# v: 변경된 내용을 상세하게 출력한다.
# c: 변경된 파일만 출력한다.

# test.txt 파일의 소유자에게 읽기, 쓰기, 실행 권한을 부여하고, 그룹과 기타 사용자에게 읽기 권한만 부여
chmod u=rwx,g=r,o=r test.txt

# test.txt 파일의 소유자에게 읽기, 쓰기, 실행 권한을 부여하고, 그룹과 기타 사용자에게 읽기 권한만 부여 (숫자적 표현)
chmod 744 test.txt

# 기존 권한에 실행 권한을 추가
chmod +x example.txt

# test 디렉토리와 그 내부의 모든 파일과 디렉토리에 대해 재귀적으로 권한을 변경 (소유자에게 모든 권한, 그룹과 기타 사용자에게 읽기 권한)
chmod -R 744 test

# test.txt 파일의 소유자에게 쓰기 권한을 추가
chmod u+w test.txt

# test.txt 파일의 그룹 사용자에게 실행 권한을 제거
chmod g-x test.txt

# test 디렉토리와 그 내부의 모든 파일과 디렉토리에 대해 재귀적으로 권한을 변경하며, 변경된 내용들을 상세하게 출력
chmod -Rv 755 test

# test.txt 파일의 권한을 변경하고, 변경된 파일에 대해서만 출력
chmod -c 755 test.txt
```

<br/>

### 시스템 사용자에 관한 정보

- `who`: 현재 시스템에 로그인한 사용자 정보를 출력한다. 로그인한 사용자의 사용자 이름, 터미널, 로그인 시간 등을 표시한다.
- `w`: 현재 시스템에 로그인한 사용자와 그들이 실행 중인 프로세스를 출력한다. 사용자 이름, 터미널, 원격 호스트, 로그인 시간, 유휴 시간, 현재 작업 등의 정보를 포함한다.
- `last`: /var/log/wtmp 파일을 읽어 시스템에 로그인한 사용자의 기록을 출력한다. 사용자 이름, 로그인 시간, 로그아웃 시간, 로그인한 터미널 등을 포함한다.
- `lastb`: /var/log/btmp 파일을 읽어 실패한 로그인 시도의 기록을 출력한다. 주로 보안 감사와 관련하여 실패한 로그인 시도를 추적하는 데 사용된다.
- `whoami`: 현재 사용자의 사용자 이름을 출력한다. 현재의 효과적인 사용자 ID를 기반으로 한다.
- `id`: 현재 사용자나 지정된 사용자의 사용자 ID 및 그룹 ID 정보를 출력한다. 사용자 ID(uid), 기본 그룹 ID(gid), 부가 그룹 ID를 포함한다.
- `users`: 현재 시스템에 로그인한 모든 사용자 이름을 간단하게 출력한다. 각 사용자 이름은 공백으로 구분된다.
- `finger`: 사용자에 대한 정보를 자세히 출력한다. 사용자 이름, 실제 이름, 로그인 쉘, 홈 디렉토리, 로그인 시간, 유휴 시간, 사무실 위치, 전화번호 등을 포함한다. 이 명령어는 추가 패키지(finger 패키지)를 설치해야 사용할 수 있다.

```bash
# who: 현재 시스템에 로그인한 사용자에 대한 정보를 출력한다.
# -b: 마지막 시스템 부팅 시간을 출력한다.
# -d: 사망한 프로세스를 출력한다.
# -H: 출력 헤더를 포함한다.
# -l: 현재 대기 중인 프로세스를 출력한다.
# -q: 사용자의 로그인 이름과 로그인 수를 출력한다.
who

# w: 현재 시스템에 로그인한 사용자와 각 사용자가 실행 중인 프로세스에 대한 정보를 출력한다.
# -h: 헤더를 출력하지 않는다.
# -s: 짧은 포맷으로 출력한다.
# -u: 사용자별로 로그인 시간과 사용된 시간을 출력한다.
w

# whoami: 현재 사용자의 사용자 이름을 출력한다.
whoami

# last: 시스템의 과거 로그인 기록을 출력한다.
# -n [count]: 최근 [count] 개의 기록만 출력한다.
# -R: 원격 호스트 정보를 출력하지 않는다.
# -a: 세션의 종료 시간을 출력한다.
last

# lastb: 시스템의 실패한 로그인 시도를 출력한다 (일반적으로 /var/log/btmp 파일에서 정보를 가져옴).
# -n [count]: 최근 [count] 개의 기록만 출력한다.
# -R: 원격 호스트 정보를 출력하지 않는다.
# -a: 세션의 종료 시간을 출력한다.
lastb

# id: 현재 사용자의 UID, GID 및 그룹 정보를 출력한다.
# -u: 사용자 ID만 출력한다.
# -g: 그룹 ID만 출력한다.
# -G: 사용자가 속한 모든 그룹의 ID를 출력한다.
# -n: 숫자가 아닌 이름으로 출력한다.
# id [사용자명]: 특정 사용자의 정보를 출력한다.
id

# users: 현재 시스템에 로그인한 사용자의 이름을 출력한다.
users

# finger: 사용자 정보 및 로그인 세부 정보를 출력한다. 기본적으로 시스템의 /etc/passwd 파일을 사용한다.
# -s: 사용자 이름, 실제 이름, tty, 로그인 시간 등을 짧게 출력한다.
# -l: 사용자의 전체 정보를 출력한다.
# -p: 사용자의 .plan 파일을 출력하지 않는다.
finger

/var/log/wtmp # last 명령어의 로그 기록
/var/run/utmp # w, who, finger 명령어의 로그 기록
/var/log/btmp # lastb 명령어로 실패한 로그인 정보
```

<br/>
<br/>

## 프로세스 관련 명령어

### `ps`: 현새 실행되고 있는 프로세스 상태 목록

`ps` 명령어는 현재 실행 중인 프로세스의 상태를 출력하는 데 사용된다.

```bash
# ps 명령어의 사용 예시와 설명

# 현재 사용자가 실행 중인 프로세스를 보여준다.
ps

# -A 또는 -e: 모든 프로세스를 출력한다.
ps -A
ps -e

# -u: 사용자와 그에 대한 프로세스 정보를 출력한다.
ps -u [사용자명]

# -x: 터미널에 연결되지 않은 프로세스를 포함하여 출력한다.
ps -x

# -f: 프로세스의 전체 포맷을 출력한다. 부모 PID와 같은 추가 정보를 포함한다.
ps -f

# -l: 긴 포맷으로 프로세스를 출력하여 더 많은 정보를 제공한다.
ps -l

# -o: 사용자 지정 출력 형식을 지정할 수 있다. PID, USER, COMMAND 등을 지정할 수 있다.
ps -o pid,user,command

# -p: 특정 PID의 프로세스 정보를 출력한다.
ps -p [PID]

# --sort: 출력 결과를 특정 필드로 정렬할 수 있다. --sort=-%mem은 메모리 사용량 기준으로 내림차순 정렬한다.
ps --sort=-%mem

# -C: 특정 명령어 이름으로 프로세스를 필터링하여 출력한다.
ps -C [명령어명]

# --forest: 프로세스 계층 구조를 트리 형태로 출력한다.
ps --forest

# 모든 프로세스를 풀 포맷으로 출력하고, 그 결과를 페이지 단위로 보여준다.
ps -ef | less


# 자주 쓰이는 방법
# 모든 프로세스 자세히 출력 (자주 쓰임)
ps -ef 
ps -ef | grep [프로세스명]
ps -ef | grep bash

# 모든 사용자와 모든 프로세스를 포함해 상세 정보를 출력한다.
# a: 모든 사용자 프로세스, u: 사용자 정보, x: 터미널에 연결되지 않은 프로세스 포함
ps aux
# 모든 사용자와 모든 프로세스를 출력한 후, 특정 프로세스명으로 필터링한다.
ps aux | grep [프로세스명]
```

<br/>

### `kill`: 프로세스를 강제로 종료하는 명령어

```bash
# kill 명령어의 옵션 설명

# -0: 프로세스가 존재하는지 확인. 
# 프로세스가 존재하면 0을 반환하고, 없으면 1을 반환한다. 이 신호는 실제로 프로세스를 종료하지 않는다. 
kill -0 [PID]

# -1: 프로세스에 SIGHUP 신호를 보낸다.
# 일반적으로 세션 종료 시 프로세스를 종료하는 데 사용된다.
kill -1 [PID]

# -2: 프로세스에 SIGINT 신호를 보낸다.
# 사용자 인터럽트를 나타내며, Ctrl+C로 프로세스를 중단할 때 사용된다.
kill -2 [PID]

# -3: 프로세스에 SIGQUIT 신호를 보낸다.
# 이 신호를 받으면 프로세스는 종료하면서 코어 덤프를 생성할 수 있다.
kill -3 [PID]

# -4: 프로세스에 SIGILL 신호를 보낸다.
# 잘못된 명령을 실행할 때 발생하며, 보통 프로세스가 중단된다.
kill -4 [PID]

# -5: 프로세스에 SIGTRAP 신호를 보낸다.
# 디버깅을 목적으로 발생시키는 신호로, 주로 디버거에서 사용된다.
kill -5 [PID]

# -6: 프로세스에 SIGABRT 신호를 보낸다.
# 프로세스가 비정상적으로 종료되도록 유도하며, 보통 abort() 호출 시 발생한다.
kill -6 [PID]

# -7: 프로세스에 SIGBUS 신호를 보낸다.
# 잘못된 메모리 접근 시 발생하며, 프로세스가 강제 종료된다.
kill -7 [PID]

# -8: 프로세스에 SIGFPE 신호를 보낸다.
# 산술 연산에서 오류가 발생했을 때 발생한다. 예를 들어, 0으로 나누기.
kill -8 [PID]

# -9: 프로세스에 SIGKILL 신호를 보낸다.
# 프로세스를 강제로 종료하며, 프로세스가 이 신호를 무시할 수 없다.
kill -9 [PID]

# -15: 프로세스에 SIGTERM 신호를 보낸다.
# 프로세스 종료를 요청하는 신호이며, 프로세스가 이를 수신하고 종료 작업을 수행할 수 있다.
kill -15 [PID]
# kill -15로 종료된 것은 systemctl 입장에선 exit 된 것이고
# 안전한 종료 후에는 systemctl restart로만 실행된다.
# 안정한 종료는 systemctl입장에서는 중지된 상태가 된다.

# -19: 프로세스에 SIGSTOP 신호를 보낸다.
# 프로세스를 일시 중지시키는 신호로, 프로세스가 이를 무시할 수 없다.
kill -19 [PID]
```

<br/>

### `top`: CPU, RAM 사용량 등 시스템의 현재 상태를 실시간으로 모니터링

```bash
# top: 현재 시스템에서 실행 중인 프로세스들의 실시간 상태를 모니터링한다.
# CPU 사용량, 메모리 사용량, 각 프로세스별 상태 등을 확인할 수 있다.
top

# -1: 각 CPU 코어별로 사용량을 표시한다. 멀티코어 시스템에서 각 코어의 사용량을 개별적으로 확인할 수 있다.
top -1

# -d: 화면 업데이트 간격을 지정한다. 기본값은 3초이다.
top -d 5

# -p: 특정 PID를 가진 프로세스만 모니터링한다.
top -p 1111

# -n: 지정된 횟수만큼 화면을 업데이트한 후 종료한다.
top -n 10

# -u: 특정 사용자의 프로세스만을 표시한다.
top -u username

# -b: 배치 모드로 실행하여 결과를 표준 출력으로 출력한다. 주로 스크립트나 로그 파일에 결과를 저장할 때 유용하다.
top -b -n 1 > top_output.txt

# -c: 명령어 라인으로 프로세스를 표시한다. 기본적으로 프로세스 이름만 표시되지만, 이 옵션을 사용하면 전체 명령어 라인이 표시된다.
top -c

# -H: 스레드 정보를 포함하여 출력한다. 기본적으로 top 명령어는 프로세스 정보를 표시하지만, 이 옵션을 사용하면 프로세스 내의 각 스레드에 대한 정보를 볼 수 있다.
top -H

# -i: 유휴(idle) 프로세스를 숨긴다. CPU 사용량이 거의 없는 프로세스를 숨겨서 현재 활성 상태의 프로세스만 볼 때 유용하다.
top -i

# -M: 메모리 단위를 MB로 전환하여 표시한다. 기본적으로 kB 단위로 표시
top -M

# -o: 지정된 필드 기준으로 정렬한다. 예를 들어, %CPU 사용률 기준으로 정렬한다.
top -o %CPU
```

<br/>

### `free`: 메모리 사용량을 확인

`free` 명령어로 시스템의 총 메모리, 사용 중인 메모리, 남은 메모리, 버퍼와 캐시 메모리의 상태를 확인할 수 있다.

```bash
free

# -b: 메모리 량을 byte로 표시
# -k: 메모리 량을 Kbyte로 표시
# -m: 메모리 량을 Mbyte로 표시
# -o: 버퍼 메모리는 표시하지 않음
# -t: 총 메모리를 표시
# -s: 지정된 시간마다 계속 출력

free -mt

# 5초마다 메모리 정보 보이기
# 취소하려면 Ctrl + C
free -mt -s 5 
```

<br/>

### `bg`, `fg`, `jobs`: 백그라운드 및 포그라운드 작업을 관리하는 데 사용

```bash
# 가장 최근에 일시 중지되거나 백그라운드에서 실행 중인 작업을 포그라운드로 가져온다.
fg 

# 특정 작업 번호를 지정하여 포그라운드로 가져온다.
# 작업 번호 jobId번을 포그라운드로 가져옴
fg %jobId 

# 가장 최근에 일시 중지된 작업을 백그라운드에서 재개한다.
bg

# 특정 작업 번호를 지정하여 백그라운드에서 재개한다.
# 작업 번호 jobId번을 백그라운드에서 재개
bg %jobId  

# 현재 세션에서 실행 중인 모든 작업의 상태를 출력한다.
jobs
# -l: 각 작업의 PID를 포함하여 자세한 정보를 출력한다.
# -p: 각 작업의 PID만 출력한다.
# -r: 실행 중인 작업만 출력한다.
# -s: 중지된 작업만 출력한다.
# -n: 상태가 변경된 작업만 출력한다.
```

<br/>

### `nohup`: 백그라운드에서 작업을 지속적으로 실행

`nohup` 명령어는 "no hang up"의 약자로, 사용자가 로그아웃하거나 터미널 세션이 종료되더라도 백그라운드 작업을 지속적으로 실행할 수 있도록 해주는 유닉스 계열 운영 체제에서 제공하는 명령어이다.  
일반적으로 터미널에서 작업을 실행할 때, 터미널을 닫거나 세션을 종료하면 그 작업도 함께 종료된다. 하지만 nohup을 사용하면 이러한 작업을 백그라운드에서 계속 실행할 수 있다.  
기본적으로 nohup은 표준 출력(stdout)과 표준 에러(stderr)를 nohup.out 파일에 기록한다. 이 파일은 명령어를 실행한 디렉토리에 생성된다.

```bash
# nohup 명령어의 기본 형식
# 명령어를 백그라운드에서 실행하고, 터미널 종료 후에도 계속 실행되게 합니다.
# & 기호는 명령어를 백그라운드에서 실행하도록 합니다.
nohup 명령어 [인자]...
nohup 명령어 [인자] &

# 버전 확인
nohup --version

# 설치
sudo apt -y install nohup

# 터미널 세션이 끊어져도 동작
nohup java -jar *.jar

# 터미널을 종료해도 배포 유지(백그라운드 진행)
nohup java -jar *.jar &

# 백그라운드에서 실행하기 때문에 로그가 안보이는 문제 발생
build/libs/nohup.out 에서 확인

# 확인하는 법
tail -f nohup.out

# 로그파일 변경
nohup java -jar *.jar > 새로그파일명 &
nohup java -jar *.jar > mylog.out &
nohup java -jar *.jar > output.log 2>&1 &

# 백그라운드 실행죽인 jar 종료
kill -9 프로세스번호

# 표준 출력, 에러로그 출력 분리
# 리다이렉션 설정 사이에 공백이 있으면 안됨
# 표준 출력 : 1
# 에러 출력 : 2
nohup java -jar 파일명 1>log.out 2>err.out &

# 1>/dev/null 표준 출력은 제거(휴지통으로 감)
# 2>&1 표준 에러 출력은 표준 출력으로 제어
# 0</dev/null 표준 입력은 차단
# & 백그라운드로 실행
nohup 파일명 1>/dev/null 2>&1 0</dev/null &
```

<br/>
<br/>

## 패키지 관리

### `apt`
`apt`는 Debian 계열의 리눅스 배포판(예: Ubuntu)에서 패키지 관리를 위해 사용되는 명령어다. 

```bash
# 패지키를 설치하지 전 update로 가져온 패키지 메타 데이터에서 패키지에 대한 정보나 의존성 문제를 미리 확인하고, 해당 패키지와 관련된 패키지들을 검색해서 출력한다.
apt search [패키지명] # 출력 결과가 더 직관적으로 표시
apt-cache search [패키지] # 패키지 데이터베이스에서 직접 검색하여 결과를 반환, 결과가 더 간단히 나타남

# 지정한 패키지를 검색한다.
sudo apt-cache search jdk

# 설치 여부와 관계없이 패키지에 대한 의존성 정보를 확인
apt-cache depends 패키지이름

# 패키지 역의존성 확인
apt-cache rdepends [패키지이름]

# 현재 시스템에 설치된 패키지 목록을 최신 상태로 업데이트한다.
sudo apt update

# 모든 패키지를 최신 버전으로 업그레이드한다.
sudo apt upgrade

# 지정한 패키지를 설치한다. 예: sudo apt install vim
sudo apt install [패키지이름]

# 지정한 패키지를 제거한다. 예: sudo apt remove vim
sudo apt remove [패키지이름]

# 지정한 패키지와 설정 파일을 완전히 제거한다.
sudo apt purge [패키지이름]

# 패키지 검색
# 패키지 목록에서 지정한 패키지를 검색한다. 예: apt search vim
apt search [패키지이름]

# 지정한 패키지의 상세 정보를 출력한다. 예: apt show vim
apt show [패키지이름]

# 더 이상 필요하지 않은 패키지를 자동으로 제거한다.
sudo apt autoremove

# 지정한 패키지를 다운로드만 하고 설치하지 않는다.
apt download [패키지이름]

# 배포판 업그레이드를 포함하여 시스템을 업그레이드한다.
sudo apt dist-upgrade

# 다운로드된 패키지 파일을 삭제하여 캐시를 정리한다.
sudo apt clean

# 더 이상 필요하지 않은 패키지 캐시 파일을 삭제한다.
sudo apt autoclean
```

<br/>

## `apt-get`

`apt-get` 명령어는 `apt`의 이전 버전이며, 여전히 많은 리눅스 배포판에서 사용되고 있다. apt와 비교하여 더 세부적인 옵션을 제공하며, 스크립트나 자동화 작업에 더 적합하다. 

```bash
# 현재 시스템에 설치된 패키지 목록을 최신 상태로 업데이트한다.
sudo apt-get update

# 모든 패키지를 최신 버전으로 업그레이드한다.
sudo apt-get upgrade

# 지정한 패키지를 설치한다. 예: sudo apt-get install vim
sudo apt-get install [패키지이름]

# 지정한 패키지를 제거한다. 예: sudo apt-get remove vim
sudo apt-get remove [패키지이름]

# 지정한 패키지와 설정 파일을 완전히 제거한다.
sudo apt-get purge [패키지이름]

# 더 이상 필요하지 않은 패키지를 자동으로 제거한다.
sudo apt-get autoremove

# 지정한 패키지를 다운로드만 하고 설치하지 않는다.
apt-get download [패키지이름]

# 배포판 업그레이드를 포함하여 시스템을 업그레이드한다.
sudo apt-get dist-upgrade

# 다운로드된 패키지 파일을 삭제하여 캐시를 정리한다.
sudo apt-get clean

# 더 이상 필요하지 않은 패키지 캐시 파일을 삭제한다.
sudo apt-get autoclean

# 지정한 패키지의 소스 코드를 다운로드한다. 예: apt-get source vim
sudo apt-get source [패키지이름]

# 지정한 패키지의 의존성을 확인한다. 예: apt-cache depends vim
sudo apt-cache depends [패키지이름]

# 지정한 패키지의 상세 정보를 출력한다. 예: apt-cache show vim
sudo apt-cache show [패키지이름]

# 패키지 목록에서 지정한 패키지를 검색한다. 예: apt-cache search vim
sudo apt-cache search [패키지이름]
```

<br/>

### `wget`
wget은 웹에서 파일을 다운로드하는 데 사용되는 명령어다. 비대화형(non-interactive) 방식으로 동작하므로, 사용자가 입력을 기다리지 않고 백그라운드에서 파일을 다운로드할 수 있다. wget은 HTTP, HTTPS, FTP와 같은 프로토콜을 지원한다.  

```bash
# 기본명령어: 주어진 URL에서 파일을 다운로드한다.
wget [URL]
wget [옵션] [URL]

# -O: 다운로드한 파일을 지정한 이름으로 저장한다.
sudo wget -O [파일명] [URL]

# -c: (continue) 중단된 다운로드를 이어서 한다. 
sudo wget -c [URL]

# -b:  (background) 백그라운드 모드로 다운로드를 수행한다.
sudo wget -b [URL]

# -q: (quiet) 메시지 출력을 하지 않도록 조용하게 다운로드한다. 
sudo wget -q [URL]

# -r: (recursive) 주어진 URL의 디렉토리를 재귀적으로 다운로드한다. 
sudo wget -r [URL]

# -l: (level) 재귀 다운로드 시 최대 몇 단계까지 탐색할지 설정한다. 
sudo wget -l [레벨] [URL]

# -t: 다운로드 실패 시 재시도 횟수를 설정한다. 기본값은 20회이다.
sudo wget -t [횟수] [URL]

# -T: 각 시도마다 걸리는 시간을 제한한다. 기본값은 900초 (15분)이다.
sudo wget -T [초] [URL]
```

<br/>
<br/>