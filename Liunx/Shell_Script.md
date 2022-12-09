# 셸 스크립트(Shell Script)

셸(Shell) : 명령을 해석 후 실행하여 커널에 전달하는 역할을 한다. 커널(Kernel)과 사용자간의 다리역할을 하며, 용도에 따라 다양한 셸이 존재한다.

## 우분투의 bash 셸

\- 기본 셸은  **Bash**이다.

**Bash 셸의 특징**  

\-  Alias 기능(명령어 단축 기능)
\-  History 기능(위/아래 화살표키)
\-  연산 기능
\-  Job Control 기능
\-  자동 이름 완성 기능(탭 키)
\-  프롬프트 제어 기능
\-  명령 편집 기능

**셸의 명령문 처리 방법**  

\-  (프롬프트) 명령어 [옵션…] [인자…]
\-  예) # rm -rf /mydir

## 환경 변수

\- echo : 화면 출력을 담당하는 명령어  
\- “echo $환경변수이름” 으로 확인 가능  
\- “export 환경변수=값” 으로 환경 변수의 값을 변경  

**주요 환경변수**

| 환경 변수 | 설명 |
| --- | --- |
| HOME | 현재 사용자의 홈 디렉토리 |
| PATH | 실행 파일을 찾는 디렉토리 경로 |
| LANG | 기본 지원되는 언어 |
| PWD | 사용자의 현재 작업 디렉토리 |
| TERM | 로그인 터미널 타입 |
| SHELL | 로그인해서 사용하는 셸 |
| USER | 현재 사용자의 이름 |
| DISPLAY | X 디스플레이 이름 |
| COLUMNS | 터미널의 컬럼 수 |
| LINES | 현재 터미널의 라인 수 |
| PS1 | 1차 명령 프롬프트 변수 |
| PS2 | 2차 명령 프롬프트(대개는 ‘>’) |
| BASH | bash 셸의 경로 |
| BASH_VERSION | bash 버전 |
| HISTFILE | 히스토리 파일의 경로 |
| HISTSIZE | 히스토리 파일에 저장되는 개수 |
| HOSTNAME | 호스트의 이름 |
| USERNAME | 현재 사용자의 이름 |
| LOGNAME | 로그인 이름 |
| LS_COLORS | ls 명령자의 확장자 색상 옵션 |
| MAL | 메일을 보관하는 경로 |
| OSTYPE | 운영체제 타입 |

## 셸 스크립트 프로그래밍

\- C언어와 유사하게 프로그래밍 가능하다.  
\- 변수, 반복문, 제어문 등의 사용이 가능하다.  
\- 별도로 컴파일 하지 않고 텍스트 파일 형태로 바로 실행한다.  
\- vi, vim, gedit, nano 에디터로 작성이 가능하다.  
\- 리눅스의 많은 부분이 셸 스크립트로 작성되어 있다.  

### 셸 스크립트의 작성과 실행

\- 셸 스크립트 파일의 확장명은 되도록 *.sh로 해주는 것이 좋다.
\- 셸 스크립트 파일을 /usr/local/bin/ 디렉토리에 복사하고, 속성(chmod)을 755로 변경해주면 모든 사용자가 스크립트를 사용할 수 있다.(이 작업은 보안상 root만 실행)  

**예제 문서 ⇒ name.sh**

```bash
#!/bin/bash
# 셸 스크립트 문서임을 지정해주는 행

echo "사용자 이름: " $USER
echo "홈 디렉토리: " $HOME
exit 0 # 정상적으로 끝났다는 처리
```

**실행**

```bash
# sh문서 실행
sh name.sh

# 다른 방법

# 실행속성 추가
chmod +x name.sh
# 현재 디렉토리에 있는 name.sh 실행
./name.sh
```

## 변수의 기본

\- 변수를 사용하기 전에 미리 선언하지 않으며, 변수에 처음 값이 할당되면서 자동으로 변수가 생성된다.
\- 모든 변수는 문자열(String)로 취급된다.
\- 변수 이름은 대소문자를 구분한다.
\- 변수를 대입할 때 ‘=’ 좌우에는 공백이 없어야 한다.
\- 변수의 값에 공백이 있으면 “”로 묶어서 사용해야 한다.
\- ‘$’ 문자가 들어간 글자를 출력하려면 ‘’(작은 따옴표)로 묶어주던가 앞에 역슬래쉬(\)를 붙여준다.  
\- 변수의 값을 사용하려면 $value, ${value} 로 사용한다
\- 변수의 삭제는 unset var 명령어를 사용한다.

