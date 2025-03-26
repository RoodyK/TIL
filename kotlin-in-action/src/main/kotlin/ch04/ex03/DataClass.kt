package ch04.ex03

/**
 * first
 * 코틀린은 자바에서 equals(), hashcode(), toString() 같은 기계적으로 생성하는 작업을 보이지 않는 곳에서 처리해 소스코드를 깔끔하게 유지할 수 있다.
 *
 * 코틀린도 자바처럼 필요한 경우 equals(), hashcode(), toString() 을 직접 오버라이드 해야한다.
 * toString(): 객체의 출력값을 주소값이 아닌 커스텀 문자열을 출력
 * equals(): 객체의 주소로 비교하는 것이 아닌 두 객체 내부 프로퍼티의 값이 동일하다면 동등한 객체로 표현
 * - 코틀린에서는 == 연산자를 사용하면 내부적으로 equals를 호출해서 객체를 비교한다. 참조 비교를 위해서는 === 연산자를 사용한다.
 * hashcode(): hashFunction 을 통해서 나온 값이 동일한 값을 보장하려면 equals()를 오버라이딩해서 값이 같은 객체의 동등성을 보장했다면 해시코드도 같게 만들어야 한다.
 */
class Customer(val name: String, val postalCode: Int) {
    // Any는 코틀린의 모든 클래스의 최상위 클래스로, Any?는 널이 될 수 잇는 타입이므로 other는 null일 수 있다.
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Customer) {
            return false
        }
        return name == other.name && postalCode == other.postalCode
    }

    override fun hashCode(): Int = name.hashCode() * 31 + postalCode

    // 편의 메서드 copy() 직접 구현했을 떄
    fun copy(name: String = this.name, postalCode: Int = this.postalCode) = Customer(name, postalCode)

    override fun toString() = "Customer(name=$name, postalCode=$postalCode)"
}

/**
 * 클래스 앞에 data 변경자를 붙이면 필요한 메서드를 컴파일러가 자동으로 만들어준다. 이를 데이터 클래스라고 한다.
 * equals, hashcode 메서드는 주 생성자에 나열된 프로퍼티를 고려해 만들어진다. 주 생성자 밖의 프로퍼티는 고려되지 않는다.
 * data 클래스는 그 외에도 몇가지 유용한 메서드를 더 생성해준다. 데이터 클래스는 불변 클래스를 만들기를 권장한다.
 *
 * copy()
 * - 데이터 클래스 인스턴스를 불변 객체로 쉽게 활용하도록 코틀린 컴파일러가 제공하는 편의 메서드
 * - 객체를 복사하면서 일부 프로퍼티를 바꿀 수 있게 해준다.
 * - 복사본은 원본과 다른 생명주기를 가지며 복사를 하면서 일부 프로퍼티 값을 바꾸거나 복사본은 원본에 영향을 미치지 않는다.
 */
data class Customer2(val name: String, val postalCode: Int)

/**
 * 코틀린 data class, 자바 Record 차이점
 * - equals(), hashcode(), toString() 는 둘 다 제공하지만, 레코드는 copy()와 같은 편의 메서드는 없다.
 *
 * 자바 레코드는 좀더 많은 제약이 존재한다.
 * - 모든 프로퍼티가 private이며 final이어야 한다.
 * - 레코드는 상위 클래스를 확장할 수 없다.
 * - 클래스 본문 안에서 다른 프로퍼티를 정의할 수 없다.
 *
 * 코틀린은 상호운영성을 위해 data class에 @JvmRecord 애노테이션을 통해 레코드를 선언할 수 있다. 이 때 레코드와 같은 제약 사항을 지켜야 한다.
 */

fun main() {
    val customer1 = Customer("Bob", 1234)
    val customer2 = Customer("Bob", 1234)
    println(customer1 == customer2)

    val hashSet = hashSetOf(Customer("Bob", 1234))
    println(hashSet.contains(Customer("Bob", 1234)))

    val john = Customer2("John", 1234)
    val copyJohn = john.copy(postalCode = 4321)
    println(copyJohn)
}