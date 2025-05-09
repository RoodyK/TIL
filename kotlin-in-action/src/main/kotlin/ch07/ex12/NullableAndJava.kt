package ch07.ex12

/**
 * 코틀린은 자바 상호운용성을 강조하는 언어인데, 자바 타입 시스템은 널 가능성을 지원하지 않는다. 코틀린과 자바가 조합될 때 어떤 일이 생기는지 확인한다.
 *
 * 자바 코드에도 애노테이션으로 표시된 널 가능성 정보가 있다. @Nullable String은 String? 과 같고, @NotNull String은 String 과 같다.
 *
 * 코틀린은 여러 널 가능성 애노테이션을 알아보는데, 널 가능성 애노테이션이 없는 경우 자바의 타입은 코틀린의 플랫폼 타입이 된다.
 */

/**
 * 플랫폼 타입은 코틀린이 널 관련 정보를 알 수 없는 타입을 말한다.
 * 그 타입을 널이 될 수 있는 타입으로 처리해도 되고 널이 될 수 없는 타입으로 처리해도 된다.
 * 이는 자바와 마찬가지로 플랫폼 타입에 대해 수행하는 모든 연산에 대한 책임이 개발자에게 있다는 뜻이다.
 * 코틀린 컴파일러는 플랫폼 타입의 값에 대해 널 안정성 검사를 중복 수행해도 아무런 경고를 표시하지 않는다.
 *
 * 어떤 플랫폼 타입의 값이 널이 될 수도 있음을 알고 있다면 그 값을 사용하기 전에 null인지 검사할 수 있다.
 * 어떤 플랫폼 타입의 값이 널이 아님을 알고 있다면 아무 null 검사 없이 그 값을 직접 사용해도 된다.
 *
 * (같은 패키지 Person.java 참조)
 * 코틀린은 자바 코드의 getName() 메서드가 null 을 반활할 지 알 수 없으므로 개발자가 이 코드를 직접 처리해야 한다.
 *
 * 자바 API를 다룰 때는 보통 라이브러리는 널 관련 애노테이션을 쓰지 않으므로 조심히 다뤄야 한다. 문서를 잘 살펴보고 null 반환을 한다면 추가 검사를 해야 한다.
 */
fun yellAt(person: Person) { // null 검사 없이 자바 클래스 접근하기
    println(person.name.uppercase() + "!!")
}

fun yellAtSafe(person: Person) { // null 검사를 통해 자바 클래스 접근하기
    println((person.name ?: "Anyone").uppercase() + "!!!")
}

/**
 * 코틀린이 왜 플랫폼 타입을 도입했는가?
 * 모든 자바 타입을 널이 될 수 있는 타입으로 다루면 더 안전하지 않을 수 있지만,
 * 컴파일러가 널 가능성을 판단하지 못하므로 결코 널이 될 수 없는 값에 대해서도 불필요한 null 검사가 들어간다.
 * 컬렉션의 제네릭 타입에서 리스트의 원소마다 널 검사를 수행하면 비용이 너무 커진다.
 * 코틀린 설꼐자들은 자바의 타입을 가져온 경우 프로그래머에게 그 타입을 처리할 책임을 부여하는 실용적인 접근 방법을 택했다.
 *
 *
 * 코틀린에서 플랫폼 타입을 선언할 수는 없다. 자바 코드에서 가져온 타입만 플랫폼 타입이 된다. IDE나 컴파일러 오류 메시지에서는 플랫폼 타입을 볼 수도 있다.
 * val person = Person("Mike")
 * val i: Int = person.name // Type mismatch.
 *
 * String! 타입은 코틀린 컴파일러와 IDE에서 자바 코드에서 온 플랫폼 타입을 표시하는 방법이다.
 * ! 표기는 String! 타입의 널 가능성에 대해 아무 정보도 없다는 사실을 강조할 뿐이다.
 *
 * 다시 말하지만 플랫폼 타입은 널이 될 수 있는 타입이나 널이 될 수 없는 타입 어느쪽이든 사용할 수 있다.
 * val s1: String? = person.name, val s2: String = person.name
 * 메서드를 호출할 때 처럼 이 경우에도 프로퍼티의 널 가능성을 제대로 알고 사용해야 한다. 예외가 발생할 수 있다.
 */

/**
 * 코틀린에서 자바 메서드를 오버라이드 할 때 그 메서드의 파라미터와 반환 타입을 널이 될 수 있는 타입으로 선언할지 널이 될 수 없는 타입으로 선언할지 결정해야 한다.
 */
class StringPrinter : StringProcessor { // 자바 인터페이스를 다른 여러 널 가능성의 파라미터로 구현하기
    override fun process(value: String) {
        println(value)
    }
}

class NullableStringPrinter : StringProcessor { // 자바 인터페이스를 다른 여러 널 가능성의 파라미터로 구현하기
    override fun process(value: String?) {
        if (value != null) {
            println(value)
        }
    }
}

/**
 * 자바 클래스나 인터페이스를 코틀린에서 구현할 경우 널 가능성을 제대로 처리하는 일이 중요하다.
 * 구현 메서드를 다른 코틀린 코드가 호출할 수 있으므로 코틀린 컴파일러는 널이 될 수 없는 타입으로 선언한 모든 파라미터에 대해 널이 아님을 검사하는 단언문을 만들어준다.
 * 자바 코드가 그 메서드에게 null 값을 넘기면 이 단언문을 발동돼 예외가 발생한다. 파라미터를 메서드 안에서 결코 사용하지 않더라도 이런 예외는 피할 수 없다.
 */

fun main() {
    // yellAt(Person(null)) // Null can not be a value of a non-null type String
    yellAt(Person("abc"))

    // null 값을 제대로 처리하므로 실행 시점에 예외 발생 x
    yellAtSafe(Person(null))
}