```bash
# 변수 설정
testval=helloWorld
# testval = hello => = 좌우에 공백이 없어야 함

# 출력
echo $testval
echo ${testval}
echo "${testval}"
# 문자 그대로 출력
echo \$testval

#결과
helloWorld
helloWorld
helloWorld
$testval

# 변수의 값에 공백이 있을 시 ""로 묶어서 처리
testvalue="hello world"

# 값 입력하기
read myvar
```

## 숫자 계산

\- 변수에 대입된 값은 모두 문자열로 취급한다.
\- 변수에 들어 있는 값을 숫자로 해서 +, -, *, / 등의 연산을 하려면 expr을 사용한다.(expr도 명령어이기 때문에 연산자와 피연산자는 (space)로 구분되어야 합니다.)
\- expr을 사용할 때 백틱(`)을 사용한다
\- $(())을 사용해서 연산을 처리할 수 도 있다.
\- 수식에 괄호 또는 곱하기(*)는 그 앞에 꼭 역슬래쉬(\)를 붙인다.

```bash
#!/bin/bash
num1=100
num2=$num1 + 200 # 문자가 입력됨
echo $num2 # 문자 출력
num3=`expr $num1 + 200`
echo $num3
num4 = `expr \( $num1 + 200 \) 10 \* 2`
echo $num4
exit 0
```

## 파라미터 변수

\- 파라미터 변수는 $0, $1, $2…의 형태를 가진다.
\- 전체 파라미터는 $*로 표현한다.

**파라미터 변수 테스트 파일 ⇒ parameter.sh**

```bash
#!/bin/bash
echo "실행파일 이름은 <$0> 이다"
echo "첫 번째 파라미터는 <$1> 이고, 두 번째 파라미터는 <$2> 이다"
echo "전체 파라미터는 <$*> 이다"
exit 0
```

**파라미터 변수 테스트**

```bash
sh [parameter.sh](http://parameter.sh) 값1 값2 값3
```

## 기본 if문

\- “[ 조건 ]” 사이의 각 단어에는 공백이 있어야 한다.

**조건문 형식**

```bash
if [ 조건 ]
then
	참일 경우 실행
elif [ 조건 ]
else
	거짓인 경우 실행
fi
```

**if문 테스트 파일⇒ if1.sh**

```bash
#!/bin/bash

if [ $1 -eq 1 ]
then
	echo "1 입니다."
elif [ $1 -eq 2 ]
then
	echo "2 입니다."
else
	echo "거짓 입니다."
fi
exit 0
```

**if문 테스트**

```bash
sh **if1**.sh 2

# 결과
2 입니다.
```

### **조건문 비교 연산자**

\- **문자열 비교**

| 문자열 비교 | 결과 |
| --- | --- |
| “문자열1” = “문자열2” | 두 문자열이 같으면 참 |
| “문자열1” != “문자열2” | 두 문자열이 같지 않으면 참 |
| -n “문자열” | 두 문자열이 NULL이 아니면 참 |
| -z “문자열” | 문자열이 NULL이면 참 |

\- **산술 비교**

| 산술 비교 | 결과 |
| --- | --- |
| 수식1 -eq 수식2 | 두 수식(또는 변수)이 같으면 참 |
| 수식1 -ne 수식2 | 두 수식(또는 변수)이 같지 않으면 참 |
| 수식1 -gt 수식2 | 수식1이 크다면 참 |
| 수식1 -ge 수식2 | 수식1이 크거나 같으면 참 |
| 수식1 -lt 수식2 | 수식1이 작으면 참 |
| 수식1 -le 수식2 | 수식1이 작거나 같으면 참 |
| !수식 | 수식이 거짓이라면 참 |

### AND, OR 관계 연산자

\- and는 ‘-a’ 또는 ‘&&’ 를 사용한다.

\- or는 ‘-o’ 또는 ‘||’ 를 사용한다.

## 파일과 관련된 조건

| 파일 조건 | 결과 |
| --- | --- |
| -d 파일이름 | 파일이 디렉토리면 참 |
| -e 파일이름 | 파일이 존재하면 참 |
| -f 파일이름 | 파일이 일반 파일이면 참 |
| -g 파일이름 | 파일에 set-group-id가 설정되면 참 |
| -r 파일이름 | 파일이 읽기 가능이면 참 |
| -s 파일이름 | 파일 크기가 0이 아니면 참 |
| -u 파일이름 | 파일에 [ set-user-id가 설정되면 참 |
| -w 파일이름 | 파일이 쓰기 가능 상태이면 참 |
| -x 파일이름 | 파일이 실행 가능 상태이면 참 |

**파일관련 조건문 테스트**

```bash
#!/bin/bash

fname=/lib/systemd/system/cron.service

if [ -f $fname ]
then
	head -5 $fname
else
	echo "cron 서버가 설치되지 않았습니다."
fi

exit 0
```

**파일 조건문 테스트 실행**

```bash
sh if2.sh

# 결과
[Unit]
Description=Regular background program processing daemon
Documentation=man:cron(8)
After=remote-fs.target nss-user-lookup.target
```

### 입력을 받는 if문 예제

**입력을 받는 조건문 테스트 파일 ⇒ if3.sh**

```bash
#!/bin/bash

echo "값을 입력하세요.";
read num
if [ $num -eq 1 ]
then
	echo "1 입니다."
elif [ $num -eq 2 ]
then
	echo "2 입니다."
else
	echo "조건 이외의 값 입니다."
fi
exit 0
```

**테스트 결과**

```bash
sh if3.sh

# 결과
값을 입력하세요.
2
2 입니다.
```

## CASE 문

\- 다중 분기문에서 CASE문을 사용할 수 도 있다.

**CASE 문 테스트 파일 ⇒ case1.sh**

```bash
#!/bin/bash

case "$1" in
	start)
		echo "시작~";;
	stop)
		echo "중지~";;
	restart)
		echo "다시 시작~";;
	*)
		echo "DEFAULT 값~";;
