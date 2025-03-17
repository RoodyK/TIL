# 7장. 람다와 스트림

자바 8에서 함수형 인터페이스, 람다, 메서드 참조 개념이 추가되면서 함수 객체를 더 쉽게 만들 수 있게 됐다. 스트림 API 까지 추가되면서 데이터 원소의 시퀀스 처리를 라이브러리 차원에서 지원하기 시작했다.  
<br/>
<br/>

## 아이템 42. 익명 클래스보다는 람다를 사용하라

예저네는 자바에서 함수 타입을 표현할 때 추상 메서드를 하나만 담은 인터페이스를 사용했다. 이런 인터페이스의 인스턴스를 함수 객체(function object)라고 하여, 특정 함수나 동작을 나타내는 데 썻다.  
<br/>

```java
// 익명 클래스의 인스턴스를 함수 객체로 사용 - 낡은 기법
Collections.sort(words, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    }
});
```
<br/>

전략 패턴처럼, 함수 객체를 사용하는 과거 객체지향 디자인 패턴에는 익명 클래스명 충분했다.  

자바 8에서는 추상 메서드 하나를 갖는 인터페이스는 특별한 의미를 인정받아 함수형 인터페이스라고 부르는 이 인터페이스들의 인스턴스를 람다식(lambda expression)을 사용해 만들 수 있게 됐다.  
<br/>

```java
Collection.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length());
```
<br/>

매개변수(s1, s2)의 반환값의 탕비은 각각 `(Comparator<String>), String, int`지만 코드에서는 언급이 없다. 컴파일러가 문맥을 살펴 타입을 추론해준 것이다.  

상황에 따라 컴파일러가 타입을 결정하지 못할 수도 있는데, 그럴 때는 프로그래머가 직접 명시해야 한다.  

타입 추론은 복합하므로, 타입을 명시해야 코드가 더 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자. 컴파일러가 “타입을 알 수 없다”라는 오류를 낼 때만 해당 타입을 명시하면 된다.  

람다는 비교자 생성 메서드를 사용하면 코드를 더 간결하게 만들 수 있다.  
<br/>

```java
Collections.sort(words, comparingInt(String::length));
```
<br/>

람다를 사용하면 열거 타입의 인스턴스 필드를 이용하는 방시긍로 상수별로 다르게 동작하는 코드를 쉽게 구현할 수 있다.  
<br/>

```java
// 함수 객체(람다)를 인스턴스 필드에 저장해 상수별 동작을 구현한 열거 타입 
public enum Operation {
    PLUS  ("+", (x, y) -> x + y),
    MINUS ("-", (x, y) -> x - y),
    TIMES ("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    Operation(String symbol, DoubleBinaryOperator op) {
        this.symbol = symbol;
        this.op = op;
    }

    @Override public String toString() { return symbol; }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```
<br/>

람다는 이름이 없고 문서화도 못 한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수 가 많아지면 람다를 쓰지 말아야 한다.  

람다는 함수형 인터페이스에서만 쓰인다. 추상 클래스의 인스턴스를 만들 때 람다를 쓸 수 없으니, 익명 클래스를 써야 한다.  

람다는 자신을 참조할 수 없다. 람다에서의 this 키워드는 바깥 인스턴스를 가리킨다.  

람다도 익명 클래스처럼 직렬화 형태가 구현별로 다를 수 있다. 그러므로 람다를 직렬화하는 일은 극히 삼가야 한다.  
<br/>
<br/>

### 핵심 정리

자바가 8로 판올림되면서 작은 함수 객체를 구현하는 데 적합한 람다가 도입되었다. 
익명 클래스는 타입의 인스턴스를 만들 때만 사용하라. 람다는 작은 함수 객체를 아주 쉽게 표현할 수 있어 함수형 프로그래밍의 지평을 열었다.  
<br/>
<br/>
<br/>

## 아이템 43. 람다보다는 메서드 참조를 사용하라

람다가 익명 클래스보다 나은 가장 큰 특징은 간결함인데, 자바에서는 함수 객체를 람다보다 메서드 참조를 이용해서 더욱 간결하게 만들 수 있다.  

