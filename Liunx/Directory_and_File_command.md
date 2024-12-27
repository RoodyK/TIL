# 디렉토리와 파일 관련 명령어

### help, man 
도움말 관련 명령어

```bash
ls --help
man ls
```
<br/>
<br/>

### . : 파일 디렉토리 숨김

```bash
touch .temp.txt # 숨김 파일이 된다.
```
<br/>
<br/>

### ls : 파일 디렉토리 목록 보기

```bash
ls -al # 디렉토리의 모든 파일 및 상세 속성 표시

# 디렉토리 내의 모든 파일을 출력, 숨김 파일 포함
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
# 사용자의 권한을 출력하지 않음
ls -g
```
<br/>
<br/>

### cp : 파일 복사

```bash
cp 원본파일명 복사할파일명

# 원본 파일의 속성, 링크 정보들을 유지하면서 복사
# 디렉토리의 경우 하위 디렉토리와 포함된 모든 파일 복사
cp -a
# 복사할 대상이 있으면 기본 파일을 백업한 뒤 복사
cp -b
# 복사할 원본이 심볼릭 링크이면 심볼릭 파일 자체를 복사
cp -d
# 복사할 파일이 존재해도 강제로 복사
cp -f
# 하디 링크 형식으로 복사
cp -l
# 원본 파일의 소유, 그룹, 권한 등을 보존한 채 복사
cp -p
# 복사 과정을 보임
cp -v
# 원본이 파일이면 그냥 복사하고 디렉토리면 디렉토리를 복사
cp -r
# 디렉토리를 복사할 때 하위 디렉토리에 있는 모든 파일까지 복사
cp -R
# 심볼릭 링크 형식으로 복사하는데 원본파일의 절대경로를 지정해야 한다.
cp -s
# 복사하려는 파일이 대상 파일보다 새롭거나, 대상 파일이 없을 경우에만 복사한다.
cp -u
```
<br/>
<br/>

### rm : 파일과 디렉토리 제거

```bash
rm 파일명

# 비어있는 디렉토리 삭제
rm -d
# 삭제 여부를 확인하지 않고 강제 삭제
rm -f
# 디렉토리와 그 안의 모든 파일을 삭제
rm -r

# -rf: 파일 삭제, 디렉토리 및 그 안의 모든 내용을 강제로 삭제
rm -rf directory_name
```
<br/>
<br/>

### cd : 디렉토리 이동

```bash
cd 경로

# 상대경로
cd ./경로
# 절대경로
cd /
```
<br/>
<br/>

### mkdir : 디렉토리 생성

```bash
mkdir 디렉토리명

# 퍼미션을 지정해서 생성하는데 디폴트 퍼미션은 755(rexr-xr-x)이다.
mkdir -m
# 지정한 상/하위 디렉토리까지 한꺼번에 생성하기
mkdir -p
```
<br/>
<br/>

### rmdir : 디렉토리 제거

```bash
rmdir 디렉토리명

# 지정한 상/하위 디렉토리까지 한꺼번에 삭제
rmdir -p
```
<br/>
<br/>

### mv : 파일, 디렉토리 옮기기

```bash
mv 디렉토리명 옮길디렉토리명
mv /경로/디렉토리명 /경로/디렉토리명

# 이동할 때 대상파일의 이름이 있을 때 백업 파일을 만든 뒤 복사
mv -b
# 대상 파일이 있어도 강제로 이동
mv -f
# 이동할 때 덮어 쓸 여부 확인
mv -i
# 대상보다 원본이 최신파일일 때 갱신
mv -u
# 이동 과정 보여주기
mv -v
```
<br/>
<br/>

### `>`, `<`, `>>`, `<<` : 리다이렉션

