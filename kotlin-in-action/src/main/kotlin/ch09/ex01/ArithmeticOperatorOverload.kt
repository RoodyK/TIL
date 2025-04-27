package ch09.ex01

import java.math.BigDecimal

/**
 * 코틀린에서 관례를 사용하는 가장 단순한 예는 산술 연산자다. 자바는 기본 타입에 대해서만 + 연산자를 사용할 수 있고, String 값에 대해 + 연산자를 사용할 수 있다.
 * 다른 클래스도 유용한 경우가 있는데 BigInteger 클래스도 add 메서드의 명시적 호출보단 + 연산을 사용하는 편이 낫고, 컬렉션의 원소 추가도 + 연산자를 사용할 수 있으면 좋다.
 */

/**
 * plus, times, divide 등: 이항 산술 연산 오버로딩
 *
 * plus 이름의 함수를 정의하면 + 로 두 객체를 더할 수 있다. 연산자를 오버로딩하는 함수 앞에는 반드시 operator가 있어야 한다.
 * operator 키워드로 어떤 함수가 관례를 따르는 함수임을 명확히 할 수 있고, 실수로 관례에서 사용하는 함수 이름을 사용하는 경우를 막아준다.
 *
 * 코틀린의 operator 변경자는 특정 연산자를 함수로 오버로드할 수 있게 해주는 기능이다.
 * 이를 통해 사용자 정의 타입에 대해 자연스럽고 직관적인 연산자 사용이 가능하게 된다.
 * 예를 들어, +, -, *, / 같은 산술 연산자뿐만 아니라, 비교 연산자, 인덱스 연산자, 호출 연산자 등 다양한 연산자를 오버로드할 수 있다.
 *
 * operator 변경자를 사용하는 이유
 * - 가독성 향상: 연산자 오버로드를 통해 복잡한 함수 호출 대신 자연스러운 연산자 표기를 사용할 수 있어 코드의 가독성을 높인다.
 * - 직관성 제공: 사용자 정의 타입이 내장 타입처럼 동작하는 것처럼 보여지게 하여, 사용자가 더 쉽게 이해하고 사용할 수 있게 만든다.
 * - 코드 간결화: 반복적이거나 복잡한 함수 호출을 연산자로 대체하여 코드의 간결성을 유지한다.
 *
 * 언제 사용하는가?
 * - 사용자 정의 데이터 타입에 대해 자연스럽고 직관적인 연산자 동작이 필요할 때
 * - 특정 연산이 의미하는 바를 명확하게 표현하고 싶을 때
 * - 기존 연산자를 오버로드하여 사용자 정의 타입 간의 연산을 쉽게 수행하고 싶을 때
 */
data class Point(val x: Int, val y: Int) { // plus 연산자 구현
    operator fun plus(other: Point): Point { // plus 이름의 연산자 함수를 정의
        return Point(x + other.x, y + other.y)
    }
}

/**
 * 연산자를 멤버 함수로 만드는 대신 확장 함수로 정의할 수도 있다.
 * 외부 함수의 클래스에 대한 연산자를 정의할 때는 관례를 따르는 이름의 확장 함수로 구현하는 것이 일반적인 패턴이다.
 */
data class Point2(val x: Int, val y: Int)
operator fun Point2.plus(other: Point2): Point2 {
    return Point2(x + other.x, y + other.y)
}

/**
 * 두 피연산자 사이에 함수를 호출하고 싶은 경우(예: a myOp b)에 대해 코틀린은 중위 함수를 제공한다.
 * 중위 함수는 커스텀 연산자의 장점(함수의 양변에 피연산자를 둘 수 있음)을 제공하면서 기억하기 힘든 임의의 기호의 조합으로 이름을 붙였을 때 커스텀 연산자가 줄 수 있는 코통을 덜어준다.
 *
 * 코틀린은 개발자가 직접 연산자를 만들어 사용할 수 없고 언어에서 미리 정해둔 연산자만 오버로딩할 수 있으며, 관례에 따르기 위해 클래스에서 정의해야 하는 이름이 연산자별로 정해져 있다.
 * 식: a * b => 함수명: times
 * 식: a / b => 함수명: div
 * 식: a % b => 함수명: mod
 * 식: a + b => 함수명: plus
 * 식: a - b => 함수명: minus
 *
 * 연산자를 정의할 때 두 피 연산자가 같은 타입일 필요는 없다
 * 코틀린 연산자가 자동으로 교환법칙(a op b == b op a)을 지원하지 않음을 유의하자. 이를 성립하려면 operator fun Double.times(p: Point) 하나 더 정의해야 한다.
 */
operator fun Point.times(scale: Double): Point {
    return Point((x * scale).toInt(), (y * scale).toInt())
}

/**
 * 연산자 함수의 반환 타입이 꼭 두 피연산자 중 하나와 일치하지 않아도 된다.
 */
operator fun Char.times(count: Int): String {
    return toString().repeat(count)
}