임의의 키와 Integer 값의 매핑을 관리하는 프로그램의 일부이다.  
<br/>

```java
// 기존 키가 맵안에 없다면 키와 1의 값을 매핑하고, 있다면 기존 값 증가
// 기존 람다
map.merge(key, 1, (count, increse) -> count + increse));
// 메서드 참조
map.merge(key, 1, Integer::sum);
```
<br/>

하지만 어떤 람다에서는 매개변수의 이름 자체가 프로그래머에게 좋은 가이드가 되기도 한다. 

이런 람다는 길이는 더 길지만 메서드 참조보다 읽기 쉽고 유지보수도 쉬울 수 있다.  

람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없다.  

메서드 참조가 정답은 아니지만, 보통은 더 짧고 간결하므로, 람다로 구현했을 대 너무 길거나 복잡하면 메서드 참조가 좋은 대안이 된다.  
<br/>

```java
// 메서드 참조
service.execute(GoshThisClassNameIsHumongous::action);
// 람다가 오히려 더 깔끔하고 명확함
service.execute(() -> action());
```
<br/>

### 메서드 참조의 종류

| 메서드 참조 유형 | 예 | 같은 기능을 하는 람다 |
| --- | --- | --- |
| 정적 | Integer::parseInt | str → Integer.parseInt(str) |
| 참조대상 인스턴스를 특정하는 한정적(인스턴스) | Instant.now()::isAfter | Instant then = Instant.now();
t → then.isAfter(t) |
| 참조대상 인스턴스를 특정하지 않는 비한정적(인스턴스) | String::toLowerCase | str → str.toLowerCase() |
| 클래스 생성자 | TreeMap<K, V>::new | () → new TreeMap<K, V>() |
| 배열 생성자 | int[]::new | len → new int[len] |
<br/>
<br/>

### 핵심 정리

메서드 참조는 람다의 간단명료한 대안이 될 수 있다. 메서드 참조 쪽이 짧고 명확하다 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라.  

람다로는 불가능하나 메서드 참조로 가능한 유일한 예는 제네릭 함수 타입 구현이다.  
<br/>
<br/>
<br/>

## 아이템 44. 표준 함수형 인터페이스를 사용하라

자바가 람다를 지원하면서 API를 작성하는 사례도 상의 클래스의 기본 메서드를 재정의해 원하는 동작을 구현하는 템플릿 메서드 패턴의 매력이 줄어들었다.  

이를 대처하는 해법은 같은 효과의 함수 객체를 받는 정적 팩터리나 생성자를 제공하는 것이다.  

필요한 용도에 맞는게 있다면 직접 구현하지 않고 표준 함수형 인터페이스를 활용하라.  
<br/>

| 인터페이스 | 함수 시그니처 | 예 |
| --- | --- | --- |
| UnaryOperator<T> | T apply(T t) | String::toLowerCase |
| BinaryOperator<T> | T apply(T t1, T t2) | BigInteger::add |
| Predicate<T> | boolean test(T t) | Collection::isEmpty |
| Function<T, R> | R apply(T t) | Arrays::asList |
| Supplier<T> | T get() | Instant::now |
| Consumer<T> | void accept(T t) | System.out::println |
<br/>

표준 함수형 인터페이스 대부분은 기본 타입만 지원한다.  

그렇다고 기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하지는 말자. 오히려 계산량이 많을 때는 성능이 느려질 수 도 있다.  
<br/>

표준 함수형 인터페이스가 있더라도, 전용 함수형 인터페이스를 구현해야 할 경우 예시

- 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
- 반드시 따라야하는 규약이 있다.
- 유용한 디폴트 메서드를 제공할 수 있다.  
<br/>

전용 함수형 인터에이스를 작성하기로 했다면, 자신이 작성하는 게 ‘인터페이스’임을 명시해야 한다.  
<br/>

@FunctionalInterface 애노테이션이 있는데 @Override와 비슷하게 프로그래머의 의도를 명시하는 것으로, 세가지 목적이 있다.  

