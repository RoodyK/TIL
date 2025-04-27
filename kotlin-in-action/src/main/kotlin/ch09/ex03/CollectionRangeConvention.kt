package ch09.ex03

import java.time.LocalDate

data class Point(val x: Int, val y: Int)

/**
 * 컬렉션을 다룰 때 가장 많이 쓰는 연산은 인덱스를 사용해 원소를 읽거나 쓰는 연산과 어떤 값이 컬렉션에 속해 있는지 검사하는 연산이다. 이 모든 연산을 연산자 구문으로 사용할 수 있다.
 * 인덱스를 사용해 원소를 설정하거나 가져오고싶을 때는 a[b]라는 식을 사용한다.(인덱스 접근 연산자라고 함)
 * in 연산자는 원소가 컬렉션이나 범위에 속하는지 검사하거나 컬렉션에 있는 원소를 이터레이션할 때 사용한다.
 * 개발자는 자신의 클래스에 이런 연산을 추가할 수 있다.
 */

/**
 * 인덱스로 원소 접근: get, set
 *
 * 코틀린에서 맵에 접근할 때 자바 배열 원소에 접근할 때 처럼 [] 를 사용한다.
 * 조회: val value = map["key"]
 * 변경: mutableMap["key"] = newValue
 *
 * 코틀린에서는 인덱스 접근 연산자도 관례를 따른다.
 * 인덱스 접근 연산자를 사용해 원소를 읽는 연산은 get 연산자 메서드로, 원소를 쓰는 연산은 set 연산자 메서드로 변환된다. Map, MutableMap은 이미 들어있다.
 *
 * get 관례를 구현하기 위해 get 메서드를 만들고 operator 변경자를 붙이기만 하면 된다.
 * get 메서드의 파라미터로는 Int가 아닌 타입도 사용할 수 있다. Map의 경우 Map의 Key 타입과 같은 임의의 타입이 된다.
 *
 * 2차원 행렬이나 배열을 표현하는 클래스에 operator fun get(rowIndex: Int, colIndex: Int)를 정의하면 matrix[row, col]로 그 메서드를 호출할 수 있다.
 *
 * 컬렉션 클래스가 다양한 키 타입을 지원해야 한다면 다양한 파라미터 타입에 대해 오버로딩한 get 메서드를 여럿 정의할 수도 있다.
 */