```bash
# >: 명령어 출력을 파일로 리다이렉트하고, 기존 파일을 덮어쓴다.
# >>: 명령어 출력을 파일로 리다이렉트하며, 기존 파일에 내용을 추가한다.
# <: 파일의 내용을 명령어의 입력으로 사용한다.
# <<: 여러 줄의 입력을 명령어에 전달하며, 종료 문자열을 사용하여 입력을 종료한다.

# "Hello, World"를 output.txt 파일에 저장한다. 만약 output.txt가 이미 존재한다면, 기존 파일을 덮어쓴다.
echo "Hello, World" > output.txt

# output.txt 파일에 "Hello, World"을 추가한다. 기존 파일을 덮어쓰지 않고 뒤에 내용이 추가된다. 파일이 존재하지 않으면 새로 생성된다. 
echo "Hello, World" >> output.txt

# input.txt 파일의 내용을 읽어들이고, 그 데이터를 sort 명령어에 입력으로 전달하여 정렬된 결과를 출력한다.
sort < input.txt

# # cat 명령어에 "Hello, World"와 "This is a test"를 입력으로 제공한다. EOF는 종료 문자열로, 입력이 종료될 때까지의 모든 데이터를 cat 명령어에 전달한다.
cat << EOF
Hello, World
This is a test
EOF
```
<br/>
<br/>

### 링크 : 복잡한 파일명이나 경로를 단순화

1) 심볼릭 링크(소프트 링크): 심볼릭 링크는 Windows에서의 단축키와 같은 개념으로 원본 파일을 지시하는 파일이라고 보면 된다. 바로가기 아이콘과 개념이 비슷하다.

2) 하드링크는 복사와 유사한 개념으로 동일한 inode를 사용하므로 동일한 파티션 내에서만 생성이 가능하고, 다른 파티션에 있는 파일에 대해서는 하드링크를 생성할 수 없다.

```bash
# 하드 링크
ln -s 링크대상파일이름 링크파일이름

# 심볼릭 링크
ln 링크파일대상이름 링크파일이름
```
<br/>
<br/>

### cat : 파일의 내용을 출력하거나 생성

```bash
cat 파일명

# 파일의 내용을 결합하여 새로운 파일에 저장한다.
cat file1.txt file2.txt > new.txt

# 파일의 내용을 번호와 함께 출력한다.
cat -n filename.txt  # 모든 줄에 번호를 매긴다.
cat -b filename.txt  # 비어 있지 않은 줄에만 번호를 매긴다.

# 공백을 포함해서 줄 번호를 매긴다.
cat -n
# 공백을 포함하지 않고 번호를 매긴다.
cat -b
```
<br/>
<br/>

### tr : 입력된 텍스트에서 문자를 변환하거나 삭제하는데 사용

```bash
# 기본적인 사용법: 특정 문자를 다른 문자로 변환
echo "hello world" | tr 'h' 'H'
# 결과: Hello world

# 여러 문자를 동시에 변환
echo "hello world" | tr 'hw' 'HW'
# 결과: Hello World

# 대소문자 변환
echo "hello world" | tr 'a-z' 'A-Z'
# 결과: HELLO WORLD

# -d 옵션: 특정 문자를 삭제
echo "hello world" | tr -d 'l'
# 결과: heo word

# -s 옵션: 연속된 동일 문자를 하나로 축소
echo "hellooo    world" | tr -s 'o '
# 결과: helo world

# -c 옵션: 특정 문자 집합의 보충 집합을 지정
# 여기서는 a-z를 제외한 모든 문자를 *로 변환
echo "hello world 123" | tr -c 'a-z' '*'
# 결과: hello*world***

# -t 옵션: 출력 집합의 길이를 입력 집합의 길이로 트림
echo "hello world" | tr -t 'a-z' 'A-Z'
# 결과: HELLO WORLD

# -d와 -s를 함께 사용: 특정 문자를 삭제하고 연속된 문자를 축소
echo "hellooo world" | tr -d 'l' | tr -s 'o'
# 결과: heoo word
```

<br/>
<br/>

### clear : 화면 지우기

```bash
clear
```
<br/>
<br/>

### file : 파일의 종류 보기

```bash
file 파일명
```
<br/>
<br/>

### pwd : 현재 작업하고 있는 디렉토리 보기

```bash
# 현재 작업 중인 디렉토리의 절대 경로를 출력한다.
pwd

# -L (--logical): 현재 경로를 논리적 경로로 출력한다. 심볼릭 링크를 따라간다.
# 심볼릭 링크를 통해 이동한 경우에도 심볼릭 링크의 경로를 출력한다.
pwd -L

# -P (--physical): 현재 경로를 물리적 경로로 출력한다. 심볼릭 링크를 실제 경로로 해석한다.
# 심볼릭 링크를 따라가지 않고 실제 경로를 출력한다.
pwd -P
```
<br/>
<br/>

