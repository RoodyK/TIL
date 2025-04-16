package ch07.ex02

/**
 * 코틀린과 자바의 가장 중요한 차이는 코틀린 타입 시스템이 널이 될 수 있는 타입을 명시적으로 지원한다는 점이다.
 * 널이 될 수 있는 타입은 프로그램 안의 프로퍼티나 변수에 null 값을 허용하게 만드는 방법이다.
 * 어떤 변수가 null이 될 수 있다면 그 변수에 대해 메서드를 호출하면 NullPointerException이 발생할 수 있으므로 안전하지 않다.
 * 코틀린은 그런 메서드 호출을 금지함으로써 많은 예외를 방지한다.
 */

/**
 * 자바에서 널 위험이 있는 코드 int strLen(String str) { return s.length(); }
 * 코틀린에서 이런 함수를 작성할 때 "이 함수가 null을 인자로 받을 수 있는가?"를 알아야 한다.
 * 코틀린에서는 null이 될 수 있는 인자를 넘기는 것은 금지되며 그런 값을 넘기면 컴파일 에러가 발생한다.
 * 컴파일러는 null이 될 수 있는 값을 인자로 넘기지 못하게 막으므로 NullPointerException이 발생하지 않음을 장담할 수 있다.
 */
fun strLen(s: String) = s.length

/**
 * 함수가 null을 포함한 모든 문자열을 인자로 ㅂ다을 수 있게 하려면 타입 이름 뒤에 물음표(?)를 명시해야 한다.
 * 어떤 타입이든 타입 뒤에 물음표를 붙이면 그 타입의 변수나 프로퍼티에 null 참조를 저장할 수 있다.
 * 물음표가 없는 타입은 어떤 변수가 nulㅣ 참조를 저장할 수 없다는 의미로, 모든 타입은 기본적으로 null이 아닌 타입이다. 물음표를 명시적으로 붙여야 null이 될 수 있다.
 *
 * 많은 제약에서 널이 될 수 있는 타입의 값으로 할 수 있는 중요한 일은 null과 비교하는 것이다.
 * null과 비교하고 나면 컴파일러는 그 사실을 기억하고 null이 아님이 확실한 영역에서는 해당 값을 null이 아닌 타입의 값처럼 사용할 수 있다.
 *
 * 널 가능성을 다루기 위해 사용할 수 있는 도구가 if 뿐이라면 코드가 금방 번잡해지는데, 코틀린은 nullable한 값을 다루기 위한 여러 도구를 제공한다.
 */
fun strLenSafe(s: String?) =
    if (s != null) s.length // 널이 될 수 있는 값은 타입이 제공하는 메서드를 사용하려면 체크를 해야 한다.
    else 0

fun main() {
    // strLen(null)
    // Null can not be a value of a non-null type String

    val x: String? = null
    // var y: String = x // Type mismatch.

    val strLenSafe1 = strLenSafe(null)
    val strLenSafe2 = strLenSafe("abcde")
    println(strLenSafe1)
    println(strLenSafe2)
}