- 해당 클래스의 코드나 설명 문서를 읽을 이에게 그 인터페이스 람다용으로 설계된 것임을 알려준다.
- 해당 인터페이스가 추상 메서드를 오직 하나만 가지고 있어야 컴파일되게 해준다.
- 그 결과 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아준다.  
<br/>

그러므로 직접만든 함수형 인터페이스에는 항상 @FunctionalInterface 애노테이션을 사용하라.  

함수형 인터페이스를 API에서 사용할 때의 주의점이 있다.  

서로 다른 함수형 인터페이스를 같은 위치의 인수로 받는 메서드들을 다중정의해서는 안된다.  

클라이언트에게 불필요한 모호함만 안겨줄 뿐이고, 이로 인해 실제 문제가 일어나기도 한다.  

이 문제를 피하기 위한 가장 쉬운 방법은 서로 다른 함수형 인터페이스를 같은 위치의 인수로 사용하는 다중정의를 피하는 것이다.  
<br/>
<br/>
<br/>

### 핵심 정리

이제 자바도 람다를 지원한다. 여러분도 지금부터는 API를 설계할 때도 람다도 염두에 두어야 한다는 뜻이다. 

입력값과 반환값에 함수형 인터페이스 타입을 활용하라. 

보통은 `java.util.function` 패키지의 표준 함수형 인터페이스를 사용하는 것이 가장 좋은 선택이다. 단, 흔치는 않지만 직접 새로운 함수형 인터페이스를 만들어 쓰는 편이 나을 수도 있음을 잊지 말자.  
<br/>
<br/>
<br/>

## 아이템 45. 스트림은 주의해서 사용하라

스트림 API는 데이터 처리 작업(순차적, 병렬적)을 돕기 위해 자바 8에서 추가됐다.  

- 스트림은 데이터의 유한 혹은 무한 시퀀스를 뜻한다.
- 스트림 파이프라인은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다.  
<br/>

스트림의 원소들은 어디로부터든 올 수 있다. 대표적으로는 컬렉션, 배열, 파일, 정규표현식 패턴 매처, 난수 생성기, 다른 스트림 등이 있다.  

스트림 파이프라인은 소스 스트림에서 시작해 종단 연산(terminal operation)으로 끝나며, 그 사이에 하나 이상의 중간 연산(intermediate operation)이 있을 수 있다. 

각 중간 연산은 스트림을 변환(transform)한다. 특정 조건에 맞는 않는 원소를 걸러내거나, 한 스트림을 다른 스트림으로 변환할 수 있다.  

종단 연산은 마지막 중간 연산이 내놓은 스트림에 최후의 연산을 가한다. 원소를 정렬해 컬렉션에 담거나, 특정 원소 하나를 선택하거나 모든 원소를 출력하는 방식이다.  

스트림 파이프라인은 지연 평가(lazy evaluation) 된다. 평가는 종단 연산이 호출될 때 이뤄지며, 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다.  

지연 평가가 무한 스트림을 다룰 수 있게 해주는 열쇠다. 종단 연산이 없는 스트림 파이프라인은 아무 일도 하지 않는 명령어인 no-op과 같으니, 종단 연산을 빼먹지 말자.  

스트림 API는 메서드 연쇄를 지원하는 플루언트 API(Fluent API)다. 파이프라인 하나를 구성하는 모든 호출을 연결하여 단 하나의 표현식으로 완성할 수 있다. 파이프라인 여러 개를 연결해 표현식 하나로 만들 수 도 있다.  

기본적으로 스트림 파이프라인은 순차적으로 수행되며, parellel 메서드로 병렬 처리도 가능하나 효과를 볼 상황은 많지 않다.  

스트림 API는 다재다능하여 어떤 연산도 처리할 수 있고 제대로 사용하면 프로그램이 짧고 깔금해지지만, 잘못 사용하면 읽기 어렵고 유지보수도 힘들어 진다.   

람다 매개변수의 이름은 주의해서 정해야 한다. 람다에서는 타입 이름을 자주 생략하므로 매개벼수 이름을 잘 지어야 스트림 파이프라인의 가독성이 유지된다. 

