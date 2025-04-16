package ch07.ex04

/**
 * 코틀린이 제공하는 가장 유용한 도구로 안전한 호출 연산자 ?. 가 있다. ?. 는 null 검사와 메서드 호출을 한 연산으로 수행한다.
 * 호출하려는 값이 null이 아니라면 ?.는 일반 메서드 호출처럼 작동하고, 호출하려는 값이 null이면 메서드는 무시되고 결과값은 null이 된다.
 * str?.uppercase() 코드는 if(str != null) str.uppercase() else null 코드와 같다.
 * str?.uppercase() 코드의 경우 결과도 null이 될 수 있음을 주의해야 하며, 반환 타입은 String? 이 된다.
 */
fun printAllCaps(str: String?) {
    val allCaps: String? = str?.uppercase() // null일 수 있음
    println(allCaps)
}

/**
 * 널이 될 수 있는 manager라는 프로퍼티가 있는 간단한 Employee 클래스로 프로퍼티 접근 시 안전한 호출을 사용하는 방법
 */
class Employee(val name: String, val manager: Employee?)

fun managerName(employee: Employee): String? = employee.manager?.name

/**
 * 객체 그래프에서 널이 될 수 있는 중간 객체가 여럿 있다면 한 식 안에서 안전한 호출을 연쇄해서 함께 사용하면 편할 때가 자주 있다.
 *
 * 아래 코드는 마지막에 country를 null 검사하는 불필요한 반복이 들어가 있는데 코틀린은 이런 반복을 제거할 수 있는 방법을 제공한다.
 */
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)

class Company(val name: String, val address: Address?)

class Person(val name: String, val company: Company?)

fun Person.countryName(): String {
    val country = this.company?.address?.country // 안전한 호출 연산자를 여러 개 연쇄해 사용
    return if (country != null) country else "Unknown"
}

fun main() {
    printAllCaps("abc")
    printAllCaps(null)

    val ceo = Employee("Da Boss", null)
    val developer = Employee("Bob smith", ceo)
    println(managerName(developer))
    println(managerName(ceo))

    val person = Person("Dmitry", null)
    println(person.countryName())
}