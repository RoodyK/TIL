package ch07.ex10

/**
 * null 값을 다루는 새로운 도구를 코틀린에서 추가하는 방법
 *
 * 널이 될 수 있는 타입에 대한 확장 함수를 정의하면 null 값을 다루는 강력한 도구로 활용할 수 있다.
 * 어떤 메서드를 호출하기 전 수신 객체 역할을 하는 변수가 null이 될 수 없다고 보장하는 대신, 메서드 호출이 null을 수신 객체로 받고 내부에서 null을 처리하게 할 수 있다.
 * 이런 처리는 확장 함수에서만 가능하며, 일반 멤버 호출은 객체 인스턴스를 통해 디스패치;되므로 그 인스턴스가 null인지 여부를 검사하지 않는다.
 */
fun verifyUserInput(input: String?) {
    // input은 널이 될 수 있는 값, isNullOrBlank() 메서드는 널이 될 수 있는 타입의 확장 함수,
    if (input.isNullOrBlank()) { // 안전한 호출을 사용하지 않는다. input?.some ?: "default"
        println("Please fill in the required fields")
    }
}

/**
 * 안전한 호출 없이도 널이 될 수 있는 수신 객체 타입에 대해 선언된 확장 함수를 호출 가능하다. 함수는 null 값이 들어오는 경우 이를 적절히 처리한다.
 *
 * isNullOrBlank() 함수는 널을 명시적으로 검사해서 null일 경우 true를 반환하고, null이 아닌 경우 isBlank를 호출한다.
 */
fun String?.isNullOrBlank(): Boolean = this == null || this.isBlank()

/**
 * 널이 될 수 있는 타입에 대한 확장을 정의하면 널이 될 수 있는 값에 대해 그 확장 함수를 호출할 수 있다.
 * 그 함수의 내부에서 this는 null이 될 수 있으므로 명시적으로 nulㅣ 여부 검사를 해야한다.
 * 코틀린에서는 널이 될 수 있는 타입의 확장 함수 안에는 this가 null이 될 수 있다는 것이 자바와 다르다. 자바의 메서드 안의 this는 수신 객체를 가리키므로 null이 아니다.
 *
 * let 함수도 널이 될 수 있는 타입의 값에 호출할 수 있지만, this가 null인지 검사하지 않는다.
 * 널이 될 수 있는 타입의 값에 대해 안전한 호출을 사용하지 않고 let을 호출하면 람다의 인자는 널이 될 수 있는 타입으로 추론한다. null 여부와 관계없이 람다가 호출된다는 의미다.
 */
fun sendEmailTo(email: String) {
    println("Sending email to $email")
}

/**
 * 직접 확장 함수를 작성한다면 그 확장 함수를 널이 될 수 있는 타입에 대해 정의할지 여부를 고민할 필요가 있다. 처음에는 널이 될 수 없는 타입에 대한 확장 함수를 정의하라.
 * 이후 null을 제대로 처리하게 되면 널이 될 수 있는 타입에 대한 확장함수로 바꿀 수 있다.
 *
 * s.isNullOrBlank() 메서드처럼 추가 검사 없이 변수를 참조한다고 해서 s가 널이 될 수 없는 타입이 되는 것이 아니며,
 * s.isNullOrBlank() 메서드가 널이 될 수 있는 타입의 확장 함수라면 s가 널이 될 수 있는 타입일 수도 있다.
 */

fun main() {
    verifyUserInput("")
    verifyUserInput(null)

    val recipient: String? = null
    // 안전한 호출을 하지 않기 때문에 it는 널이 될 수 있는 타입으로 취급된다.
    // recipient.let { sendEmailTo(it) } // Type mismatch.

    recipient?.let { sendEmailTo(it) }

}