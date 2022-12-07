# 서버 구축 시 필요한 개념 및 명령어
![Untitled](https://user-images.githubusercontent.com/95058915/206116509-2751ecc2-6ad3-4549-827d-08f18174ffe5.png)
<br/>

**명령어의 모든 것은 대문자 및 소문자를 구분한다.**

```bash
# 종료하는 방법
poweroff
shutdown -P now
halt -p
init now
shutdown -h +5 # 5분 뒤 종료
shutdown -k +10 # 10분 뒤 종료(가짜)

# 시스템 재부팅
shutdown -r now
reboot
init 6

# 로그아웃
logout
exit

# 종료 취소
shutdown -c
```

### **리눅스 기본 명령어**

```bash
# 경로
.. # 상대경로 ..etc/systemd
/ # 절대경로 /etc/systemd

# 디렉토리에 있는 파일 목록을 나열 list
ls
# 디렉토리 내의 모든 파일을 출력
ls -a
# 파일의 inode번호를 출력
ls -i
# 파일의 소유자, 권한, 크기, 날짜 같이 출력
ls -l
# 파일을 쉼표로 구분하여 출력
ls -m
# 파일을 생성된 시간순으로 출력
ls -t
# 지정 경로에 있는 최상위 디렉토리만 출력
ls -d
# 파일의 형태와 함께 출력(* : 실행파일, @ : 심볼릭 링크, / : 디렉토리)
ls -F
# 서브디렉토리의 내용도 함께 출력
ls -R
# 파일을 크기순으로 출력
ls -S
# 다른 디렉토리의 목록 확인
ls 경로

# 디렉토리로 이동 change directory
cd
cd 경로
cd .. # 상대경로
cd / # 절대경로

# 현재 디렉토리의 전체 경로를 출력 print working directory
pwd

# 파일 제거 remove
rm 
# 디렉토리 삭제
rm -r
# 디렉토리, 파일 강제 삭제
rm -f
# 디렉토리 강제 삭제, 삭제 시 삭제 확인 메시지 출력 X
rm -rf
# 삭제되는 대상의 정보 출력
rm -v

# 파일이나 디렉터리 복사
cp 파일명 새파일명

# 크기가 0인 새 파일을 생성
touch 파일명

# 파일 내용 출력
cat 파일명
# 페이지 단위로 파일 내용 출력
more 파일명
less 파일명
# 파일 내용의 상위 10줄 출력
head 파일명
head -5 파일명
# 파일 내용의 하위 10줄 출력
tail 파일명
tail -5 파일명

# 파일과 디렉토리의 이름을 변경하거나 위치 이동
mv 파일명 변경할파일명

# 새로운 디렉토리 생성
mkdir 디렉토리명
# 디렉토리 제거(비어있는 디렉토리만 가능)
rmdir 디렉토리명

# 파일의 종류 확인
file 파일명

# 화면 클리어
clear

```

### **가상 콘솔**

# : 루트 사용자, $: 일반 사용자

Ctrl + Alt + F2 ~ F7 (F2는 X윈도우 모드)

### **런 레벨(Run Level)**

init 명령어 뒤에 붙는 숫자를 런레벨(RunLevel)이라고 부른다.

```bash
# 종료모드 Power Off
init 0
# 시스템 복구 모드 Rescue
init 1
# Multi-User 잘 사용하지 않음
init 2
# 텍스트 모드의 다중 사용자 모드
init 3
# Multi-User 잘 사용하지 않음
init 4
# 그래픽 모드의 다중 사용자 모드 Graphical
init 5
# 재시작
init 6

# 런레벨 파일 확인
cd /lib/systemd/system 
ls -l runlevel?.target
```

### **자동완성과 히스토리**
- 파일명의 일부만 입력 후 Tab키를 눌러 나머지 파일명을 자동으로 완성하는 기능
- 파일이 여러 개일 경우 Tab키를 두 번 누르면 폴더 확인 가능
- 도스 키란 이전에 입력한 명령어를 상/하 화살표 키를 이용해서 다시 나타내는 기능

```bash
# 이전까지 입력한 명령어 확인
history
# 히스토리 클리어
history -c
```

### **에디터 사용**

gedit, nano, vi, vim

```bash
# gedit 에디터 생성
gedit
# nano 생성
nano
nano 파일명
# nano 에디터를 사용한 파일 열기
nano 파일명
nano -c 파일명 # 행 번호 출력
# vi 에디터 생성
vi
vi 파일명

# 도움말
man 명령어
man ls
```

### **마운트와 CD/DVD, USB의 활용**

물리적인 장치를 특정한 위치(디렉토리)에 연결 시켜주는 과정

```bash
# 확인
mount
# 마운트 연결 끊기
umount 경로
umount /dev/cdrom
```

### **사용자와 그룹**

- 리눅스는 다중 사용자 시스템(Multi-User System)이다.
- 기본적으로 root라는 이름을 가진 수퍼유저(SuperUser)가 있으며, 모든 작업을 할 수 있는 권한이 있다.
- 모든 사용자는 하나 이상의 그룹에 소속되어 있다.
- 사용자는 /etc/passwd 파일에 정의되어 있다.
    - 각 행의 의미 ⇒ 사용자 이름:암호:사용자 ID:사용자가 소속된 그룹 ID:추가정보:홈 디렉토리:기본 셸
- 생성된 사용자의 정보는 /home/ 에 생성된다.
- 사용자의 비밀번호는 /etc/shadow 파일에 정의되어 있다.
- 그룹은 /etc/group 파일에 정의되어 있다.
    - 각 행의 의미 ⇒ 그룹명:비밀번호:그룹 id:보조 그룹 사용자

```bash
# 새로운 사용자 추가
adduser 사용자명
# 사용자 생성 시 옵션
--uid # ID 지정
--gid # 그룹 지정
--home # 홈 디렉토리 지정
--shell # 셸 지정

# 사용자의 비밀번호를 지정하거나 변경
passwd 사용자명

# 사용자의 속성을 변경
usermod

# 사용자를 삭제
userdel

# 사용자의 암호를 주기적으로 변경하도록 설정
chage -m 2 사용자명

# 현재 사용자가 속한 그룹을 보여줌
groups

# 새로운 그룹을 생성
groupadd 그룹명

# 그룹의 속성을 변경
groupmod --new-name mygroup newgroup

# 그룹을 삭제 - 그룹안의 사용자가 없어야 함
groupdel 그룹명

# 그룹의 암호를 설정하거나, 그룹의 관리를 수행
gpasswd 그룹명
```

### **파일과 디렉토리의 소유와 허가권**

파일의 리스트와 파일 속성

ex) - rw-r--r-- 1 root root 0 5월7일 14:34 sample.txt
파일 유형-파일 허가권-링크 수- 파일 소유자 이름-파일 크기(Byte)-마지막 변경 날짜/시간-파일 이름