### date : 날짜 보기

```bash
# 기본 날짜 출력
# 현재 날짜와 시간을 기본 포맷으로 출력한다.
date

# +는 포맷 옵션을 시작한다는 의미이다.
# 특정 포맷으로 출력
# 현재 날짜를 '연도-월-일' 형식으로 출력한다. 예: 2023-10-04
date '+%Y-%m-%d'

# 날짜와 시간 출력
# 현재 날짜와 시간을 '연도-월-일 시:분:초' 형식으로 출력한다. 예: 2023-10-04 14:30:45
date '+%Y-%m-%d %H:%M:%S'

# 요일과 월 이름 출력
# 현재 날짜를 '요일, 월 이름 일, 연도' 형식으로 출력한다. 예: Wednesday, October 04, 2023
date '+%A, %B %d, %Y'

# UTC 시간으로 출력
# UTC(협정 세계시)로 현재 날짜와 시간을 출력한다.
date -u

# 특정 날짜 출력
# 2023년 10월 1일의 요일을 출력한다. 예: Sunday
date -d '2023-10-01' '+%A'

# 현재 시간의 타임스탬프 출력
# 현재 시간을 Unix 타임스탬프 형식으로 출력한다. (1970년 1월 1일 이후의 초)
date +%s
```
<br/>
<br/>

### type : 명령어의 종류 보기

```bash
type 명령어
type pwd
```
<br/>
<br/>

### tail : 파일의 마지막 행을 기준으로 지정한 행까지의 파일 내용 일부를 출력해주는 명령어

```bash
tail [옵션] [파일명]

# 톰캣 실행 시 쌓이는 로그를 실시간으로 확인
tail -f catalina.log
# 명령 종료 ctrl + c

# -n : 출력할 줄 수를 지정한다. 예를 들어, -n 20은 마지막 20줄을 출력한다.
tail -n 20 filename

# -f : 파일의 끝에 새로운 내용이 추가될 때 실시간으로 출력한다. 로그 파일을 모니터링할 때 주로 사용된다.
tail -f filename

# -c : 출력할 바이트 수를 지정한다. 예를 들어, -c 100은 마지막 100바이트를 출력한다.
tail -c 100 filename

# -q : 여러 파일을 함께 출력할 때 파일 이름을 출력하지 않는다.
tail -q file1 file2

# -s : -f 옵션과 함께 사용하여 주기적으로 파일을 확인하는 시간을 지정한다. 단위는 초이다.
tail -f -s 5 filename
```

<br/>
<br/>

### lsof : 실행중인 파일과 목록 보기

```bash
lsof

# 파일을 선택하는데 AND 연산으로 대상을 출력
lsof -a
# 지정한 COMMAND 필드만 출력
lsof -c
# 지정한 구분자로 필드를 구분하여 출력
lsof -F
# 현재 사용되는 소켓 정보 출력
lsof -I
# 로그인 사용자명 대신 UID 출력
lsof -l
# 호스트명 대신 IP 주소 출력
lsof -n
# 포트 서비스명 대신에 포트 번호 출력
lsof -p
```
<br/>
<br/>

### touch : 빈파일 생성과 파일의 접속속성 변경

```bash
touch 파일명 ...
touch abc.txt def.txt
```
<br/>
<br/>

### find : 파일을 찾거나 찾은 파일로 작업하게 해줌

```bash
# 명령어 뒤에 2>/dev/null을 붙여서 화면에 에러가 표시되지 않게 하기
find / =user root 2>/dev/null

# root 디렉토리부터(/) 빈(-size 0k) 파일(-type f)을 찾아서 -exec 뒤의 { } 에
# 인자로 넣어서 모두 삭제(rm -rf)를 실행(-exec)하기
find / -type f -size 0k -exec rm -rf {} \;
```
<br/>
<br/>

### | : 명령어 연결시키기(파이프라인)
```bash
ps -ef | grep bash
```
<br/>
<br/>

