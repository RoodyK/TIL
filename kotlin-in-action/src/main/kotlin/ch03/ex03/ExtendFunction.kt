package ch03.ex03

/**
 * 확장 함수와 확장 프로퍼티
 *
 * 확장 함수
 *
 * 코틀린의 핵심 목표중 하나는 기존 코드와 코틀린 코드를 자연스럽게 통합하는 것이다.
 * 코틀린은 자바 라이브러리를 기반으로 만들어진다.
 * 코틀린을 기존 자바 프로젝트에 통합하는 경우 코틀리능로 직접 변환할 수 없거나 미처 변환하지 못한 기존 자바코드를 처리할 수 있어야 한다.
 * 이 역할을 확장 함수가 해줄 수 있다.
 *
 * 확장 함수란 어떤 클래스의 멤버 메서드인 것처럼 호출할 수 있지만 그 클래스 밖에 선언된 함수다.
 *
 * 확장 함수는 자바, 그루비, 코틀린 등 어떤 언어로 쓰인 경우에도 정의할 수 있고, final로 선언된 상속 불가능한 경우도 가능하다.
 * 자바 클래스로 컴파일된 클래스 파일이 있는 한 그 클래스에 원하는 대로 확장을 추가할 수 있다.
 * 확장 함수에서는 클래스 안에서 정의된 메서드와는 달리 private, protected 멤버를 사용할 수 없다. 확장 함수는 이 함수가 확장 함수인지, 멤버 메서드인지 알 수 없다.
 *
 * 확장함수도 import를 통해서 사용한다.
 * 확장함수를 import해서 사용할 때 여러 같은 이름의 충돌을 방지하기 위해서는 함수를 다른 이름으로 지정하면된다.
 * 함수 이름은 as키워드를 사용해서 지정하며, 코틀린 문법상 짧은 이름을 써야한다.
 * import package.path.lastChar as last
 *
 * 코틀린을 확장 함수를 정적으로 결정하기 떼문에 오버라이드 할 수 없다. 확장 함수는 클래스의 일부가 아니다.
 * 확장 함수는 정적 자바 메서드로 컴파일한다는 사실을 기억하면 동작을 이해하는데 도움이 될 것이다. 자바도 마찬가지로 호출 시 static 함수를 결정한다.
 *
 * 어떤 클래스를 확장한 함수와 그 클래스의 멤버 함수의 이름과 시그니처가 같다면 확장 함수가 아닌 멤버 함수가 호출된다.(멤버 함수 우선 순위가 더 높다.)
 *
 *
 * 확장 프로퍼티
 *
 * 확장 프로퍼티는 기존 자바 객체의 인스턴스에 필드를 추가할 방법은 없기 때문에 실제 확장 프로퍼티는 아무 상태도 가질 수 없다.
 * 그렇기 때문에 커스텀 접근자를 지정해서 사용해야 한다.
 * 확장 함수의 경우와 마찬가지로 확장 프로퍼티도 단지 프로퍼티 수신에 객체 클래스가 추가됐을 뿐이다.
 * 뒷팓침하는 필드가 없어 기본 게터 구현을 제공할 수 없으므로 최소한 게터는 꼭 정의해야 한다.
 */

/**
 * 확잠 함수를 만들기 위해 해야할 일은 추가 함수 이름 앞에 그 함수가 확장할 클래스의 이름을 덧붙여는 것 뿐이다.
 * 확장 함수 선언에서 확장이 정의될 클래스의 타입을 수신 객체 타입이라고 하고, 호출하는 대상값을 수신 객체라고 한다. 수신 객체는 그 타입의 인스턴스 객체다.
 * 수신객체를 통해 확장 중인 타입의 메서드와 프로퍼티에 접근할 수 있다.
 *
 * 아래 함수에서 String이 수신 객체 타입, this.get과 this.length가 수신 객체이다. this는 생략 가능하다.
 *
 * 코틀린 함수를 자바에서 호출하려면 정적 메서드로 생성되므로 코틀린파일명.메서드(ExtendFunctionKt.lastChar)로 사용하면 된다.
 */
fun String.lastChar(): Char = this.get(this.length -1) // 마지막 문자를 반환

// joinToString 확장으로 정의
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "(",
    postfix: String = ")"
): String {

    val result = StringBuilder(prefix)

    for ((index, element) in this.withIndex()) {
        if (index  > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

/**
 * 멤버 함수 오버라이드
 */
open class View { // 하위 클래스 허용
    open fun click() = println("View Click") // 오버라이드를 위해 함수에 open을 추가해야 한다.
}

class Button: View() {
    override fun click() = println("Override View Click")
}

/**
 * 확장 프로퍼티
 */
val String.lastChar: Char
    get() = this.get(length -1)


fun main() {
    // 확장 함수 호출은 일반 함수 호출과 같다.
    println("Hello, Kotlin".lastChar())
    println("Hello, Kotlin".lastChar)

    val list = listOf(1, 2, 3, 4)

    println(list.joinToString())
}