- 파일 유형
    - 디렉터리일 경우에는 d, 일반적인 파일의 경우에는 - 표시
- 파일 허가권
    - “rw-”, “r—”, “r—” 3개씩 끊어서 읽는다.
    - r은 read, w는 write, x는 execute
    - 첫 번째 “rw-”는 소유자(User)의 파일 접근 권한
    - 두 번째 “r—”는 그룹(Group)의 파일 접근 권한
    - 세 번째 “r—”는 그 외의(기타) 사용자(Other)의 파일 접근 권한
    - 숫자로도 표시 가능(8진수) (0~7) ⇒ 7이면 모두 허용

```bash
# 현재 사용자 확인
whoami
# 사용자 계정 전환
su - 사용자명

# 파일 허가권 변경 명령어 chmod
chmod 777 파일명 # 읽기 쓰기 실행 모두 허용

# 파일 소유권(Ownership) - 루트 사용자만 사용
#파일을 소유한 사용자와 그룹의 의미
# chown/chgrp 명령
chown 소유자.소유그룹 파일명
chown 소유자 파일명
chgrp 소유그룹 파일명
```

### 링크

- 파일의 링크(Link)에는 하드 링크(Hard Link)와 심볼릭 링크(Symbolic Link 또는 Soft Link) 두 가지가 있다
![Untitled](https://user-images.githubusercontent.com/95058915/206116456-c89cfe71-de4f-4064-a9bd-3a77be93f569.png)
- 하드 링크를 생성하면 “하드 링크파일”만 하나 생성되며 같은 inode1을 사용한다. (명령 : # ln 링크파일대상이름 링크파일이름)
- 심볼릭 링크를 생성하면 새로운 inode2를 만들고, 데이터는 원본 파일을 연결하는 효과 (명령 ln -s 링크대상파일이름 링크파일이름)

## 관리자를 위한 명령어

### 프로그램 설치를 위한 dpkg(Debian Package)

- dpkg(Debian Package)
    - Windows의 “setup.exe”와 비슷한 설치 파일
    - 확장명은 *.deb이며, 이를 패키지라고 부름
- 파일의 의미
    - 패키지이름_버전-개정번호_아키텍처.deb
    - 패키지 이름: 프로그램의 이름
    - 버전 : 대개 3자리 수로 구성. 주버전, 부버전, 패치버전
    - 개정번호 : 문제점을 개선할 때마다 붙여지는 번호
    - 아키텍처 : cpu를 의미 (amd64: 64비트, i386: 32비트, all: 모든cpu)

```bash
# 자주 사용하는 dpkg 명령어 옵션
# 패키지이름_버전-개정번호_아키텍처.deb
# 설치
dpkg -i 패키지파일이름.deb

# 삭제
dpkg -r 패키지이름 # 패키지 이름만 적어야함
dpkg -p 패키지이름 -> 설정파일까지 삭제

# 패키지 조회
dpkg -l 패키지이름 # 설치된 패지키에 대한 정보를 보여줌
dpkg -L 패지키이름 # 패키지가 설치된 파일 목록을 보여줌

# 아직 설치되지 않은 deb 파일 조회
dpkg --info 패키지파일이름.deb # 패지키 파일에 대한 정보를 보여줌

# 파일 다운로드
axel 경로
axel https://www.kernel.org/pub/linux/kernel/v5.x/linux-5.8.1.tar.xz
```

- dpkg 명령의 단점
    - 의존성 문제 : A패키지가 설치되기 위해서 B패키지가 필요할 경우 dpkg명령으로 해결이 까다로움 ⇒ 해결을 위해 apt 등장
    

### 편리한 패키지 설치 apt

- apt 명령
    - dpkg 명령의 패키지 의존성 문제를 완전하게 해결
    - 인터넷을 통하여 필요한 파일을 저장소(Repository)에서 자도으로 모두 다운로드해서 설치하는 방식 (/etc/apt/sources.list)

```bash
# apt 기본적인 사용법

# 기본 설치
apt install 패키지이름
apt -y install 패키지이름 # -y는 모두 yes로 간주하고 설치 진행

# 패키지 목록의 업데이트(/etc/apt/sources.list 변경 후 적용)
apt update

# 삭제
apt remove/purge 패지키이름

# 사용하지 않는 패키지 제거
apt autoremove

# 내려 받은 파일 제거
apt clean
apt autoclean

# 시스템 전체 최신버전으로 업그레이드
apt upgrade

# apt-cache
# 패지키를 설치하지 전에 패키지에 대한 정보나 의존성 문제를 미리 확인

# 패키지 정보 보기
apt-cache show 패키지이름

# 패키지 의존성 확인
apt-cache depends 패키지이름

# 패키지 역의존성 확인
apt-cache rdepends 패키지이름
```

**apt 작동방식 설정 파일**

1. apt install 입력 (apt -y install은 아래의 과정 한번에 진행)
2. /etc/apt/sources.list 파일을 열어서 URL 주소 확인
3. 우분투 패키지 저장소에 설치와 관련된 패키지 목록을 요청
4. 전체 패키지 목록 파일만 다운로드
5. 설치할 패키지와 관련된 패키지 이름을 화면에 출력
6. y를 입력하면 설치에 필요한 패키지 파일 요청
7. 설치할 패키지 파일을 다운로드해서 자동 설치

**우분투 패키지 저장소**

- main : 우분투에서 공식적으로 지원하는 무료 SW
- universe : 우분투에서 지원하지 않는 무료 SW
- restricted : 우분투에서 공식적으로 지원하지 않는 유료(Non-Free) SW
- multiverse : 우분투에서 지원하지 않는 유료 SW

**저장소가 기록된 파일**

- /etc/apt/sources.list 파일
- 형식 : deb 우분투_저장소_URL 버전_코드명 저장소_종류
- 우분투 저장소 모음 사이트 : [https://launchpad.net/ubuntu/+cdmirrors](https://launchpad.net/ubuntu/+cdmirrors)

### 파일의 압축과 묶기

**파일 압축**

- 압축파일 확장명은 xz, bz2, gz, zip, Z 등
- xz나 bz2 압축률이 더 좋다

```bash
# 파일압축 관련 명령

# xz : 확장명 xz로 압축을 하거나 풀어준다.
# (원본파일이 없어지고 새로운 압축파일이 생김), zip만 원본 유지
xz 파일명 # 압축
xz -d 파일명.xz # 압축해제

# bzip : 확장명 bz2로 압축을 하거나 풀어준다.
bzip2 파일명 # 압축
bzip2 -d 파일명.bz2 # 압축해체

# gzip : 확장명 gz으로 압축을 하거나 풀어준다
gzip 파일명 # 압축
gzip -d 파일명.gz # 압축해제

# zip / unzip : 확장명 zip으로 압축하거나 풀어준다.
zip 새로생성될파일이름.zip 압축할파일이름 # 압축
unzip 압축파일이름.zip # 압축해제

```

**파일 묶기**

- 리눅스(유닉스)에서는 파일 압축과 파일 묶기는 원칙적으로 별개의 프로그램으로 수행
- 파일 묶기의 명령어는 tar이며 묶인 파일의 확장명도 tar이다. (압축이 아닌 파일만 묶이는 것)

```bash
#파일 묶기 명령(tar)
# tar : 확장명 tar로 묶음 파일을 만들어 주거나 묶음을 풀어준다
# 동작 : c(묶기), x(풀기), t(경로확인)
# 옵션 : f(파일), v(과정보이기), J(tar+xz), z(tar+gzip), j(tar+bzip2)

tar 옵션 묶음명 디렉토리 # 폴더가 묶임
tar cvf my.tar /etc/systemd/ # 묶기
tar cvfJ my.tar.xz /etc/systemd/ # 묶기 + xz 압축
tar xvf my.tar # tar 풀기
tar xvfJ my.tar.xz /etc/systemd/ # xz 압축해제 tar 풀기
```

**파일 위치 검색**

- find [경로] [옵션] [조건] [action] : 기본 파일 찾기

```bash
# [옵션] -name, -user(소유자), -newer(전, 후), perm(허가권), -size(크기)
# [action] -print(디폴트), exec (외부명령실행)

find /etc -name "*.conf"
find /binm -size +10k -size -100k
find /home -name "*.swp" *exec rm { } \; #swp: 임시파일

# PATH에 설정된 디렉토리만 검색
which 실행파일이름
# 실행 파일, 소스, main페이지 파일까지 검색
whereis 실행파일이름
# 파일 목록 데이터베이스에 검색
locate 파일이름

# find가 가장 많이 쓰임
```

### 시스템 설정

- 다양한 환경 설정 (gnome-control-center) ⇒ 제어판
- 네트워크 설정 (nmtui)
- 방화벽 설정 (ufw, gufw)

### CRON과 AT

**cron**

- 주기적으로 반복되는 일을 자동적으로 실행될 수 있도록 설정
- 관련된 데몬(서비스)은 crond, 관련 파일은 /etc/crontab

```bash
# #!/bin/sh 스크립트 할 셸을 지정하는 선언문

# 현재 서버에 지정된 시간으로 변경
rdate -s time.bora.net

# ctl => control
# 날짜를 수동으로 변경하기 위한 설정
timedatectl set-ntp 0
date 011503002030 # 1월15일03시00분2030년

# cron과 관련된 서비스가 작동되는지 확인
systemctl status cron

# cron과 관련된 서비스가 재시작
systemctl restart cron

# /etc/crontab 형식 
# 분 시 일 월 요일 사용자 실행명령
00 05 1 * * root cp -r /home /backup
```

**at**

- 일회성 작업을 예약

```bash
# 예약
at [시간]
at 3:00am tomorrow ⇒ 내일 새벽 3시
at now + 1 hours ⇒ 1시간 후
at> 프롬프트에 예약 명령어 입력 후 [Enter]
완료되면 [Ctrl] + [D]

# 확인
at -l
# 취소
atrm [작업번호]

```

### 네트워크 관련 필수 개념

**TCP/IP**

- 데이터가 의도된 목적지로 전송할 수 있도록 보장해주는 프로토콜

**호스트 이름과 도메인 이름**

- 호스트 이름은 각각의 컴퓨터에 지정된 이름
- 도메인 이름(도메인 주소)은  웹사이트의 IP주소를 나타내는 이름

**IP 주소**

- 각 컴퓨터의 랜카드에 부여되는 중복되지 않는 주소
- 4바이트로 이루어져 있으며. 각 자리는 0~255까지의 숫자

**네트워크 주소**

- 같은 네트워크에 속해 있는 공통된 주소
- 전체 네트워크에서 작은 네트워크를 식별하는데 사용

**브로드캐스트(Broadcast) 주소**

- 네트워크에 있는 컴퓨터나 장비 모두에게 한 번에 데이터를 전송하는 데 사용하는 전용 IP 주소
- 현재 주소의 제일 끝자리를 255로 바꾼 주소(C클래스)

**게이트웨이(Gateway)**

- 네트워크 간에 데이터를 전송하는 컴퓨터 장비
- 한 네트워크에서 다른 네트워크로 이동하기 위한 통로

**라우터(Router)**

- 패킷(데이터)을 목적지까지 전달하기 위해 다음 네트워크 지점을 결정하는 장치나 컴퓨터 내의 소프트웨어

**클래스(Class)**

- 하나의 IP 주소에서 네트워크 영역(주소)과 호스트 영역(주소)을 구분하는 방법

**넷마스크(Netmask)**

- IP (Internet Protocol) 주소의 클래스와 범위를 정의

**DNS(Domain Name System) 서버(= 네임 서버) 주소**

- URL을 해당 컴퓨터의 IP주소로 변환해주는 서버
- 설정 파일은 /etc/resolv.conf

**DHCP(Dynamic Host Configuration Protocol) 서버**

- 호스트의 IP주소와 각종 TCP/IP 프로토콜의 기본 설정을 클라이언트에게 자동적으로 제공해주는 프로토콜이다.
- 즉, 호스트(서버)에서 보유하고있는 IP를 자동할당하고 분배한다.

**랜 카드**

- 한 네트워크 안에서 컴퓨터 간 신호를 주고받는 데 쓰이는 하드웨어

**리눅스에서의 네트워크 장치 이름**

- 우분투는 랜카드를 ens32 또는 ens33으로 할당
- ipconfig ens32 or ens33 ⇒ 네트워크 설정 정보를 출력
- ifdown ens32 or ens33 ⇒ 네트워크 장치를 정지
- ifup ens32 or ens33 ⇒ 네트워크 장치를 가동

### 중요한 네트워크 관련 명령어

**nm-connection-editor 또는 nmtui**

- 네트워크와 관련된 대부분의 작업을 이 명령어에서 수행
- 자동 IP주소 또는 고정 IP주소 사용 결정
- IP주소, 서브넷 마스크, 게이트웨이 정보 입력
- DNS 정보 입력
- 네트워크 카드 드라이버 설정
- 네트워크 장치(ens32)의 설정

**systemctl [start/stop/restart/status] networking**

- 네트워크의 설정을 변경한 후에 변경된 내용을 시스템에 적용시키는 명령어

**ifconfig [장치이름]**

- 장치의 ip주소 설정 정보를 출력

**nslookup**

- DNS 서버의 작동을 테스트하는 명령어

**ping [IP주소 또는 URL]**

- 컴퓨터가 네트워크 상에서 응답하는지를 테스트하는 간편한 명령어

### 네트워크 설정과 관련된 주요 파일

**네트워크 기본 정보가 설정된 파일**

- X윈도우 모드
    - ‘/etc/NetworkManager/system-connections/유선 연결 1’ 파일
- 텍스트 모드
    - ‘/etc/netplan/*.yaml’ 파일

**DNS 서버의 정보 및 호스트 이름이 들어있는 파일**

- /etc/resolv.conf

**현재 컴퓨터의 호스트 이름 및 FQDN이 들어있는 파일**

- /etc/hosts

### 파이프, 필터, 리디렉션

**파이트(pipe)**

- 두 개의 프로그램을 연결해주는 연결통로
- “ | ” 문자를 사용한다
- 예) ls -l /etc | more

**필터(filter)**

- 필요한 것만 걸러주는 명령어
- grep, tail, wc, sort, awk, sed 등
- 주로 파이프와 같이 사용한다.
- 예) ps -ef | grep bash ⇒ 현재 작동되는 프로세서 중에서 bash가 들어가는 글자만 확인

**리다이렉션(redirection)**

- 표준 입출력의 방향을 바꿔준다
- 예) ls -l > list.txt ⇒ 주로 사용
soft < list.txt > out.txt

### 프로세스, 데몬

하드디스크에 저장된 프로그램(실행코드)이 메모리에 로딩되어 활성화된 것

**포그라운드 프로세스(Froeground Process)**

- 실행하면 화면에 나타나서 사용자와 상호작용을 하는 프로세스
- 대부분의 응용프로그램

**백그라운드 프로세스(Background Process)**

- 실행은 되었지만 화면에는 나타나지 않고 실행되는 프로세스
- 백신 프로그램, 서버 데몬 등

**프로세스 번호**

- 각각의 프로세스에 할당된 고유번호

**작업 번호**

- 현재 실행되고 있는 백그라운드 프로세스의 순차번호

**부모 프로세스와 자식 프로세스**

- 모든 프로세스는 부모 프로세스를 가지고 있다
- 부모 프로세스를 kill 하면, 자식 프로세스도 자동으로 kill 된다

```bash
# 테스트용 명령

#yes라는 명령을 계속 발생시킴
yes
# /dev/null로 yes를 무한히 발생시킴
yes > /dev/null 

# 프로세스 관련 명령

# 포그라우드 명령을 백그라운드로 이동
bg

# 백그라운드의 작업 목록 출력
jobs

# 백그라운드의 작업을 포그라운드로 이동
fg [작업번호]

# 작업을 정지
[Ctrl] + Z
# 작업을 종료
[Ctrl] + C

gedit # 포그라운드로 실행, 실행한 터미널 사용 불가
gedit & # 백그라운드로 실행, 실행한 터미널 사용 가능

# ps : 현재 프로세스의 상태를 확인하는 명령어
ps -ef | grep [프로세스 이름] # 주로 사용

# kill : 프로세스를 강제로 종료하는 명령어
kill -9 [프로세스 번호] # -9가 제일 강력함

# pstree : 부모 프로세스와 자식 프로세스의 관계를 트리형태로 보여줌
pstree
```

### 서비스와 소켓

**서비스**

- 시스템과 독자적으로 구동하며 제공되는 프로세스이며, 웹 서버, DB 서버, FTP 서버 등이 있다.
- 실행 및 종료는 대게 ‘systemctl start/stop/restart [서비스 이름]’ 으로 사용된다.
- 서비스의 실행 스크립트 파일은 /lib/systemd/system/ 디렉토리에 ‘서비스이름.service’ 라는 이름으로 확인할 수 있다. 
예를 들면 cron 서비스는 cron.service라는 이름의 파일로 존재한다.
- 부팅과 동시에 서비스의 자동 실행 여부를 지정할 수 있는데, 터미널에서 systemctl list-unit-files 명령을 실행하면 현재 사용(enabled)과 사용 안함(disabled)을 확인할 수 있다.

**소켓**

- 서로 멀리 떨어져있는 host(컴퓨터)간에 데이터를 주고 받을 수 잇도록 프로그램으로 구현한 것 이다.(양방향 통신의 접속점(end point))
- 서비스는 항상 가동되지만, 소켓은 외부에서 특정 서비스를 요청할 경우 systemd가 구동시킨다. 그리고 요청이 끝나면 소켓도 종료된다.
- 소켓으로 설정된 서비스를 요청할 때는 처음 연결되는 시간이 서비스에 비교했을 때 약간 더 느릴 수 있다.
이유는 systemd가 서비스를 새로 구동하는 데 시간이 소요되기 때문이다.
소켓의 대표적인 예) 텔넷 서버
- 소켓과 관련된 스크립트 파일은 /lib/systemd/system/ 디렉토리에 소켓이름.socket라는 이름으로 존재한다.

### 응급 복구

시스템이 부팅이 되지 않을 경우에 수행

server 실행전에 esc를 빠르게 연속적으로 누른 후 설정으로 이동
<br/>
![Untitled (1)](https://user-images.githubusercontent.com/95058915/206117149-eb21d80c-0826-4eca-955b-87538d1b766f.png)
<br/>
읽기 쓰기로 변경 ⇒ mount -o remount,rw / 


### GRUB 부트로더

**GRUB 부트로더의 특징**

- 부트 정보를 사용자가 임의로 변경해 부팅할 수 가 있다. 즉, 부트 정보가 올바르지 않더라도 수정하여 부팅할 수 있다.
- 다른 여러가지 운영체제와 멀티 부팅을 할 수 있다.
- 대화영 설정을 제공하므로 커널의 경로와 파일 이름만 알면 부팅이 가능하다

**GRUB2의 장점**

- 셸 스크립트를 지원함으로써 조건식과 함수를 사용할 수 있다.
- 동적 모듈을 로드할 수 있다.
- 그래픽 부트 메뉴를 지원하며, 부트 스플래시(boot splash) 성능이 개선되었다.
- ISO 이미지를 이용해서 바로 부팅할 수 있다.

**GRUB2 설정 방법**

- /boot/grub/grub.cfg 설정파일 (직접 변경하면 안됨)
- /etc/default/grub 파일과 /etc/grub.d/ 디렉토리의 파일을 수정한 후에 ‘update-grub’ 명령어를 실행해서 설정한다.(/boot/grub/grub.cfg 설정파일이 업데이트 됨)

### 모듈의 개념과 커널 컴파일의 중요성

**모듈** : 필요할 때마다 호출하여 사용되는 코드
<br/>
![Untitled (2)](https://user-images.githubusercontent.com/95058915/206117704-3f9c9704-5d0a-43f1-a8ed-d29582882ad5.png)



### 커널 컴파일

- [https://www.kernel.org/](https://www.kernel.org/) 에서 최신 커널 확인 가능

```bash
# 컴파일을 위한 패키지
apt -y install qt5-default libssl-dev make gcc g++ flex bison

# **커널 컴파일 순서**

# 1. 현 커널 버전 확인
uname -r

# 2. 커널 소스 다운로드
/use/src 폴더에 다운로드

# 3. 커널 소스 압축 풀기
tar xfz linux-5.8-rc2.tar.xz
cd linux-5.8-rc2

# 4. 커널 설정 초기화
make mrproper

# 5. 커널 환경설정
make xconfig

# 6. 이전 정보 삭제
make clean

# 7. 커널 컴파일 및 설치
make
make modules_install
make install
ls -l /boot

# 8. 부트로더 확인
cat /boot/grub/brub.cfg
```

<br/>
<br/>

### 참조
- 이것이 우분투 리눅스다(개정판) - 