도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복 코드에서보다 스트림 파이프라인에서 훨씬 크다. 

파이프라인에서는 타입 정보가 명시되지 않거나 임시 변수를 자주 사용하기 때문이다.  

스트림은 자바 기본타입인 char용을 지원하지 않기 때문에, char 값들을 처리할 때는 스트림을 삼가는 편이 좋다.  

스트림으로 모든 코드를 변경하지 말고, 스트림으로 변경할 때 코드가 더 나아 보이면 변경하도록 하자.  
<br/>
<br/>

### 함수 객체보다 코드 블록이 좋은 예

- 코드 블록에서는 범위 안의 지역변수를 읽고 수정할 수 있다. 하지만 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있고, 지역변수를 수정하는 건 불가능하다.
- 코드 블록에서는 return 문을 사용해 메서드에서 빠져나가거나, break나 continue 문으로 블록 바깥의 반복문을 종료하거나 반복을 한 번 건너뛸 수 있다. 또한 메서드 선언에 명시된 검사 예외를 던질 수 있다. 하지만 람다로는 이 중 어떤 것도 할 수 없다.  
<br/>
<br/>

### 스트림을 사용할 때의 좋은 예

- 원소들의 시퀀스를 일관되게 변환한다.
- 원소들의 시퀀스를 필터링한다.
- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다.(더하기, 연결하기, 최솟값 구하기 등)
- 원소들의 시퀀스를 컬렉션에 모은다.(공통된 속성을 기준으로 묶어가며)
- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.  
<br/>

스트림으로 처리하기 어려운 일은 한 데이터가 파이프라인의 여러 단계(stage)를 통과할 때 이 데이터의 각 단계에서의 값 들에 동시에 접근하기는 어려운 경우다.  

스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이기 때문이다.  
<br/>

```java
// 데카르트 곱 계산을 반복 방식, 스트림 방식으로 구현
public class Card {
    public enum Suit { SPADE, HEART, DIAMOND, CLUB }
    public enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN,
                       EIGHT, NINE, TEN, JACK, QUEEN, KING }

    private final Suit suit;
    private final Rank rank;

    @Override public String toString() {
        return rank + " of " + suit + "S";
    }

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;

    }
    private static final List<Card> NEW_DECK = newDeck();

		// 반복 방식으로 구현
    private static List<Card> newDeck() {
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values())
            for (Rank rank : Rank.values())
                result.add(new Card(suit, rank));
        return result;
    }

		// 스트림 방식으로 구현
		private static List<Card> newDeck() {
		   return Stream.of(Suit.values())
		           .flatMap(suit ->
		                   Stream.of(Rank.values())
		                           .map(rank -> new Card(suit, rank)))
		           .collect(toList());
		}
}
```
<br/>

flatMap은 스트림의 원소 각각을 하나의 스트림으로 매핑한 다음 그 스트림들을 다시 하나의 스트림으로 합친다.(평탄화)  
<br/>
<br/>

### 핵심 정리

스트림을 사용해야 멋지게 처리할 수 있는 일이 있고, 반복 방식이 더 알맞은 일도 있다. 

그리고 수많은 작업이 이 둘을 조합했을 때 가장 멋지게 해결된다. 

어느쪽을 선택하는 확고부동한 규칙은 없지만 참고할만한 지침 정도는 있다. 어느쪽이 나은지가 확연히 드러나는 경우가 많겠지만, 아니더라도 방법은 있다. 

스트림과 반복 중 어느쪽이 나은지 확신하기 어렵다면 둘다 해보고 더 나은 쪽을 택하라.  
<br/>
<br/>
<br/>

## 아이템 46. 스트림에서는 부작용이 없는 함수를 사용하라

스트림은 또 하나의 API가 아닌, 함수형 프로그래밍에 기초한 패러다임이다. 스트림이 제공하는 표현력, 속도, 병렬성을 얻으려면 API와 패러다임까지 함께 받아들어야 한다.  

스트림 패러다임의 핵심은 계산을 일련의 변환(Transformation)으로 재구성하는 부분이다.  

