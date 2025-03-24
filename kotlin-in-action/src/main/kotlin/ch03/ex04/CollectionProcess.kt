package ch03.ex04

/**
 * 컬렉션 처리
 *
 * 코틀린의 몇가지 특성
 * - vararg 키워드를 사용하면 호출 시 인자 개수가 달라질 수 있는 함수를 정의할 수 있다.
 * - 중위 함수 호출 구문을 사용하면 인자가 하나뿐인 메서드를 간편하게 호출할 수 있다.
 * - 구조 분해 선언을 사용하면 복합적인 값을 분해해서 여러 변수에 나눠 담을 수 있다.
 *
 *
 * 코틀린은 자바와 같은 클래스를 사용하지만 더 확장된 API를 제공하는데, 이는 확장 함수로 정의하고 항상 코틀린 파일에서 디폴트로 import 된다.
 *
 * 코틀린의 모든 확장 함수를 외우기 보다는 사용할 때 IDE의 도움을 빌어서 원하는 함수가 있으면 찾아서 사용하면 된다.
 *
 *
 * 가변 인자 함수
 *
 * 코틀린의 리스트 생성 함수는 가변 길이 인자인 vararg를 사용한다. 자바는 ... 을 사용한다.
 * fun <T> listOf(vararg elements: T): List<T> = 구현부
 *
 * 배열의 안지를 가변 길이 인자로 넘길 때는 스프레드 연산자인 * 를 사용한다.
 * listOf(*args)
 *
 *
 * 튜플 다루기: 중위 호출과 구조 분해 선언
 *
 * 중위 호출은 기본적으로 함수의 호출을 더 간단하게 만들어주는 기능이다.
 * 메소드 호출이 object.method() 형태인 것에 비해, 중위 호출은 object method argument 형태로 사용할 수 있다.
 * 중위 호출의 특징
 * - 중위 호출은 반드시 인자를 하나만 받아야 한다. (두 개 이상의 인자는 중위 호출로 사용할 수 없다.)
 * - 중위 호출을 사용하려면 해당 함수가 infix 키워드로 정의되어야 한다.
 * - 중위 호출은 인자와 메소드 이름 사이에 점(.)을 사용하지 않는다. 대신, 두 값을 공백으로 구분해서 호출한다.
 *
 * mapOf(1 to "one")에서 to는 코틀린 문법이 아닌 중위 호출이라는 방식으로 to 라는 메서드를 호출한 것이다.
 * 중위 호출 시에는 수신 객체 뒤에 메서드 이름을 위치시키고 그 뒤에 유일한 메서드 인자를 넣는다.
 * 1.to("one") 과 1 to "one" 는 동일하다
 *
 * to 함수의 정의 요약: infix fun <A, B> A.to(that: B): Pair<A, B> = Pair(this, that)
 * Pair는 코틀린에서 두 원소로 이루어진 순서 쌍을 표현한다.
 *
 * 튜플(pair) - 구조 분해 선언
 * val (number, name) = 1 to "one"
 *
 * to 함수는 확장함수로 타입과 관계 없이 임의의 순서쌍을 만들 수 있다. to의 객체가 제네릭이다.
 */
fun main() {
    val strings: List<String> = listOf("apple", "banana", "cherry")
    println(strings.last()) // 마지막 요소

    val numbers: Set<Int> = setOf(1, 2, 3, 4)
    println(numbers.sum()) // 모든 요소의 합

    val arr: Array<Int> = arrayOf(1, 2, 3, 4)
    val arrList = listOf(*arr)
    println(arrList)

    mapOf(1 to "one", 2 to "two")
}

