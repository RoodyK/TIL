package ch09.ex04

data class Point(val x: Int, val y: Int)

/**
 * 구조 분해 선언
 *
 * 구조 분해를 사용하면 복합적인 값을 분해해서 별도의 여러 지역 변수를 한꺼번에 초기화할 수 있다.
 *
 * 구조 분해 선언은 일반 변수 선언과 비슷해보이며, 등호 왼쪽의 여러 변수를 괄호로 묶었다는 점이 다르다.
 * 내부에서 구조 분해 선언은 다시 관례를 사용한다.
 * 구조 분해 선언의 각 변수를 초기화하고자 componentN 이라는 함수를 호출한다. 여기서 N은 구조 분해 선언에 있는 변수 위치에 따라 붙는 번호다.
 * val (a, b) = p => val a = p.component1(), val b = p.component2()
 *
 * data 클래스의 주 생성자에 들어있는 프로퍼티에 대해서는 컴파일러가 자동으로 componentN 함수를 만들어준다.
 */

/**
 * data 타입이 아닌 클래스에서의 구현
 */
class Point2(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}

/**
 * 구조 분해 선언은 함수에서 여러 값을 반환할 때 유용하다.
 * 여러 값을 한꺼번에 반환해야 하는 함수가 있다면 반환해야 하는 모든 값이 들어갈 데이터 클래스를 정의하고 함수의 반환 타입을 그 데이터 클래스로 바꾼다.
 * 구조 분해 선언 구문을 사용하면 이런 함수가 반환하는 값을 쉽게 풀어 여러 변수에 넣을 수 있다.
 */
data class NameComponents(val name: String, val extension: String)

fun splitFilename(fullName: String): NameComponents {
    val result: List<String> = fullName.split(".", limit = 2)
    return NameComponents(result[0], result[1])
}

/**
 * componentN을 무한히 선언할 수는 없어 이런 구문을 무한정 사용할 수 없지만, 컬렉션에 대한 구조 분해는 유용하다.
 * 코틀린 라이브러리에서는 맨 앞의 다섯 원소에 대한 componentN을 제공한다.
 *
 * 함수에서 여러 값을 반환하는 더 단순한 방법은 표준 라이브러리의 Pair, Triple 클래스를 사용하는 것이다.
 * Pair, Triple 클래스는 그 안에 담겨있는 원소의 의미를 말해주지 않으므로 코드에서 귀종한 표현력을 잃게되는 단점이 있다.
 */

/**
 * 구조 분해 선언과 루프
 *
 * 함수 본문 내의 선언문뿐 아니라 변수 선언이 들어갈 수 있는 장소라면 어디든 구조 분해 선언을 사용할 수 있다.
 *
 * 코틀린 라이브러리는 Map에 대한 iterator를 제공하고, Map.Entryㅇ ㅔ대한 확장 함수로 component1, component2를 제공한다.
 */
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

/**
 * 람다가 data class나 맵 같은 복합적인 값을 파라미터로 받을 때도 구조 분해 선언을 쓸 수 있다.
 * map.forEach { (key, value) -> println("$key -> $value") }
 */

/**
 * _ 문자를 사용해 구조 분해 값 무시
 *
 * 컴포넌트가 여럿 있는 객체에 대해 구조 분해 선언을 사용할 때는 변수 중 일부가 필요 없을 경우가 있다.
 *
 * 전체 객체를 구조 분해해야만 하는 것은 아니기 때문에 구조 분해 선언에서 뒤쪽의 구조 분해 선언을 제거할 수 있다.
 * 중간의 변수를 구조 분해 선언하지 않을 때는 코틀린은 사용하지 않는 구조 분해 선언에 대해 _ 문자를 사용한다.
 */
data class Person(val firstName: String, val lastName: String, val age: Int, val city: String)

fun introducePerson(p: Person) {
    val (firstName, lastNAme, age, city) = p
    val (firstName2, lastNAme2, age2) = p // 뒤쪽 구조 분해 선언 제거
    val (firstName3, _, age3) = p // 무시할 컴포넌트에 _ 문자 사용
    println("This is $firstName, aged $age")
    println("This is $firstName2, aged $age2")
    println("This is $firstName3, aged $age3")
}

/**
 * 코틀린 구조 분해의 한계와 단점
 *
 * 구조 분해 선언 구현은 위치에 의한 것으로, 구조 분해 연산의 결과가 오직 인자의 위치에 따라 결정된다.
 * 구조 분해의 결과가 대입될 변수의 이름은 중요하지 않다. 구조 분해가 componentN 함수에 대한 순차적 호출로 변환되기 때문이다.
 * val (firstName, lastNAme, age, city) = p
 * val (f, l, a, c) = p
 *
 * 이로 인해 리팩터링을 하면서 데이터 클래스의 프로퍼티 순서를 변경하면 미묘한 문제가 발생할 수 있다.
 * Person 클래스의 firstName, lastName 인자의 위치가 바뀌어도 코드는 정상 동작한다. data class Person(val lastName: String, val firstName: String, ...)
 *
 * 이런 동작은 구조 분해 선언이 작은 컨테이너 클래스나 장차 변경될 가능성이 아주 적은 클래스에 대해서만 유용하다는 것을 의미한다. 복잡한 엔티티에 대해 구조 분해 사용을 피하는 것이 좋다.
 *
 * 이 문제에 대한 잠재적 해법은 이름 기반 구조 분해를 도입하는 것으로, 현재 논의 중으로 향후 코틀린 버전에 추가될 계획이 있다.
 */

fun main() {
    val p = Point(10, 20)
    val (x, y) = p
    println(x)
    println(y)

    val (name, ext) = splitFilename("example.txt")
    println("filename=$name, ext=$ext")

    val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
    printEntries(map)

    introducePerson(Person("Bob", "Johnson", 22, "Paris"))
}