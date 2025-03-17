# 8장. 메서드

메서드의 매개변수와 반환값을 어떻게 처리해야 하는지, 메서드 시그니처는 어떻게 설계 하는지, 문서화는 어떻게 해야 하는지를 다룬다.  
<br/>
<br/>

## 아이템 49. 매개변수가 유효한지 검사하라

메서드와 생성자는 대부분 입력 매개변수의 값 중 인덱스 값은 음수이면 안되며, 객체 참조는 nbull이 아니어야 하는 등의 특정 조건을 만족하기를 바란다.  

이런 제약은 반드시 문서화해야 하며 메서드 몸체가 시작되기 전에 검사해야 한다. 오류를 빨리 잡지 못하면 오류 감지도 어렵고, 감지해도 오류 발생 지점을 찾기 어렵다는 의미이다.  

메서드 몸체 실행 전 매개변수를 확인하면 잘못된 값에 대한 즉각적이고 깔끔한 방식으로 예외를 던질 수 있다.  

매개변수 검사를 제대로 하지 못하면 메서드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다.  

더 나쁜 상황은 메서드가 잘 수행되지만 잘못된 결과를 반환하거나, 메서드는 정상 수행됐지만 어떤 객체를 이상한 상태로 만들어놓아서 미래에 메서드에 관련없는 오류가 날 때다.  

<br/>

public과 protected 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화 해야 한다.(@throws 자바독 태그 사용)   
<br/>
```java
String s = Objects.requireNonNull(str, "널 체크");
```
<br/>

자바 7에 추가된 java.util.Objects.requireNonNull 메서드는 유연하고 사소용하기도 편하므로, null 검사를 수동으로 하지 않아도 된다.  

public이 아닌 메서드라면 단언문(assert)을 사용해 매개변수 유효성을 검증할 수 있다.  
<br/>

```java
// 재귀 정렬용 private 도우미 함수
private static void sort(long a[], int offset, int length) {
		assert a != null;
		assert offset >= 0 && offset <= a.length;
		assert length >= 0 && length <= a.length - offset;
}
```
<br/>

위 코드에서 핵심은 단언문들은 자신이 단언한 조건이 무조건 참이라고 선언한다는 것이다.  

단언문은 몇가지 면에서 일반적인 유효성 검사와 다르게 동작한다.  

- 실패하면 AssertionError을 던진다.
- 런타임에 아무런 효과도, 아무런 성능 저하도 없다. (단, java를 실행할 때 명령줄에서 -ea, —enableassertions 플래그를 설정하면 런타임에 영향을 준다.)  
<br/>

메서드 몸체 실행 전에 매개변수 유효성을 검사해야 한다는 규칙에도 예외는 있다. 유효성 검사 비용이 지나치게 높거나 실용적이지 않을 때, 혹은 계산 과정에서 암묵적으로 검사가 수행될 때다.  
<br/>
<br/>

### 핵심 정리

메서드나 생성자를 작성할 때면 그 매개변수들에 어떤 제약이 있을지 생각해야 한다. 그 제약들을 문서화하고 메서드 코드 시작 부분에서 명시적으로 검사해야 한다.  

이런 습관을 반드시 기르도록 하자. 그 노력은 유효성 검사가 실제 오류를 처음 걸러낼 때 충분히 보상받을 것이다.  
<br/>
<br/>
<br/>

## 아이템 50. 적시에 방어적 복사본을 만들라

자바는 C, C++에서 자주보는 버퍼 오버런, 배열 오버런, 와일드 포인터 같은 메모리 충돌 오류에서 안전한 언어다.  

하지만 자바라고 해도 다른 클래스로부터 침범을 다 막을 수 있는 것은 아니다. 그러므로 클라이언트가 여러분의 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍 해야한다.  
<br/>

```java
// 기간을 표현하는 클래스 - 불변식을 지키지 못함
public final class Period {
    private final Date start;
    private final Date end;

    /**
     * @param  start 시작 시각
     * @param  end 종료 시각. 시작 시각보다 뒤여야 한다.
     * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
     * @throws NullPointerException start나 end가 null이면 발생한다.
     */
    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0)
            throw new IllegalArgumentException(
                    start + "가 " + end + "보다 늦다.");
        this.start = start;
        this.end   = end;
    }

    public Date start() {
        return start;
    }
    public Date end() {
        return end;
    }

    public String toString() {
        return start + " - " + end;
    }
}
```
<br/>

