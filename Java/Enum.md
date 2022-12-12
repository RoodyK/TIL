# 열거형(enum)

: 관련된 상수들을 같이 묶어놓은것. java는 타입에 안전한 열거형을 제공

```java
enum kind {
  CLOVER, HEART, SPADE, DIAMOND
} 
```

: 열거형을 정의하는 방법 

```java
// enum 열거형 이름 {상수명1, 상수명2, ....}
enum kind {
  CLOVER, HEART, SPADE, DIAMOND
} 
```

: 열거형 타입의 변수를 선언하고 사용하는 방법

```java
class A {
  Kind kind;

  void init() { kind = Kind.SPADE; }
}
```

: 열거형 상수의 비교에 == 와 compareTo() 사용가능 / 비교연산자는X
```java
if(dir == Kind.SPADE) {}
```

## 열거형의 조상 java.lang.Enum]

: 모든 열거형은 Enum의 자손이며, 아래의 메서드를 상속받는다.

```java
// 열거형 상수의 이름을 문자열로반환
String name() 

// 열거형 상수가 정의된 순서를 반환(0부터 시작)
int ordinal() 

// 지정된 열거형에서 name과 일치하는 열거형 상수를 반환
T valueOf(Class<T> enumType. String name) 

// values(), valueOf()는 컴파일러가 자동으로 추가
```

## 열거형에 멤버 추가하기

: 불연속적인 열거형 상수의 경우, 원하는 값을 괄호()안에 적는다

```java
enum Direction {
  EAST(1), SOUTH(5), WEST(-1), NORTH(10)
}
```

: 괄호()를 사용하려면, 인스턴스 변수와 생성자를 새로 추가해 줘야 한다.

```java
enum Direction {

  EAST(1), SOUTH(5), WEST(-1), NORTH(10);

  private final int value;

  private Direction(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
```

: 열거형의 생성자는 묵시적으로 private이므로, 외부에서 객체생성 불가
