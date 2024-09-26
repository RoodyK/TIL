주석처럼 프로그래밍 언어에 영향을 미치지 않으며, 유용한 정보를 제공한다

### 애노테이션의 요소

어노테이션은 다양한 요소를 가질 수 있다. 요소는 메서드 형태로 정의되며, 기본값을 가질 수 있다.
요소가 하나이고 이름이 value일 때는 요소의 이름 생략 가능하다.
요소의 타입이 배열인 경우, 괄호{}를 사용해야 한다.
<br/>

### 모든 애노테이션의 조상

모든 어노테이션은 `java.lang.annotation.Annotation` 인터페이스를 상속받는다. 이 인터페이스는 어노테이션의 기본적인 기능을 제공하며, 모든 어노테이션은 이 인터페이스의 메서드를 사용할 수 있다.

Annotation은 모든 어노테이션의 조상이지만 상속은 불가능하다.
<br/>

### 마커 애노테이션

마커 애노테이션은 요소가 정의되지 않은 애노테이션이다. 즉, 단순히 해당 클래스나 메서드에 특정 의미를 부여하기 위해 사용된다. 예를 들어, `@Deprecated`가 마커 애노테이션의 한 예이다. 이 애노테이션은 해당 요소가 더 이상 사용되지 않음을 나타낸다.
<br/>

### 애노테이션 요소의 규칙

- 요소는 메서드 형태로 정의된다.
- 요소의 반환 타입은 기본 타입, `String`, `Class`, enum, 다른 애노테이션 타입, 배열 타입 등이 가능하다.
- 요소에 기본값을 설정할 수 있으며, 기본값이 설정된 요소는 어노테이션을 사용할 때 생략할 수 있다.
- 요소는 public 접근 제어자를 가져야 한다.

<br/>

### @Override

`@Override`는 자바에서 메서드 오버라이딩을 나타내는 어노테이션이다. 이 어노테이션은 자식 클래스에서 부모 클래스의 메서드를 재정의할 때 사용된다. `@Override`를 사용하면 코드의 가독성을 높이고, 잘못된 오버라이딩을 방지하는 데 도움이 된다.
<br/>

### @Deprecated

`@Deprecated`는 앞으로 사용하지 않을 것을 권장하는 필드나 메서드에 붙인다.
<br/>

### @FunctionalInterface

함수형 인터페이스에 붙이면, 컴파일러가 올바르게 작성했는지 체크한다. 함수형 인터페이스에는 하나의 추상 메소드만 가져야 한다는 제약이 있다.
<br/>

### @SuppressWarnings

컴파일러의 경고메시지가 나타나지 않게 억제한다. 괄호()안에 억제하고자 하는 경고의 종류를 문자열로 지정한다.
<br/>
<br/>

## 메타 어노테이션

메타 어노테이션은 어노테이션에 붙이는 어노테이션이다. 즉, 사용할 어노테이션을 정의하는 데 사용한다. 메타 어노테이션은 사용할 어노테이션의 적용대상 또는 어노테이션을 유지하는 시간 등을 규정한다.
<br/>

### @Target

어노테이션을 정의할 때 적용대상 지정에 사용
<br/>

### @Retention

어노테이션이 유지(retention)되는 기간을 지정하는데 사용

- 유지정책 SOURCE : 소스 파일에만 존재. 클래스 파일에는 존재하지 않음
- 유지정책 RUNTIME : 클래스 파일에 존재. 실행 시에 사용가능

<br/>

### @Documented, @Inherited

Javadoc으로 작성한 문서에 포함시키려면 @Documented를 붙인다.
어노테이션을 자손 클래스에 상속하고자 할 때, @Ingerited를 붙인다.
<br/>

### @Repeatable

반복해서 붙일 수 있는 어노테이션을 정의할 때 사용
<br/>
<br/>

## 어노테이션 타입 정의하기

어노테이션을 직접 만들어 쓸 수 있다.

```java
// 이 어노테이션은 메서드에 사용가능하며 RUNTIME에 동작한다.
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface 어노테이션이름 {

  // 타입 요소이름();
  String name() default "name";
  String type();
}
```
<br/>

어노테이션의 값을 부여하기 원한다면 변수를 지정 가능하다.
어노테이션의 메서드는 추상 메서드이며 어노테이션을 적용할때 지정(순서x)
<br/>

### @Target

어노테이션을 적용할 위치를 선택한다.

- ElementType.PACKAGE : 패키지 선언 시
- ElementType.TYPE : 타입 선언 시
- ElementType.ANNOTATION_TYPE : 어노테이션 타입 선언 시
- ElementType.CONSTRUCTOR : 생성자 선언 시
- ElementType.FIELD : 멤버 변수 선언 시
- ElementType.LOCAL_VARIABLE : 지역 변수 선언 시
- ElementType.METHOD : 메서드 선언 시
- ElementType.PARAMETER : 매개변수 선언
- ElementType.TYPE_PARAMETER : 매개변수 타입 선언 시
- ElementType.TYPE_USE : 타입 선언 시

<br/>
 
### @Retention

자바 컴파일러가 어노테이션을 다루는 방법을 기술하며, 특정 시점까지 영향을 미치는지를 결정한다.

- RetentionPolicy.SOURCE : 컴파일 전까지만 유효하며 컴파일 이후에는 사라짐
- RetentionPolicy.CLASS : 컴파일러가 클래스를 참조할 때까지 유효
- RetentionPolicy.RUNTIME : 리플렉션을 사용하여 컴파일 이후에도 JVM에 의해 계속 참조가 가능

<br/>

### @Documented

해당 어노테이션을 Javadoc에 포함한다.