esac

exit 0
```

**CASE문 테스트 실행**

```bash
sh case1.sh restart

# 결과
"다시 시작~"
```

## 반복문 for ~ in문

**형식**

```bash
# 방법1
for 변수 in 값1 값2 값3 ...
do
	반복할 문장
done

# 방법2
for ((변수=0; 변수<10; 변수++));
do
	반복할 문장
done
```

**테스트 파일1 ⇒ for1.sh**

```bash
#!/bin/bash

total=0
for i in 1 2 3 4 5 6 7 8 9 10
do
	total=`expr $total + $i`
done

echo "1부터 10까지의 합: "$total

exit 0
```

**반복문 테스트 실행**

```bash
sh for1.sh

# 결과
1부터 10까지의 합: 55
```

**테스트 파일2 ⇒ for2.sh** 

\-  현재 디렉토리에 있는 셸 스크립트 파일(*.sh)의 파일명과 앞 3줄을 출력하는 프로그램

```bash
#!/bin/bash

for fname in $(ls *.sh)
do
	echo "--------$fname--------"
	head -3 $fname
done
exit 0
```

**테스트 파일2 실행**

```bash
sh for2.sh

# 결과
--------name.sh--------
#!/bin/sh
echo "사용자 이름: " $USER
echo "홈 디렉토리: " $HOME
--------parameter.sh--------
#!/bin/sh
echo "실행파일 이름은 <$0> 이다"
echo "첫 번째 파라미터는 <$1> 이고, 두 번째 파라미터는 <$2> 이다"
...
```

**테스트 파일3 - 변수, 배열 사용 ⇒ for3.sh**

```bash
#!/bin/bash
 
echo "실행1";
data="1 3 5 7 9"
for var in $data
do
  echo $var
done

echo "실행2";
array=(1 3 5 7 9)
for var2 in "${array[@]}"
do
  echo $var2
done

exit 0
```

**테스트 파일3 실행**

```bash
sh for3.sh

# 결과
실행1
1
3
5
7
9
실행2
1
3
5
7
9
```

**테스트 파일4 - 순차적 증가 ⇒ for4.sh**

```bash
#!/bin/bash

echo "첫 번째 순차적 증가";
for var in `seq 1 10`
do
  echo $var
done

echo "두 번째 순차적 증가";
for num in `seq 1 3 10`
do
  echo $num 
done

echo "세 번째 순차적 증가";
for var in {1..10}
do
  echo $var
done

echo "네 번째 순차적 증가";
for num in {1..10..3}
do
  echo $num 
done

exit 0
```

**테스트 파일4 실행**

```bash
sh for4.sh

# 결과
첫 번째 순차적 증가
1
2
3
4
5
6
7
8
9
10
두 번째 순차적 증가
1
4
7
10
세 번째 순차적 증가
1
2
3
4
5
6
7
8
9
10
네 번째 순차적 증가
1
4
7
10
```

## 반복문 while 문

조건이 참인 동안 무한 반복

```bash
while [ 조건 ]
do
	반복 실행할 문장
