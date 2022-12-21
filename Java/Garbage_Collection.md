# 가비지컬렉션(Garbage Collection)

프로그램이 동적으로 할당했던 메모리 영역(Heap 메모리) 중 필요 없게 된 영역을 해제 시켜주기 위해서 사용한다.    
메모리 누수를 방지할 수 있고, 해제된 메모리에 대한 접근이나 해제된 메모리에 다시 접근하는 것을 방지할 수 있다.  
하지만 GC작업은 순수한 오버헤드이며. 개발자가 언제 GC가 메모리를 해제하는지 알 수 없다.  
<br/>
<br/>

## GC 알고리즘

### **Reference Counting**

heap영역에 선언된 객체들이 reference count를 가지며 reference count는 해당 객체에 접근할 수 있는 방법인데 rc가 0에 도달하면 가비지 컬렉터의 대상이 된다.  

: 순환참조가 일어날 때 rc가 1로 유지되게 때문에 memory leak이 발생하는 문제가 있다.  
<br/>


### **Mark And Sweep(순환 참조 문제 해결)**

Root Space에서부터 해당 객체에 접근 가능한지를 기준으로 삼는다.  

Mark: 그래프 순회를 통해 접근 가능한지를 찾아냄  
Sweep: 연결이 끊어진 객체는 지우는 방식  
Reachable: 루트로부터 연결된 객체  
Unreachable: 루트로부터 끊어진 객체  
  
: 의도적으로 GC를 실행시켜야 한다.  
: 어플리케이션 실행과 CD 실행이 병행된다.  
<br/>

### Root Space

Stack의 로컬변수  
Method Area의 static 변수  
Native Method Stack의 JNI참조  
<br/>
<br/>

## GC 순서 - Heap영역

![가비지컬렉션](https://user-images.githubusercontent.com/95058915/208910038-3a4402ef-f89f-4345-895e-b7c8ec84df1e.png)

Heap영역은 Young Generation과 Old Generation으로 나뉜다.  
YG는 Eden영역과 Survival0 Survival1로 나뉜다.  
YG에서 일어나는 GC를 Minor GC라고 한다.   
OG에서 발생하는 GC를 Major GC라고 한다.  
Eden영역: 새롭게 생성된 객체들이 할당되는 영역  
Survival영역: Minor GC로부터 살아남은 객체가 존재하는 영역  
Survival영역의 규칙이 있는데 Survival1, 2중 하나의 영역은 비워져있어야 한다.  
<br/>
<br/>

1. 새로운 객체가 생성되다보면 Eden영역이 꽉차는 순간이 오는데 이때 Minor GC가 일어나고 Mark And Sweep이 진행된다.  
2. Root로부터 Reachable로 판단된 객체는 Survival1으로 옮겨지고 Unreachable로 판단된 객체는 GC의 정리 대상이된다.  
3. S1으로 옮겨진 객체들의 age-bit가 0에서 1로 증가한다.  
4. 다시 시간이 지남에 따라 Eden영역이 꽉차면 Minor GC가 발생되고 Recahable이라고 판단된 객체들이 S2영역으로 이동한다.(이전 S1의 영역에 있던 객체도 함께 이동)  
5. 이렇게 계속 진행되며 age-bit가 일정 수준을 넘어가면 오래 참조된 객체로 판단되어 Old Generation으로 이동(Promotion)시킨다.  
\[ 자바8 기준 age-bit가 15일 때 Promotion 진행 \]
6. 다시 Eden영역에서부터 같은 방법으로 진행되면서 OG가 꽉차는 순간이 오면 Major GC가 발생하면서 Mark And Sweep방식을 통해서 필요없는 메모리를 비우게 된다.  
<br/>

Major GC는 Minor GC보다 오랜시간이 소요된다.  
YG와 OG를 나눈이유는 GC 설계자들이 어플리케이션을 분석해보니 대부분의 객체가 수명이 짧다는 것을 알게되고, GC도 비용이 드는데 메모리의 특정 부분만을 탐색하면서 해제하면 효율적이기 때문에 대다수의 객체는 금방 사라지므로 최대한 YG에서 해결하도록 한 것이다.  
<br/>

### Stop The World

JVM이 GC를 실행하기 위해서 어플리케이션 실행을 멈추는 것  
<br/>
<br/>

## GC 방식

#### **Serial GC**

: 하나의 쓰레드로 GC를 실행한다.  
: Stop The World 시간이 길다.  
: 싱글 쓰레드 환경 및 Heap이 매우 작을 때 사용한다.  
<br/>

#### **Parallel GC**

: 여러개의 쓰레드로 GC를 실행한다.  
: 멀티코어 환경에서 사용한다.  
: Java 8의 기본 GC 방식이다.  
<br/>

#### **CMS GC**

: Stop The World 최소화를 위해 연구되어 만들어졌다.  
: GC 작업을 어플리케이션과 동시에 실행한다.  
: G1 GC등장에 따라 Deprecated됐다.  
<br/>

#### **G1 GC**

힙을 일정 크기의 Region으로 잘게 나누어서 어떤 영역은 YG 어떤 영역은 OG로 사용한다.  

: Garbage First (G1)  
: Heap을 여러개의 작은 Region으로 나누어서 사용한다.  
: 짧은 중지 시간을 목표로 한다.  
: Java 9부터 기본 GC 방식이다.  
<br/>



## 참조
- [https://d2.naver.com/helloworld/1329](https://d2.naver.com/helloworld/1329)
