package ch07.ex11

/**
 * 물음표가 붙어있지 않는 타입 파라미터가 널이 될 수 있는 타입임을 확인
 *
 * 코틀린에서 함수나 클래스의 모든 타입 파라미터는 기본적으로 null이 될 수 있다. 널이 될 수 있는 타입을 포함하는 어떤 타입이라도 타입 파라미터를 대신할 수 있다.
 * 타입 파라미터 T를 클래스나 함수 안에서 타입 이름으로 사용하면 물음표가 없어도 T는 널이 될 수 있는 타입이다.
 */
fun <T> printHashCode(t: T) {
    println(t?.hashCode()) // t는 널일 수 있으므로 안전한 호출을 해야 함
}

/**
 * 타입 파라미터가 널이 아님을 확실히 하려면 널이 될 수 없는 타입 상계(upper bound)를 지정해야 한다. 이러면 널이 될 수 있는 값을 거부하게 된다.
 *
 * 타입 파라미터는 널이 될 수 있는 타입을 표시하려면 반드시 물음표를 타입 이름 뒤에 붙여야 한다는 규칙의 유일한 예외라는 점을 기억하라
 */
fun <T : Any> printHashCode2(t: T) {
    print(t.hashCode())
}

fun main() {
    printHashCode(null) // T의 타입은 Any? 로 추론된다.

    // printHashCode2(null) // Null can not be a value of a non-null type TypeVariable(T)
    printHashCode2(42)
}