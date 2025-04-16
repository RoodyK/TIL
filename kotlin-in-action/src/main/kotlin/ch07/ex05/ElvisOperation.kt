package ch07.ex05

/**
 * 코틀린은 null 대신 사용할 기본값을 지정할 때 편리하게 사용가능한 엘비스(elvis) 연산자를 제공한다. ?: 형태로 사용한댜.
 * 엘비스 연산자는 값이 존재하면 그 값을 사용하고, 값이 없다면 ?: 연산자 후에 오는 값을 사용한다.
 */
fun greet(name: String?) {
    val recipient: String = name ?: "unnamed"
    println("Hello, $recipient!")
}

/**
 * 객체가 null인 경우 null을 반환하는 안전한 호출 연산자와 함께 엘비스 연산자를 사용해서 객체가 null인 경우에 대비한 값을 지정하는 경우
 */
fun strLenSafe(s: String?): Int = s?.length ?: 0

/**
 * 코틀린에서는 return, throw 등도 식이기 대문에 엘비스 연산자의 우측에 넣을 수 있어서 편하게 사용할 수 있다.
 * 이 경우 엘비스 연산자의 왼쪽 값이 null 이면 함수가 즉시 어떤 값을 반환하거나 예외를 던진다. 이런 패턴은 함수의 전제조건을 검사하는 경우 유용하다.
 */
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)

class Company(val name: String, val address: Address?)

class Person(val name: String, val company: Company?)

fun printShippingLabel(person: Person) {
    val address = person.company?.address ?: throw IllegalArgumentException("No address")

    with (address) { // address 값은 널이 아님
        println(streetAddress)
        println("$zipCode $city, $country")
    }
}

fun main() {
    println(strLenSafe("abc"))
    println(strLenSafe(null))

    val address = Address("Elsestr. 47", 80687, "Munich", "Germany")
    val jetbrains = Company("JetBrains", address)
    val person = Person("Dmitry", jetbrains)

    printShippingLabel(person)
    printShippingLabel(Person("Alexey", null))
}