/**
 * 일반 함수와 마찬가지로 operator 함수도 오버로딩할 수 있다.
 *
 * 코틀린은 표준 숫자 타입에 대해 비트 연산자를 정의하지 않고 커스텀 타입에서 비트 연산자를 정의할 수 없다.
 * 대신 중위 연산자 표기법을 지원하는 일반 함수를 사용해 비트 연산을 수행한다.
 * shl: 왼쪽 시프트(<<)
 * shr: 오른쪽 시프트(>>)
 * ushr: 오른쪽 시프트(0으로 부호 비트 설정. 자바 >>>)
 * and: 비트 곱(&)
 * or: 비트 합(|)
 * xor: 비트 배타합(^)
 * inv: 비트 반전(자바 ~)
 */

/**
 * 연산을 적용한 다음에 그 결과를 바로 대입: 복합 대입 션산자 오버로딩
 *
 * 코틀린은 plus 같은 연산자를 오버로딩하면 + 연산자 뿐만 아니라 +=도 자동으로 함께 지원한다. +=, -= 등의 연산자륿 복합 대입 연산자라 한다.
 * var point = Point(1, 2)
 * point += Point(3, 4)
 *
 * 경우에 따라 += 연산이 객체에 대한 참조를 다른 참조로 바꾸기보다 원래 객체의 내부 상태를 변경하게 만들고 싶을 때 가 있다.
 * val numbers = mutableListOf<Int>()
 * numbers += 42
 * 반환 타입이 Unit인 plusAssign(minusAssign, timesAssign 등) 함수를 정의하면서 operator로 표시하면 코틀린은 += 연산자에 그 함수를 사용한다.
 */
operator fun <T> MutableCollection<T>.plusAssign(element: T) {
    this.add(element)
}

/**
 * 이론적으로 코드에 있는 += 연산을, plus, plusAssign 양쪽으로 컴파일 할 수 있으며, 어떤 클래스가 이 두 함수를 모두 정의 후 += 연산에 사용 가능한 경우 컴파일러는 오류를 보고한다.
 * plus, plusAssign 연산을 동시에 정의하지 말라. 변경 불가능하다면 새로운 값을 반환하는 plus, 빌더와 같이 변경 가능한 클래스를 설계한다면 plusAssign 혹은 비슷한 연산을 제공하라.
 *
 * 코틀린 표준 라이브러리는 컬렉션에 대해 2가지 접근 방법을 제공한다.
 * +, -는 항상 새로운 컬렉션을 반환하며, +=, -= 연산자는 항상 변경 가능한 컬렉션에 작용해 메모리에 있는 객체 상태를 변화시킨다.
 * 또한 읽기 전용 컬렉션에서 +=, -=은 변경을 적용한 복사본을 반환한다. 따라서 var로 선언한 변수가 가리키는 읽기 전용 컬렉션에서만 +=, -=을 적용할 수 있다.
 * 이런 연산자의 피연산자로는 개별 원소를 사용하거나 원소 타입이 일치하는 달느 컬렉션을 사용할 수 있다.
 * val list = mutableListOf(1, 2)
 * list += 3 // +=는 list를 변경함
 * val newList = list + listOf(4, 5) // +는 두 리스트의 모든 원소를 포함하는 새로운 리스트를 반환
 */

/**
 * 피 연산자가 1개뿐인 연산자: 단항 연산자 오버로딩
 *
 * 단항 연산자를 오버로딩하는 절차도 이항 연산자와 마찬가지로, 미리 정해진 이름의 함수를 (멤버나 확장함수로)선언하면서 operator로 표시하면 된다.
 * val point7 = Point(10, 20)
 * println(-point7)
 *
 * 단항 연산자를 오버로딩하기 위해 사용하는 함수는 인자를 취하지 않는다. 다음은 오버로딩할 수 있는 단항 산술 연선자다.
 * 식        | 함수이름
 * +a       | unaryPlus
 * -a       | unaryMinus
 * !a       | not
 * ++a, a++ | inc
 * --a, a-- | dec
 */
operator fun Point.unaryMinus(): Point {
    return Point(-x, -y)
}

/**
 * inc, dec 함수를 정의해 증가/감소 연산자를 오버로딩하는 경우 컴파일러는 일반적인 값에 대한 전위와 후위 증가/감소 연산자와 같은 의미를 제공한다.
 */
operator fun BigDecimal.inc() = this + BigDecimal.ONE

fun main() {
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    println(p1 + p2) // + 기호를 사용하면 plus 함수가 호출됨

    val p3 = Point2(10, 20)
    val p4 = Point2(30, 40)
    println(p3 + p4) // + 기호를 사용하면 plus 함수가 호출됨

    val p5 = Point(10, 20)
    println(p5 * 1.5)

    println('a' * 3)

    println(0x0F and 0xF0)
    println(0x0F or 0xF0)
    println(0x1 shl 4)

    var point6 = Point(1, 2)
    point6 += Point(3, 4)
    println(point6)

    val list = mutableListOf(1, 2)
    list += 3 // +=는 list를 변경함
    val newList = list + listOf(4, 5) // +는 두 리스트의 모든 원소를 포함하는 새로운 리스트를 반환
    println(list)
    println(newList)

    val point7 = Point(10, 20)
    println(-point7)

    var bd = BigDecimal.ZERO
    println(bd++) // 후위 증가 연선자는 println 실행 후 값 증가
    println(bd)
    println(++bd) // 전위 증가 연산자는 println 실행 전 값 증가
}