위 코드는 불변식 같아 보이지만 Date가 가변이기 때문에 쉽게 불변식이 깨질 수 있다.  
<br/>

```java
Date start = new Date();
Date end = new Date();
Period p = new Period(start, end);
end.setYear(78); // p 내부 수정
```
<br/>

자바 8 이후에는 Date 대신 Instant나 LocalDateTime, ZoneDateTime을 사용해서 해결할 수 있다.  

Date는 낡은 API이니 새로운 코드를 작성할 때는 더이상 사용하면 안된다. 하지만 레거시 코드에는 잔재가 많이 남아있으므로 주의해야 한다.   

외부 공격으로부터 Period 인스턴스의 내부를 보호하려면 생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy)해야 한다.  

그 후 Period 인스턴스 안에서는 원본이 아닌 복사본을 사용한다.  
<br/>

```java
// 수정한 생성자 - 매개변수의 방어적 복사본을 만듬
public Period(Date start, Date end) {
   this.start = new Date(start.getTime());
   this.end   = new Date(end.getTime());

   if (this.start.compareTo(this.end) > 0)
       throw new IllegalArgumentException(
				       this.start + "가 " + this.end + "보다 늦다.");
}
```
<br/>

매개변수의 유효성 검사하기 전에 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사한 점에 주목하자.  

순서가 부자연스러워 보일 수 있지만 멀티 스레딩 환경이라면 원본 객체의 유효성을 검사한 후 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 원본 객체를 수정할 위험이 있기 때문에 이렇게 작성해야 한다.  

매개변수가 제 3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안된다.  

가변 필드의 변경을 해결하려면 방어적 복사본을 반환하면 된다.  
<br/>

```java
// 수정한 접근자 - 필드의 방어적 복사본을 반환
public Date start() {
   return new Date(start.getTime());
}

public Date end() {
   return new Date(end.getTime());
}
```
<br/>

방어적 복사에는 성능 저하가 따르고, 항상 쓸 수 있는 것은 아니다. 호출자가 컴포넌트 내부를 수정하지 않으리라 확신하면 방어적 복사를 생략할 수 있다.  

이러한 상황이라도 호출자에서 해당 매개변수나 반환값을 수정하지 말아야 함을 명확히 문서화 하는 것이 좋다.  
<br/>
<br/>

### 핵심 정리

클래스가 클라이언트로부터 받는 혹은 클라이언트로 반환하는 구성요소가 가변이라면 그 요소는 반드시 방어적으로 복사해야 한다. 

복사 비용이 너무 크거나 클라이언트가 그 요소를 잘못 수정할 일이 없을음 신뢰한다면 방어적 복사를 수행하는 대신 해당 구성요소를 수정했을 때의 책임이 클라이언트에 있음을 문서에 명시하도록 하자.  
<br/>
<br/>
<br/>

## 아이템 51. 메서드 시그니처를 신중히 설계하라

이번 아이템의 요령을 잘 활용하면 배우기 쉽고, 쓰기 쉽고, 오류 가능성이 적인 API를 만들 수 있을 것이다.  

메서드 이름은 신중히 짓자. 항상 표준 명명 규칙을 따라야 한다.  

- 같은 패키지에 속한 다른 이름들과 일관되게 짓기
- 개발자 커뮤니티에서 널리 받아들여지는 이름 사용  
<br/>

편의 메서드를 너무 많이 만들지 말자. 메서드가 너무 많은 클래스는 익히고, 사용하고, 문서화하고, 테스트하고, 유지보수하기 어렵다.  

매개변수 목록은 짧게 유지하자. 4개 이하로 사용하자. 같은 타입의 매개변수 여러개가 연달아 나오는 경우가 특히 해롭다.  
<br/>

긴 매개변수 목록을 짧게 줄여주는 기술

- 여러 메서드로 쪼갠다. (원래 메서드 매개변수 목록의 부분집합을 받음)
- 매개변수 여러 개를 묶어주는 도우미 클래스를 만든다. 일반적으로 도우미 클래스는 정적 멤버 클래스로 만듬
- 앞의 두 기법을 혼합 한 것으로, 객체 생성에 사용한 빌더 패턴을 메서드 호출에 응용한다고 보면된다. 이 기법은 매개변수가 많을 때, 특히 그 중 일부는 생략해도 괜찮을 때 도움이 된다.  
<br/>