done
```

**while문 테스트 파일 ⇒ while1.sh**

```bash
#!/bin/bash

total=0
total2=0
i=1
while [ $i -le 10 ]
do
	total=`expr $total + $i`
	total2=$(($total2 + $i))
	i=`expr $i + 1`
done

echo "1부터 10까지의 합: " $total
echo "1부터 10까지의 합: " $total2

exit 0
```

**while문 테스트 파일 실행**

```bash
sh while1.sh

# 결과
1부터 10까지의 합:  55
1부터 10까지의 합:  55
```

## until 문

\- while문과 용도가 거의 같지만 until문은 조건식이 참일 때까지(=거짓인 동안) 계속 반복한다.

**until문 테스트 파일 ⇒** until1.sh

```bash
#!/bin/bash

total=0
total2=0
i=1
until [ $i -gt 10 ]
do
	total=`expr $total + $i`
	total2=$(($total2 + $i))
	i=`expr $i + 1`
done

echo "1부터 10까지의 합: " $total
echo "1부터 10까지의 합: " $total2

exit 0
```

**until문 테스트 파일 실행**

```bash
sh until1.sh

# 결과
1부터 10까지의 합:  55
1부터 10까지의 합:  55
```

## break, continue, exit, return 문

\- break : 반복문에서 빠져나올 때

\- continue : 반복문의 조건식으로 돌아갈 때

\- exit : 해당 프로그램을 종료할 때

\- return : 함수 안에서 함수를 호출한 곳으로 리턴할 때

## 함수 사용

```bash
# 함수 선언
함수이름 ( ) {
	$1, $2
}

# 함수 호출
함수이름 파라미터1 파라미터2 ... 
```

**함수 테스트 ⇒ func.sh**

```bash
#!/bin/bash

hello() {
	echo "안녕하세요!"
}

add() {
	echo $(($1 + $2))
}

hello
add 5 10

exit 0
```

**함수 테스트 실행**

```bash
sh func.sh

# 결과
안녕하세요!
15
```

## eval

문자열을 명령문으로 인식하고 실행

```bash
#!/bin/bash

str="ls -l eval.sh"
echo $str
eval $str
exit 0
```

**실행 코드**

```bash
root@server:~# sh eval.sh

# 결과
ls -l eval.sh
-rw-r--r-- 1 root root 60 12월  9 02:47 eval.sh
```

## export

\- 외부 변수로 선언해 준다. 즉, 선언한 변수를 다른 프로그램에서도 사용할 수 있도록 해준다.

**테스트 파일1 ⇒ export1.sh**

```bash
#!/bin/bash
echo $var1
echo $var2
exit 0
```

**테스트 파일2 ⇒ export2.sh**

```bash
#!/bin/bash

var1="지역 변수"
export var2="외부 변수"
sh export1.sh
exit 0
```

**테스트 실행**

```bash
sh export2.sh

# 결과

외부 변수
```

## printf

\- C언어의 printf() 함수와 비슷하게 형식을 지정해서 출력

**테스트 파일 작성 ⇒ printf1.sh**

```bash
#!/bin/bash
var1=100.5
var2="셸 스크립트 공부"
printf "%5.2f \n\n \t %s \n" $var1 "$var2"
exit 0
```

**테스트 파일 실행**

```bash
sh printf1.sh

# 결과
100.50 

 	 셸 스크립트 공부
```

## set과 $(명령어)

\- 리눅스 명령어를 결과로 사용하기 위해서는 $(명령어) 형식을 사용한다.
\- 결과를 파라미터로 사용하고자 할 때는 set과 함께 사용한다.

**테스트 파일 작성**

```bash
#!/bin/bash

echo "오늘 날짜는 $(date) 입니다."
set $(data)
echo "오늘은 $4 요일 입니다."

exit 0
```

**테스트 실행**

```bash
sh set1.sh

# 결과 
오늘 날짜는 2022. 12. 09. (금) 03:10:49 KST 입니다.
오늘은 (금) 요일 입니다.
```

## shift

\- 파라미터 변수를 왼쪽으로 한 단계씩 아래로 이동 시킨다.

**테스트 파일 작성**

```bash
#!/bin/bash

myfunc() {
	str=""

	while [ "$1" != "" ]
	do 
		str="$str $1"
		shift
	done
	echo $str
}
myfunc AAA BBB CCC DDD EEE

exit 0
```

**테스트 실행**

```bash
sh shift1.sh

# 결과
AAA BBB CCC DDD EEE
```