### wd : 파일의 정보 보기

```bash
wc 파일명

# 문자수
wc -c
# 줄 수
wc -l
# 단어 수
wc -w
```
<br/>
<br/>

### stat : 파일에 대한 부분 정보 보기

```bash
stat 파일명
```
<br/>
<br/>

### history : 실행했던 이벤트와 명령어를 보기

```bash
history

# 히스토리 사용했던 명령어 초기화
history -c
```
<br/>
<br/>

### grep : 패턴을 이용하여 검색

```bash
# 일치되는 내용이 없는 줄의 내용 표시
grep -v 
# 일치되는 내용이 있는 줄 수 표시
grep -c 
# 일치되는 내용을 줄 번호화 함께 표시
grep -n 
# 대소문자를 구별하지 않고 해당되는 내용이 있는 줄 표시
grep -i 
# 정규표현식 사용
grep -e 
# 찾으려는 문장이나 표현이 있는 파일 지정
grep -f
# 반복적으로 찾음
grep -r
```
<br/>
<br/>

## chown : 파일 시스템 명령어

```bash
chown 소유자.소유그룹 파일명
chown 소유자 파일명
chown reffy:reffy
```
<br/>
<br/>

### chmod : 파일 허가권(권한) 변경

```bash
chmod 777 파일명 # 읽기 쓰기 실행 모두 허용
chmod +x 파일명
```
<br/>
<br/>

### 파일 압축 및 묶기
**파일 압축**은 파일의 크기를 줄이는 과정이다.
<br/>

**파일 묶기**는 여러 개의 파일을 하나의 파일로 묶는 과정이다.
<br/>

**파일 묶기와 압축을 함께 사용하는 이유**
- 여러 개의 파일을 하나의 파일로 묶으면, 파일을 이동하거나 백업할 때 관리가 쉽다.
- 여러 파일을 하나로 만들면, 네트워크를 통해 전송할 때 소요시간을 줄일 수 있다.
- 각각의 파일을 개별적으로 압축하는 것보다 묶어서 압축할 때 더 나은 압축율을 얻을 수 있다.

<br/>

**압축 방식**
- gzip: 빠른 압축 및 해제를 제공하며, 상대적으로 낮은 압축률을 가진다.
- bzip2: gzip보다 느리지만 더 높은 압축률을 제공하여 파일 크기를 줄이는 데 효과적이다.
- xz: 높은 압축률을 지니지만, 압축 및 해제 속도가 느린 방식으로, 대용량 파일에 적합하다.
- zip: 여러 파일을 하나의 아카이브로 묶고 압축하는 방식으로, 압축률은 중간 정도이며, 파일의 메타데이터를 보존한다.

<br/>

**차이점 및 압축률**
- 속도: gzip > zip > bzip2 > xz
- 압축률: xz > bzip2 > gzip ≈ zip

<br/>

```bash
# tar 명령어를 사용하여 파일 묶기
# -x: 아카이브에서 파일 추출 (extract)
# -c: 새로운 아카이브 생성 (create)
# -f: 아카이브 파일 이름 지정
# -v: 진행 상황을 출력 (verbose)
# -z: gzip으로 압축
# -j: bzip2로 압축
# -J: xz로 압축
# -t: 아카이브 내용 목록 출력

# 파일 묶기 (압축 없음)
tar -cvf archive.tar file1.txt file2.txt

# 디렉토리 묶기 (압축 없음)
tar -cvf archive.tar directory_name/

# 파일 묶기 (gzip 압축)
tar -czvf archive.tar.gz file1.txt file2.txt

# 파일 묶기 (bzip2 압축)
tar -cjvf archive.tar.bz2 file1.txt file2.txt

# 파일 묶기 (xz 압축)
tar -cJvf archive.tar.xz file1.txt file2.txt

# 아카이브 내용 목록 출력
tar -tvf archive.tar

# gzip으로 압축된 아카이브에서 파일 추출
tar -xzvf archive.tar.gz

# bzip2로 압축된 아카이브에서 파일 추출
tar -xjvf archive.tar.bz2

# xz로 압축된 아카이브에서 파일 추출
tar -xJvf archive.tar.xz
```

<br/>
<br/>
<br/>