이때 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다. 순수함수란 오직 입력만이 결과에 영향을 주는 함수이다.  

다른 가변 상태를 참조하지 않고, 함수 스스로도 다른 상태를 변경하지 안흔다. 이렇게 하려면 스트림 연산에 건네는 함수 객체는 모두 부작용(Side Effect)이 없어야 한다.  
<br/>

```java
// 스트림 패러다임을 이해하지 못한 채 API만 사용했다 - 따라 하지 말 것!
Map<String, Long> freq = new HashMap<>();
try (Stream<String> words = new Scanner(file).tokens()) {
   words.forEach(word -> {
       freq.merge(word.toLowerCase(), 1L, Long::sum);
   });
}
```
<br/>

위 코드는 스트림 코드를 가장한 반복적 코드다. 스트림 API 이점을 살리지 못하여 같은 기능의 반복적 코드보다 읽기 어렵고, 유지보수에도 좋지 않다.  

이 코드의 모든 작업이 종단 연산인 forEach에서 일어나는데, forEach가 스트림이 수행한 연산 결과를 보여주는 일 이상을 하는 것을 보아 나쁜 코드일 것이라는 생각이 든다.
<br/>

```java
// 스트림을 제대로 활용해 빈도표를 초기화한다.
Map<String, Long> freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words
            .collect(groupingBy(String::toLowerCase, counting()));
}
```
<br/>

다음 코드는 스트림 API를 제대로 활용한 예로 짧고 명확하다.  

forEach 연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산하는 데 쓰지 말자. 가끔은 스트림 꼐산 결과를 기존 컬렉션에 추가하는 등의 다른 용도로 쓸 수 있다.

위의 코드에서 사용되는 collector는 스트림을 하는데 중요한 개념이다.  

`java.util.stream.Collectors` 클래스는 메서드를 39를 가지며, API가 갖는 장점을 잘 활용할 수 있게 해준다.  

collector를 사용하면 스트림의 원소를 컬렉션으로 모을 수 있다. `toList(), toSet(), toCollection(collectionFactory)` 로 리스트, 집합, 프로그래머가 지정한 컬렉션 타입을 반환한다.  
<br/>

```java
// 빈도표에서 가장 흔한 단어 10개를 뽑아내는 파이프라인
List<String> topTen = freq.keySet().stream()
        .sorted(comparing(freq::get).reversed())
        .limit(10)
        .collect(toList());
```
<br/>

`toList()`는 Collectors의 메서드인데, Collectors의 멤버를 static import를 하면 스트림 파이프라인의 가독성이 좋아져 보통 이렇게 사용한다.  

`comparing` 메서드는 키 추출 함수를 받는 비교자 생성 메서드다. 한정적 메서드 참조이자, 키 추출 함수로 쓰인 `freq::get`은 입력받은 키를 빈도표에서 추출해서 그 빈도를 반환한다.  

Collectors가 제공하는 다른 메서드인 groupingBy가 있다.  

이 메서드는 입력으로 분류 함수(classifier)를 받고 출력으로는 원소들을 카테고리 별로 모아 놓은 Map을 담은 수집기를 반환한다.  

분류 함수는 입력받은 원소가 속하는 카테고리를 반환한다. 그리고 이 카테고리가 해당 원소의 Map 키로 쓰인다.  

다중정의된 groupingBy 중 형태가 가장 간단한 것은 분류 함수 하나를 인수로 받아 맵을 반환한다. 반환된 맵에 담긴 각각의 값은 해당 카테고리에 속하는 원소들을 모두 담은 리스트다.  

```java
words.collect(groupingBy(word -> alphabetize(word));
Map<String, Long> freq = words
		.collect(grouping(String::toLowerCase, counting()));
```
<br/>

Collectors의 partitioningBy 메서드도 있는데, 분류 함수 자리에 predicate를 받고 키가 Boolean인 맵을 반환한다.  

Collectors의joining 메서드는 문자열 등의 CharSequence 인스턴스의 스트림에만 적용할 수 있다. 이 중 매개변수가 없는 joining은 단순히 원소들을 연결하는 collector를 반환한다.   