매개변수의 타입으로는 클래스보다는 인터페이스가 낫다. 예로 메서드에 HashMap을 넘길 일은 없다. Map을 사용하면 구현체들로 유연하게 사용 가능하다.  

boolean보다는 원소 2개짜리 열거 타입이 낫다. 열거 타입을 사용하면 코드를 읽고 쓰기가 쉬워진다.  
<br/>
<br/>
<br/>

## 아이템 52. 다중정의(Overloading)는 신중히 사용하라

```java
public class Classifier {
    public static String classify(Set<?> s) {
        return "집합";
    }

    public static String classify(List<?> lst) {
        return "리스트";
    }

    public static String classify(Collection<?> c) {
        return "그 외";
    }

    public static void main(String[] args) {
        Collection<?>[] collections = {
                new HashSet<String>(),
                new ArrayList<BigInteger>(),
                new HashMap<String, String>().values()
        };

        for (Collection<?> c : collections)
            System.out.println(classify(c));
    }
}
```

위 코드에서 “집합”. “리스트”, “그 외”를 차례로 출력할 것 같지만 “그 외”만 연달아서 출력한다.  

다중정의(Overloading)은 어느 메서드를 호출할지가 컴파일타임에 정해직 대문이다. for문의 c는 항상 `Collection<?>` 타입 이므로 `classify(Collection<?>)`만 호출한다.  

이처럼 직관과 어긋나는 이유는 **재정의한 메서드는 동적(런타임)으로 선택되고, 다중정의한 메서드는 정적(컴파일)으로 선택되기 때문이다.**  

위 코드를 오버로딩에서 사용할 것이면 classify 메서드를 하나로 합친 후 instanceof로 명시적으로 검사하면 된다.  
<br/>

```java
public static String classify(Collection<?> c) {
    return c instanceof Set ? "집합" : 
					 c instanceof List ? "리스트" : "그 외";
}
```
<br/>

이처럼 오버로딩은 혼동을 일으키는 상황을 피해서 작성해야 한다. 안전하고 보수적으로 가려면 매개변수가 같은 오버로딩은 만들지 말자.  

오버로딩 하는 대신 메서드 이름을 다르게 지어주는 길도 항상 열려있다.  

생성자는 이름을 다르게 지을 수 없으니 두 번째 생성자 부터는 무조건 오버로딩이 된다. 하지만 정적 팩토리라는 대안을 활용할 수 있는 경우가 많다.  

매개변수 수가 같은 오버로딩 메서드가 많더라도, 그 중 어느 것이 주어진 매개변수 집합을 처리할지가 명확히 구분된다면 헷갈릴 일은 없을 것이다.  

매개변수 중 하나 이상이 “근본적으로 다르다(radically different)”면 헷갈릴 일이 없다. 근본적으로 다르다는 것은 두 타입의 (null이 아닌) 값을 서로 어느쪽으로든 형변환할 수 없다는 뜻이다.  

메서드를 오버로딩할 때, 서로 다른 함수형 인터페이스라도 같은 위치의 인수로 받아서는 안된다. 서로 다른 함수형 인터페이스라도 서로 근본적으로 다르지 않다는 뜻이다.  
<br/>
<br/>

### 핵심 정리

프로그래밍 언어가 오버로딩을 허용한다고 해서 오버로딩을 꼭 활용하란 뜻은 아니다. 

일반적으로 매개변수 수가 같을 때는 오버로딩을 피하는 게 좋다. 상황에 따라, 특히 생성자라면 이 조언을 따르기가 불가능할 수 있다. 

그럴 때는 헷갈릴 만한 매개변수는 형변환하여 정확한 다중정의 메서드가 선택되도록 해야 한다. 

이것이 불가능하면, 예컨데 기존 클래스를 수정해 새로운 인터페이스를 구현해야 할 때는 같은 객체를 입력받는 오버로딩 메서드들이 모두 동일하게 동작하도록 만들어야 한다. 

그렇지 못하면 프로그래머들은 오버로딩된 메서드나 생성자를 효과적으로 사용하지 못할 것이고, 의도대로 동작하지 않는 이유를 이해하지도 못할 것이다.  
<br/>
<br/>
<br/>

## 아이템 53. 가변 인수는 신중히 사용하라

가변인수 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있다.  