operator fun Point.get(index: Int): Int { // get 관례 구현
    return when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

/**
 * 비슷하게 인덱스에 해당하는 컬렉션 원소를 []를 사용해 쓰는 함수를 정의할 수도 있다.
 * 불변 클래스는 이런 메서드를 정의하는 것이 의미 없으므로 변경 가능한 점을 표현하는 다른 클래스를 생성해서 사용한다.
 *
 * 대입에 인덱스 연산자를 사용하려면 set이라는 이름의 함수를 정의해야 한다.
 */
data class MutablePoint(var x: Int, var y: Int)

operator fun MutablePoint.set(index: Int, value: Int) {
    when (index) {
        0 -> x = value
        1 -> y = value
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

/**
 * 어떤 객체가 컬렉션에 들어있는지 검사: in 관례
 *
 * in은 객체가 컬렉션에 들어있는지 검사한다. 이때 대응하는 함수는 contains다.
 * 어떤 점이 사각형의 영역에 들어갈지 판단하는 예제를 구현해서 in 연산자를 사용한다
 *
 * in 오른쪽에 있는 객체는 contains 메서드의 수신 객체가 되고 in 왼쪽에 있는 객체는 contains 메서드에 인자로 전달된다.
 *
 * ..< 연산자를 사용해 열린 범위(open range)를 만들고, in 연산자를 사용해 점이 속하는지 검사한다.
 * 열린 범위는 끝 값을 포함하지 않는 범위를 말한다. 예를들어 10..20이라는 식을 사용해 일반적인 (닫힌) 범위를 만들면 10 이상 20 이하인 범위가 생긴다.
 * 10 until 20으로 만드는 열린 범위는 10 이상 19 이하인 범위이다.
 * Rectangle 클래스의 경우 오른쪽과 아래쪽 좌표는 사각형 안에 포함시키지 않는 경우가 많아서 열린 범위를 사용하는 것이 좋다.
 */
data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point): Boolean {
    // 범위를 만들고 x 좌표가 그 범위 안에 있는지 검사한다.
    return p.x in upperLeft.x..<lowerRight.x && p.y in upperLeft.y..<lowerRight.y // ..< 연산자로 열린 범위를 만든다.
}

/**
 * 객체로부터 범위 만들기: rangeTo와 rangeUntil 관례
 *
 * 범위를 만드려면 .. 구문을 사용해야 한다. (1..10 => 1부터 10까지 모든 수가 들어있는 범위)
 * .. 연산자는 rangeTo 함수 호출을 간랸하게 표현하는 방법으로 ..은 rangeTo 함수로 컴파일된다.. start..end = start.rangeTo(end)
 * rangeTo 함수는 범위를 반환한다. Comparable 인터페이스를 구현하면 rangeTo를 정의할 필요가 없다.
 * 모든 코틀린 표준 라이브러리에는 모든 Comparable 객체에 대해 적용 가능한 rangeTo 함수가 들어있다.
 * operator fun <T: Comparable<T>> T.rangeTo(that: T): ClosedRange<T>
 * 이 함수는 범위를 반환하며 어떤 원소가 그 범위 안에 들어있는지 검사할 수 있게 해준다.
 *
 * rangeTo 연산자는 다른 산술 연산자보다 우선순위가 낮지만 혼동을 피하기 위해 괄호로 인자를 감싸주면 더 좋다.
 * val n = 9
 * println(0..(n+1))
 */

/**
 * 자신의 타입에 대해 루프 수행: iterator 관례
 *
 * for 루프에서도 in 연산자를 사용하는데 이 경우 in의 의미는 다르다.
 * for (x in list) { ... } 같은 문장은 list.iterator()를 호출해서 이터레이터를 얻은 다음 자바처럼 그 이터레이터에 대해 hasNext와 next 호출을 반복하는 식으로 변환된다.
 *
 * 코틀린에서는 이 또한 관례이므로 iterator 메서드를 확장 함수로 정의할 수 있다.
 * 이런 성질로 인해 일반 자바 문자열에 대한 for 루프가 가능하다.
 * 코틀린 표준 라이브러리는 Strign의 상위 클래스인 CharSequence에 대한 iterator 확장 함수를 제공한다.
 * operator fun CharSequence.iterator(): CharIterator // 이 라이브러리 함수는 문자열을 이터레이션할 수 있게 해준다.
 *
 * 클래스 안이나 서드파티 클래스에 대해 iterator 메서드를 직접 구현할 수 있다.
 * LocalDate 객체에 대한 이터레이션을 가능하게 하는 확장 함수를 정의할 수 있다.
 * iterator 함수가 Iterator<LocalDate> 인터페이스를 구현하는 객체를 반환해야 하므로 hasNext와 next 함수 구현을 지정한 객체 선언을 사용한다. (4.4.1 참조)
 *
 * rangeTo 함수는 ClosedRange의 인스턴스를 반환한다.
 * 코드에서 ClosedRange<LocalDate>에 대한 확장 함수 iterator를 정의했기 때문에 LocalDate의 범위 객체를 for 루프에 사용할 수 있다.
 */
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> {
    val obj = object: Iterator<LocalDate> { // 이 객체는 LocalDate 원소에 대한 Iterator 구현
        var current = start
        override fun hasNext(): Boolean = current <= endInclusive // compareTo 관례를 사용해 날짜를 비교한다.
        override fun next(): LocalDate {
            val thisDate = current
            current = current.plusDays(1)
            return thisDate // 현재 날짜의 1일 뒤를 반환
        }
    }

    return obj
}


fun main() {
    val map = mapOf(Pair("a", 1), Pair("b", 2))
    println(map["a"])

    println()
    val p = Point(10, 20)
    println(p[1])

    println()
    val p2 = MutablePoint(10, 20)
    p2[1] = 42
    println(p2)

    // rangeTo, rangeUntil
    println()
    println("======rangeTo, rangeUntil======")
    val now = LocalDate.now()
    val vacation = now..now.plusDays(10)
    println(vacation)
    println(now.plusWeeks(1))
    println(now.plusWeeks(1) in vacation)

    val n = 9
    println(0..(n+1))
    (0..n).forEach{ print(it) }
    (0..<n).forEach{ print(it) }

    println()
    println("======iterator======")
    for (c in "abc") {
        print(c)
    }

    println()
    val newYear = LocalDate.ofYearDay(2042, 1)
    val daysOff = newYear.minusDays(1)..newYear
    for (dayOff in daysOff) { println(dayOff) }
}