counting 메서드가 반환하는 collector는 다운스트림 collector 전용이다.  

인수 하나짜리 joining은 CharSequence 타입의 구분문자를 매개변수로 받아, 연결부위에 이 구분문자를 삽입하는데, 구분문자로 쉼표(,)를 입력하면 CS 형태의 문자열을 만들어 준다.  
<br/>
<br/>

### 핵심 정리

스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있다. 

스트림뿐 아니라 스트림 관련 객에체 건네지는 모든 함수 객체가 부작용이 없어야 한다. 

종단 연산 중 forEach는 스트림이 수행한 계산 결과를 보고할 때만 이용해야 한다. 계산 자체에는 이용하지 말자. 

스트림을 올바르게 사용하려면 수집기를 잘 알아둬야 한다. 가장 중요한 수집기 팩토리는 toList, toSet, toMap, groupingBy, joining이다.  
<br/>
<br/>
<br/>

## 아이템 47. 반환 타입으로는 스트림보다 컬렉션이 낫다

원소 시퀀스(일련의 원소)를 반환하는 메서든는 수 없이 많다.  

스트림은 반복을 지원하지 않는다. 따라서 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다. API를 스트림만 반환하도록 짜놓으면 반환된 스트림을 for-each로 반복하길 원하는 사용자는 당연히 불만을 토로할 것이다.  

사실 Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함할 뿐만 아니라, Iterable 인터페이스가 정의한 방식대로 동작한다.  

그럼에도 for-each로 스트림을 반복할 수 없는 이유는 Stream이 Iterable을 확장(extend)하지 않아서다.  

스트림을 for-each로 사용하려면 어뎁터를 사용하면 된다.  
<br/>

```java
// 스트림 <-> 반복자 어댑터
public class Adapters {
    // Stream<E>를 Iterable<E>로 중개해주는 어댑터
    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }

    // Iterable<E>를 Stream<E>로 중개해주는 어댑터
    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
```
<br/>

객체 시퀀스를 반환하는 메서드를 작성하는데, 이 메서드가 오직 스트림 파이프라인에서만 쓰일걸 안다면 스트림을 반환해도 좋다.  

반대로 반환된 객체들이 반복문에서만 쓰일 걸 안다면 Iterable을 반환하자.  

하지만 공개 API를 작성할 때는 스트림 파이프라인을 사용하는 사람과 반복문에서 쓰려는 사람 모두를 배려해야 한다.  

Collection 인터페이스는 Iterable의 하위 타입이고 stream 메서드도 제공하니 반복과 스트림을 동시에 지원한다. 

따라서 원소 시퀀스를 반환하는 공개 API의 반환 타입에는 Collection이나 그 하위 타입을 쓰는 게 일반적으로 최선이다.  
<br/>
<br/>

### 핵심 정리

원소 시퀀스를 반환하는 메서드를 작성할 때는, 이를 스트림으로 처리하기를 원하는 살용자와 반복으로 처리하길 원하는 사용자가 모두 있을 수 있음을 떠올리고, 양쪽을 다 만족시키려 노력하자. 

컬렉션을 반환할 수 있다면 그렇게 하라. 반환 전부터 이미 원소들을 컬렉션에 담아 관리하고 있거나 컬렉션을 하나 더 만들어도 될 정도로 원소 개수가 적다면 ArrayList 같은 표준 컬렉션에 담아 반환하라. 

그렇지 않으면 앞서의 멱집합 예처럼 전용 컬렉션을 구현할지 고민하라. 

컬렉션을 반환하는 게 불가능하면 스트림과 Iterable 중 더 자연스러운 것을 반환하라. 

만약 나중에 Stream 인터페스가 Iterable을 지원하도록 자바가 수정된다면, 그때는 안심하고 스트림을 반환하면 될것이다.(스트림 처리와 반복 모두에 사용할 수 있으니)  
<br/>
<br/>
<br/>

## 아이템 48. 스트림 병렬화는 주의해서 적용하라

자바 8부터 parallel 메서드만 한 번 호출하면 파이프라인을 병렬 실행할 수 있는 스트림을 지원했다.  