가변인 수를 사용할 때는 유효성 검사를 명시적으로 해야한다.  
<br/>

```java
// 간단한 가변인수 활용 예
static int sum(int... args) {
    int sum = 0;
    for (int arg : args)
        sum += arg;
    return sum;
}

// 인수가 1개 이상이어야 할 때 가변인수를 제대로 사용하는 방법
static int min(int firstArg, int... remainingArgs) {
    int min = firstArg;
    for (int arg : remainingArgs)
        if (arg < min)
            min = arg;
    return min;
}
```
<br/>

위의 코드처럼 매개변수를 2개 받도록 하고, 첫 번째로는 평범한 매개변수를 받고, 가변인수는 두 번째로 받으면 깔끔하게 사용할 수 있다.  

가변인수는 인수 개수가 정해지지 않았을 때 아주 유용하다. printf, 리플렉션 등에서 유용하게 사용하고 있다.  

하지만 성능에 민감한 상황이라면 걸림돌이 될 수 있다. 가변인수 메서드는 호출될 때마다 배열을 새로 하나 할당하고 초기화한다.  
<br/>
<br/>

### 핵심 정리

인수 개수가 일정하지 않은 메서드를 정의해야 한다면 가변인수가 반드시 필요하다. 

메서드를 정의할 때 필수 매개변수는 가변인수 앞에 두고, 가변인수를 사용할 때는 성능 문제까지 고려하자.  
<br/>
<br/>
<br/>

## 아이템 54. null이 아닌, 빈 컬렉션이나 배열을 반환하라

컬렉션이나 배열 같은 컨네이너가 비었을 때 null을 반환하는 메서드를 사용할 때면 항시 방어코드를 넣어줘야 한다.  
<br/>

때로는 빈 컨테이너를 할당하는 데도 비용이 드니 null을 반환하는 쪽이 낫다는 주장도 있는데, 이는 두가지 면에서 틀린 주장이다.  

- 분석 결과 이 할당이 성능 저하의 주범이라고 확인되지 않는 한, 이 정도의 성능 차이는 신경 쓸 수준이 못된다.
- 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다.
<br/>

```java
// 빈 컬렉션을 반환하는 올바른 예
public List<Cheese> getCheese() {
		return new ArrayList<>(cheeseInStock);
}

// 빈 컬렉션 최적화 - 매번 새로 할당하지 않음
public List<Cheese> getCheese() {
		return cheeseInStock.inEmpty() ? Collections.emptyList()
				: new ArrayList<>(cheeseInStock);
}
```
<br/>
<br/>

### 핵심 정리

null이 아닌, 빈 배열이나 컬렉션을 반환하라. null을 반환하는 API 는 사용하기 어렵고 오류 처리 코드도 늘어난다. 그렇다고 성능이 좋은 것도 아니다.  
<br/>
<br/>
<br/>

## 아이템 55. 옵셔널 반환은 신중히 하라

자바 8 이전에는 메서드가 특정 조건에서 값을 반환할 수 없을 때 취할 수 있는 선택지가 두 가지 있었다.  

예외를 던지거나, 반환 타입이 참조 타입이면 null을 반환하는 것이다. 하지만 이 방법들은 처리하는 코드를 추가해야하는 비용이 만만치 않았다. 그리고 null 값은 어딘가에 저장해두면 언젠가 NullPointerException이 발생할 수 있다.  

자바 8에서는 `Optional<T>`가 생겨서 null이 아닌 T 타입 참조를 하나 담거나, 혹은 아무것도 담지 않을 수 있다.  

아무것도 담지 않은 옵셔널은 ‘비었다’고  하고, 반대로 어떤 값을 담은 옵셔널은 ‘비지 않았다’고 한다.  

옵셔널은 원소를 최대 1개 가질 수 있는 ‘불변’ 컬렉션이다.  

보통은 T를 반환해야 하지만 특정 조건에서는 아무것도 반환하지 않아야 할 때 T 대신 `Optional<T>`를 반환하도록 선언하면 된다.  
<br/>

```java
// 컬렉션에서 최댓값을 구해 Optional<E>로 반환한다. (327쪽)
public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
   if (c.isEmpty())
       return Optional.empty();

   E result = null;
   for (E e : c)
       if (result == null || e.compareTo(result) > 0)
           result = Objects.requireNonNull(e);

   return Optional.of(result);
}
```
<br/>

