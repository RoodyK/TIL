package ch07.ex06

/**
 * 자바의 instanceof 검사 대신 코틀린이 제공하는 안전한 캐스트 연산자(safe-cast operator)를 확인한다.
 * 안전한 캐스트 연산자를 엘비스 연산자나 안전한 호출 연산자와 함께 사용하는 경우가 자주 있다.
 *
 * 2장에서 확인한 코틀린 타입 캐스트 연산자인 as를 확인했는데, as로 지정한 타입으로 변경 불가능하면 ClassCastException이 발생한다.
 * as를 사용할 때마다 is를 통해 미리 as로 변환 가능한 타입인지 검사해볼 수 있지만 코틀린은 더 좋은 방법을 제공한다.
 *
 * as? 연산자는 어떤 값을 지정한 타입으로 변환한다. as? 연산자는 값을 대상 타입으로 변환할 수 없으면 null을 반환한다.
 *
 * 안전한 캐스트를 사용할 때 일반적인 패턴은 캐스트를 수행한 뒤에 엘비스 연산자를 사용하는 것이다.
 * 이 패턴을 사용하면 파라미터로 받은 값이 원하는 타입인지 쉽게 검사하고 캐스트하는 것을 하나의 식으로 해결할 수 있다.
 */
class Person(val firstName: String, val lastName: String) {
    override fun equals(other: Any?): Boolean {
        val otherPerson = other as? Person ?: return false

        return otherPerson.firstName == firstName && otherPerson.lastName == lastName
    }

    override fun hashCode(): Int = firstName.hashCode() * 37 + lastName.hashCode()
}

fun main() {
    val p1 = Person("Dmitry", "Jemerov")
    val p2 = Person("Dmitry", "Jemerov")
    println(p1 == null) // == 연산자는 equals를 호출한다.
    println(p1 == p2)
    println(p1 === p2)
    println(p1.equals(42))
}