동시성 프로그래밍을 할 때는 안정성(safety)과 응답 가능(liveness) 상태를 유지해야 하는데, 병렬 스트림 파이프라인 프로그래밍에서도 다를 것이 없다.  
<br/>

```java
// 스트림을 사용해 20개의 메르센 소수를 생성하는 프로그램
public static void main(String[] args) {
    primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
            .parallel() // 스트림 병렬화
            .filter(mersenne -> mersenne.isProbablePrime(50))
            .limit(20)
            .forEach(System.out::println);
}

static Stream<BigInteger> primes() {
    return Stream.iterate(TWO, BigInteger::nextProbablePrime);
}
```
<br/>

위의 코드를 저자의 컴퓨터에서 실행하면 소수를 찍기 시작해서 12.5초가 걸린다.  

이 코드의 성능을 높이고 싶어서 스트림 파이프라인의 parallel()을 호출하는 순간 성능이 개선되는 것이 아니라, 이 프로그램은 아무것도 출력하지 못하면서 CPU는 90%나 잡아먹는 상태가 무한히 계속된다.(응답불가; liveness failure)  

이는 스트림 라이브러리가 이 파이프라인을 병렬화 하는 방법을 찾아내지 못했기 때문이다. 

환경이 아무리 좋더라도 데이터 소스가 Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 별렬화로는 성능 개선을 기대할 수 없다.  
<br/>
<br/>

대체로 스트림의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위일 때 병렬화의 효과가 가장 좋다.  

이 자료구조들은 모두 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어서 일을 다수의 스레드에 분배하기 좋다는 특징이 있다.  

이 자료구조들의 다른 중요한 공통점은 원소들을 순차적으로 실행할 때의 참조 지역성(locality of reference)이 뛰어나다는 것이다. 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻이다. 

하지만 참조들이 가리키는 실제 객체가 메모리에서 서로 떨어져 있을 수 있는데, 그러면 참조 지역성이 나빠진다. 참조 지역성이 낮으면 스레드는 데이터가 주 메모리에서 캐시메모리로 전송되어 오기를 기다리며 대부분ㅇ 시간을 멍하니 보내게 된다. 

따라서 첨조 지역성은 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 중요한 요소로 작용한다.  

참조 지역성이 가장 뛰어난 자료구조는 기본 타입의 배열이다. 기본 타입 배열에서는 (참조가 아닌) 데이터 자체가 메모리에 연속해서 저장되기 때문이다.  
<br/>
<br/>

스트림의 종단 연산 중 병렬화에 가장 접합한 것은 축소(reduction)다.  

축소는 파이프라인에서 만들어지는 모든 원소를 하나로 합치는 작업으로, Stream의 reduce 메서드 중 하나, 혹은 min max, count, sum 같이 완성된 형태로 제공되는 메서드 중 하나를 선택해 수행한다. 

anyMatch, allMatch, noneMatch 처럼 조건에 맞으면 바로 반환되는 메서드도 병렬화에 적합하다.  

반면, 가변 축소(mutable reduction)를 수행하는 Stream의 collect 메서드는 컬렉션들을 합치는 부담이 크기 때문에 병렬화에 적합하지 않다.  

스트림을 잘못 병렬화하면 (응답 불가를 포함해) 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상 못한 동작이 발생할 수 있다. 
<br/>
<br/>

### 핵심 정리

계산도 올바로 수행하고 성능도 빨라질 거라는 확신 없이는 스트림 파이프라인 별렬화는 시도조차 하지 말라. 

스트림을 잘못 병렬화하면 프로그램을 오동작하게 하거나 성능을 급격히 떨엍뜨린다. 병렬화하는 편이 낫다고 믿더라더, 수정 후의 코드가 여전히 정확한지 확인하고 운영 환경고 ㅏ 유사한 조건에서 수행해보며 성능지표를 유심히 관찰하라. 

그래서 계산도 정확하고 성능도 좋아졌음이 확실해졌을 때, 오직 그럴 때만 병렬화 버전 코드를 운영 코드에 반영하라.  
<br/>
<br/>
<br/>