위 코드에서 빈옵셔널은 Optional.empty()로 생성했고, 값이 든 옵셔널은 Optional.of(value)를 사용했는데 Optional.of()에 null값이 들어가면 NullPointerException을 던지니 주의해야 한다.  

null값도 허용하는 Optional을 만들려면 Optional.ofNullable(value)를 사용하면 된다.  

옵셔널을 반환하는 메서드에서는 절대 null을 반환하지 말자. 옵셔널을 사용하는 이유가 없다.   

<br/>

옵셔널은 검사 예외와 취지가 비슷하다. 즉, 반환값이 없을 수도 있음을 API 사용자에게 명확히 알려준다.  
<br/>
<br/>

### 옵셔널 기본값을 설정하는 방법

```java
// 기본 값을 정해둠
String lastWordInLexicon = max(words).orElse("단어 없음...");

// 원하는 예외를 던짐
Toy myToy = max(toys).orElseThrow(TemperTantrumException::new);

// 항상 값이 채워져있다고 가정(값이 null이면 NullPointerException을 던짐)
Element lastNobleGas = max(Elements.NOBLE_GASES).get();
```
<br/>

filter, map, flatMap, ifPresent 같은 특별한 메서드들도 있다. API를 참고해서 활용하자.  
<br/>

```java
// 불필요하게 사용한 Optional의 isPresent 메서드를 제거하자.

// isPresent를 적절치 못하게 사용했다.
Optional<ProcessHandle> parentProcess = ph.parent();
System.out.println("부모 PID: " + (parentProcess.isPresent() ?
        String.valueOf(parentProcess.get().pid()) : "N/A"));

// 같은 기능을 Optional의 map를 이용해 개선한 코드
System.out.println("부모 PID: " +
    ph.parent().map(h -> String.valueOf(h.pid())).orElse("N/A"));
```
<br/>

반환값으로 옵셔널을 사용한다고 해서 무조건 득이 되는 건 아니다. 컬렉션, 스트림, 배열, 옵셔널 같은 컨테이너 타입은 옵셔널로 감싸면 안된다. 빈 `Optional<List<T>>`를 반환하기 보다는 빈 `List<T>`를 반환하는 것이 좋다.  

빈 컨테이너를 그대로 반환하면 클라이언트에 옵셔널 처리 코드를 넣지 않아도 된다.  

<br/>

옵셔널을 사용할 때 메서드 반환타입을 T 대신 `Optional<T>` 로 선언해야 하는 규칙은 **결과가 없을 수 있으며, 클라이언트가 이 상황을 특별하게 처리해야 한다면 Optional<T>를 반환한다.**  

하지만 이렇게 사용해도 Optional은 엄연히 새로 할당하고 초기화 해야하는 객체고, 그 안에서 값을 꺼내려면 메서드를 호출해야 하니 한 단계를 더 거친다. 그러므로 성능이 중요한 상황에서는 옵셔널이 맞지 않을 수 있다.  
<br/>
<br/>

### 핵심 정리

값을 반환하지 못할 가능성이 있고, 호출할 때마다 반환값이 없을 가능성을 염두해 둬야 하는 메서드라면 옵셔널을 반환해야 할 상황일 수 있다. 

하지만 옵셔널 반환에는 성능 저하가 뒤따르니, 성능에 민감한 메서드라면 null을 반환하거나 예외를 던지는 편이 나을 수 있다. 

그리고 옵셔널을 반환값 이외의 용도로 쓰는 경우는 매우 드물다.  
<br/>
<br/>
<br/>

## 아이템 56. 공개된 API 요소에는 항상 문서화 주석을 작성하라

API를 쓸모 있게 하려면 잘 작성된 문서도 곁들여야 하며, 자바에서는 자바독(Javadoc)이라는 유틸리티가 이 작업을 도와준다.  

자바독은 소스코드 파일에서 문서화 주석(doc comment; 자바독 주석)이라는 특수한 형태로 기술된 설명을 추려 API 문서로 변환해 준다.  

문서화 주석을 작성하는 규칙은 공식 언어 명세에 속하진 않지만 자바 프로그래머라면 응당 알아야 하는 업계 표준 API라 할 수 있다.  

API를 올바르게 문서화하려면 공개된 모든 클래스, 인터페이스, 메서드, 필드 선언에 문서화 주석을 달아야 한다.  

