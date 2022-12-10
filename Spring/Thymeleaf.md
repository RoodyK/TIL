# 타임리프(Thymeleaf) 정리

SSR(Server Side Rendering)은 JSP만 사용해보다가 스프링에 더 적합한 Thymeleaf를 사용하며 내용을 정리해본다.
기본적으로 jstl을 사용해봤고 el태그에 대해서 전반적으로 아는 개발자라면 더 편하게 이해할 수 있다.

<br/>

## 타임리프(Thymeleaf)란?

[Thymeleaf](https://www.thymeleaf.org/)

타임리프란 웹과 서버 측 독립된 실행 환경을 위한 자바 템플릿 엔진이다. <br/>
JSP를 포함한 뷰 템플릿은 파일 자체를 브라우저에 렌더링 했을 때 jstl등을 사용한 태그로 인한 렌더링이 제대로 되지 않는 결과를 볼 수 있는데, 타임리프는 브라우저에서 파일자체를 열어도 정상적인 HTML의 마크업 결과를 얻을 수 있으며(동적인 결과 렌더링x), 서버를 통해서는 동적인 렌더링 결과를 확인할 수 있으므로 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 네추럴 템플릿이라고 한다.
<br/>
<br/>
<br/>

## 타임리프(Thymeleaf) 사용

#### **모듈 의존 등록**
타임리프를 사용하기 위해서 Thymeleaf 및 ThymeleafViewResolver를 dependency 해준다.
스프링 부트는 타임리프 템플릿 엔진을 사용하기 위한 모듈을 등록하고 설정해준다.
<br/>

**build.gradle**
```java
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```
<br/>

#### **경로설정**
스프링에서 동적인 페이지를 렌더링 하기 위해서는 **resources** 하위의 **templates** 폴더에 디렉토리 및 html파일을 생성해주면 된다.
기본 ThymeleafViewResolver설정은 위의 경로로 되어있지만 개발자가 원하는 방식으로 오버라이딩해서 변경이 가능하다.
<br/>

**application.properties**
```properties
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```
<br/>

#### **사용 선언**
모듈 의존 등록이 됬으면 html에 타임리프를 사용하겠다는 선언을 해준다.
<br/>

```html
<html xmlns:th="http://www.thymeleaf.org">
```
<br/>
<br/>

타임리프는 th:* 속성을 사용하며 서버가 렌더링되면 html 태그의 속성값이 th:* 값으로 대체된다.
<br/>

```html
<!-- 서버가 렌더링되면 name속성의 "id" 가 "userId"로 대체됨 -->
<input type="text" name="id" th:name="userId">
```
<br/>
<br/>
<br/>

## 타임리프(Thymeleaf) 속성(기능)

- 타임리프 표현식
  - 변수 표현식(SpringEL): ${...}
  - 선택 변수 표현식: \*{...}
  - 메시지 표현식: #{...}
  - 링크 URL 표현식: @{...}
  - 조각 표현식(fragment): ~{...}


- 리터럴
  - 텍스트: 'one text', 'hello thymeleaf!', ...
  - 숫자: 0, 11, 2.2, 12.3, ....
  - 부울(boolean): ture, false
  - 널: null
  - 리터럴 토큰: one, sometext, main, ...


- 문자 작업
  - 문자열 연결: +
  - 리터럴 대체: |The name is ${name}|


- 산술 연산
  - Binary operators: +, -, \*, /, %
  - Minus sign (unary operator): -


- 부울 연산
  - Binary perators: and, or
  - Boolean negation (unary operator): !, not


- 비교와 동등
  - 비교: >, <, >=, <= (gt, lt, ge, le)
  - 동등 연산: ==, != (eq, ne)


- 조건 연산
  - If-then: (if) ? (then)
  - If-then-else: (if) ? (then) : (else)
  - Default: (value) ?: (defaultvalue)


- 특별 토큰
  - No-Operation: _
  
<br/>
<br/>
<br/>

## 타임리프(Thymeleaf) 주요 태그
html 마크업에 자주 사용되는 문법을 정리한다.
표현식 안에 들어가는 변수는 Controller에서 model객체를 통해 넘겨준 값이라 생각한다.
<br/>

문법 | 용도 | 예시
:--:|:--:|:--:
th:text | 텍스트 출력(이스케이프 문자 처리) | th:text="${contnet}"
th:utext | 텍스트 출력(이스케이프 문자 처리X) | th:utext="${contnet}"
th:with | 지역변수 선언(선언한 scope에서만 사용가능) | th:with="variable=${content}"
th:each | 반복처리 | th:each="item : ${items}"
th:each | 반복상태 확인 | th:each="item, itemStat : ${items}"
th:if | 조건식 | th:if="${member.point == 1000}" th:text="'1000포인트'"
th:unless | 조건식(if의 반대) | th:unless="${member.point != 1000}" th:text="'1000포인트'"
th:block | 렌러딩시 제거되는 태그 | \<th:block th:each="item:${items}" \>\</th:block\>
th:inline | 자바스크립트에서 타임리프를 편하게 사용 | \<script th:inline="javascript" \>\</script\> |
th:fragment | 반복되는 코드 조각(헤더, 푸터 등) | \<header th:fragment="header"\>\</header\>
th:insert | 선언된 태그 하위에 fragment 삽입 | th:insert="~{fragment경로 :: fragment이름}"
th:replace | 선언된 태그를 fragment로 대체 | th:replace="~{fragment경로 :: fragment이름}"



<br/>
<br/>
<br/>

## 타임리프 주석
```html
1. 표준 HTML 주석 : 주석부분은 렌더링 되지않지만 페이지 소스엔 남아있다.
<!--
<span th:text=${data}>html data</span>
-->

2. 타임리프 파서 주석 : 서버 렌더링에서 주석부분을 아예 제거한다.
<!--/* [[${data}]] */-->

<!--/*-->
<span th:text="${data}">html data</span>
<!--*/-->

3. 타임리프 프로토 타입 주석 : html파일 자체를 열면 html주석이기 때문에 렌더링 되지 않지만 서버로 타임리프 렌더링을 거치면 정상 렌더링 된다.
<!--/*/
<span th:text="${data}">html data</span>
/*/-->
```

<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>

### 참조
- [Thymeleaf_Reference](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#introducing-thymeleaf)
- 인프런(inflearn) : 김영한님 - 스프링 MVC 2편 - 백엔드 웹 개발 활용 기술