메서드용 문서화 주석에는 해당 메서드와 클라이언트 사이의 규약을 명료하게 기술해야 한다.  

일반적으로 전제조건은 @throw 태그로 비검사 예외를 선언하여 암시적으로 기술한다. 또한 @param 태그를 이용해 조건에 영향받는 매개변수를 기술할 수 있다.  

전제조건과 사후조건 뿐만 아니라 부작용도 문서화 해야한다. 부작용이란 사후조건으로 명확히 나타나지는 않지만 시스템의 상태에 어떠한 변화를 가져오는 것을 뜻한다.  

메서드의 계약을 완벽히 기술하려면 모든 매개변수에 @param 태그를, 반환 타입이 void가 아니라면 @return 태그를, 발생할 가능성이 있는 모든 예외에 @throw 태그를 달아야 한다.  
<br/>

```java
/**
 * 이 리스트에서 지정한 위치의 원소를 반환한다.
 *
 * <p>이 메서드는 상수 시간에 수행됨을 보장하지 <i>않는다</i>. 구현에 따라
 * 원소의 위치에 비례해 시간이 걸릴 수도 있다.
 *
 * @param  index 반환할 원소의 인덱스; 0 이상이고 리스트 크기보다 작아야 한다.
 * @return 이 리스트에서 지정한 위치의 원소
 * @throws IndexOutOfBoundsException index가 범위를 벗어나면,
 * 즉, ({@code index < 0 || index >= this.size()})이면 발생한다.
 */
E get(int index) {
    return null;
}
```
<br/>

자바독 유틸리티는 문서화 주석을 HTML로 변환하므로 문서화 주석안의 HTML 요소들이 최종 HTML 문서에 반영된다.  

`{@code}` 태그의 효과는 두가지가 있다.  

- 태그로 감싼 내용을 코드용 폰트로 렌더링한다.
- 태그로 감싼 내용에 포함된 HTML 요소나  다른 자바독 태그를 무시한다.  
<br/>

자바 8에 추가된 @implSpec 태그는 자기사용 패턴을 문서화한다.  

@implSpec 주석은 해당 메서드와 하위 클래스 사이의 계약을 설명하여, 하위 클래스들이 그 메서드를 상속하거나 super 키워드를 이용해 호출할 때 그 메서드가 어떻게 동작하는지를 명확히 인지하고 사용하도록 해줘야 한다.  
<br/>

```java
/**
 * 이 컬렉션이 비었다면 true를 반환한다.
 *
 * @implSpec 이 구현은 {@code this.size() == 0}의 결과를 반환한다.
 *
 * @return 이 컬렉션이 비었다면 true, 그렇지 않으면 false
 */
public boolean isEmpty() {
    return false;
}
```
<br/>

자바 11까지도 자바독 명령줄에서 `-tag “impeSpec:a:Implementation Requrements:”` 스위치를 켜주지 않으면 @implSpec 태그를 무시해버린다.  

각 문서화 주석의 첫 번째 문장은 해당 요소의 요약 설명으로 간주된다.  

한 클래스(혹은 인터페이스) 안에서 요약 설명이 똑같은 멤버(혹은 생성자)가 둘 이상이면 안된다.  

제네릭 타입이나 제네릭 메서드를 문서화 할 때는 모든 타입 매개변수에 주석을 달아야 한다.  

열거 타입을 문서화할 때는 상스들에도 주석을 달아야 한다.  

애노테이션 타입을 문서화할 때는 멤버들에도 모두 주석을 달아야 한다.  

클래스 혹슨 정적 메서드가 스레드 안전하든 그렇지 않든, 스레드 안전 수준을 반드시 API 설명에 포함해야 한다.  

API문서가 잘 쓰인 문서인지를 확인하는 유일한 방법은 자바독 유틸리티가 생성한 웹페이지를 읽어보는 것 뿐이다. 다른 사람이 사용할 API라면 반드시 모든 API 요소를 검토하라.  
<br/>
<br/>

### 핵심 정리

문서화 주석은 API를 문서화하는 가장 훌륭하고 효과적인 방법이다. 공개 API라면 빠짐없이 설명을 달아야 한다. 

표준 규약을 일관되게 지키자. 

문서화 주석에 임의의 HTML 태그를 사용할 수 있음을 기억하라. 단, HTML 메타문자는 특별하게 취급해야 한다.  
<br/>
<br/>
<br/>
