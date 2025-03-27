package ch02

import ch02.Color.*
import java.io.BufferedReader
import java.io.Reader
import java.io.StringReader

/**
 * 함수를 선언할 때 fun 키워드를 사용한다.
 * 함수를 모든 코틀린 파일의 최상위 수준에 정의할 수 있으므로 클래스 안에 함수를 넣어야 할 필요는 없다.
 * 최상위에 있는 main 함수를 애플리케이션의 진입점으로 지정할 수 있음. 함수의 인자는 필수가 아니다. (있을 수도 있음)
 * 코틀린은 간결성을 강조한다.
 * 세미콜론은 필수가 아니다.
 */
fun main() {
    println("Hello, world!")

    val person = Person("kang", true) // new 키워드를 사용하지 않음
    // 코틀린에서는 set(), get()이 아닌 프로퍼티 구문을 직접 사용
    println(person.name)
    println(person.isAdult)

    println(getColorTone(Color.ORANGE))
    println(mix(RED, YELLOW))

    println(eval(Sum(Sum(Num(1), Num(2)), Num(4))))

//    iterationWithLabel()
//    iterationFor()
    iterationMap()
    println(isLetter('x'))
    println(isNotDigit('x'))
}

/**
 * 두 파라미터 중 큰 값을 반환하는 함수
 * if 식은 다른 언어의 삼항 연산차와 비슷하다.
 * 코틀린은 반복문을 제외한 대부분의 제어 구조(자바 제어문)가 문(statement)이 아닌 식이라는 점이 다른 언어와의 차이점이다.
 *
 * 문(statement)
 * - 자신이 둘러싸고 이쓴ㄴ 가낭 안쪽 블록의 최상위 요소로 존재하며 아무런 값을 만들어내지 않는다.
 * 식
 * - 값을 만들어내며 다른 식의 하위 요소로 계산에 참여할 수 있다.
 *
 * max2() 함수는 max() 함수를 더 간소화 했다.
 * max() 처럼 함수 본문이 중괄호로 둘러싸인 함수를 블록 본문 함수라고 한다.
 * max2()처럼 등호와 식으로 이루어진 함수를 식 본문 함수라고 한다.
 */
fun max(a: Int, b: Int): Int {
    return if (a > b) a else b
}

fun max2(a: Int, b: Int): Int = if (a > b) a else b

/**
 * 타입 추론
 * 함수 본문 식을 분석해서 식의 결과 타입을 함수 반환 타입으로 정해준다.
 * 식 본문 함수의 반환 타입만 생략 가능하다.
 */
fun max3(a: Int, b: Int) = if (a > b) a else b

/**
 * 변수 선언
 * val
 * - 읽기 전용 참조를 선언
 * - val 키워드로 선언한 변수는 값을 재할당할 수 없다.
 * var
 * - 재대입 가능한 참조를 선언
 * - 초기화가 이루어져도 값을 재할당할 수 있다.
 */
fun useVariable() {
    val str: String = "abc"
    val str2 = "abc"; // 컴파일러가 타입을 추론

    // 변수를 선언하고 즉시 초기화하지 않을 때는 컴파일러가 타입을 추론하지 못하므로 명시적으로 타입을 지정해야 함
    val answer: Int
    answer = 22;
}

/**
 * 자바에서 name 필드를 갖고 생성자로 필드값을 받으며 getter() 메서드로 name 필드에 접근하는 클래스를 코틀린으로는 간단히 표현 가능하다.
 * 코틀린의 기본 접근 제어자는 public으로 생략 가능하다.
 */
class Human(val name: String)

/**
 * 클래스란 데이터를 캡슐화하고 캡슐화한 데이터를 다루는 코드를 한 주체 안에 가두는 것
 * 자바에서 필드와 getter, setter와 같은 접근자를 프로퍼티라고 하는데 코틀린에서는 기본 제공되므로 생략 가능하다.
 */
class Person(
    val name: String, // 읽기 전용으로 필드(비공개)와 getter 메서드 생성
    var isAdult: Boolean // 쓰기 가능으로 필드(비공개)와 getter, setter 메서드 생성
)

/**
 * 커스텀 접근자 구현 (계산된 프로퍼티)
 * Kotlin에서 계산된 프로퍼티는 get() 또는 set() 메서드를 오버라이드하여 값의 반환이나 설정을 동적으로 처리하는 프로퍼티를 의미한다.
 * 이 프로퍼티는 필드에 직접 저장되지 않고, 대신 프로퍼티에 접근할 때마다 get() 메서드가 실행되어 값을 계산하거나 반환한다.
 * 마찬가지로, set() 메서드를 사용하여 값을 설정할 때 계산된 방식으로 설정할 수 있다.
 */
class Rectangle(val height: Int, val width: Int) {
    val isSquare: Boolean
        get() = height == width // 다른 함수와 마찬가지로 중괄호{}와 return 생략 가능
}

/**
 * enum
 * 자바에서 enum은 코틀린에서 enum class를 사용한다.
 */
enum class Color (
    val text: String
) {
    RED("빨강색"),
    ORANGE("주황색"),
    YELLOW("노랑색"),
    GREEN("초록색"),
    BLUE("파랑색"),
    INDIGO("남색"),
    VIOLET("보라색")
    ; // 메서드를 정의하는 경우 enum 상수 목록과 메서드 정의 사이에 세미콜론을 넣어야 함
}

/**
 * when도 값을 만들어내는 식이다.
 * 중괄호를 생략하고 값을 바로 반환할 수 있다.
 * 자바와 같이 static import 가능
 * when() 식의 대상 값을 변수에 넣을 수 있음 => when (val value = someMethod())
 */
fun getColorTone(color: Color): String {
    return when (color) {
        RED, Color.ORANGE, Color.YELLOW -> "Bright"
        Color.GREEN, Color.BLUE -> "Normal"
        Color.INDIGO, Color.VIOLET -> "Dark"
    }
}

fun getColorTone2(color: Color) =
    when (color) {
        RED, Color.ORANGE, Color.YELLOW -> "Bright"
        Color.GREEN, Color.BLUE -> "Normal"
        Color.INDIGO, Color.VIOLET -> "Dark"
    }

// 분기 조건에 다른 여러 객체 사용
fun mix(c1: Color, c2: Color) =
    // when 식의 인자로 아무 객체나 사용 가능. when은 인자로 받은 객체가 각 분기 조건에 있는 개체와 같은지 테스트한다.
    when (setOf(c1, c2)) {
        setOf(RED, YELLOW) -> ORANGE
        setOf(YELLOW, BLUE) -> GREEN
        setOf(BLUE, VIOLET) -> INDIGO
        else -> throw Exception("Dirty color")
    }

/**
 * 마커 인터페이스
 * - 여러 타입의 식 객체를 아우르는 공통 타입 역할만 수행하는 인터페이스
 *
 * 클래스가 구현하는 인터페이스를 지정하기 위해 콜론 뒤에 인터페이스 이름을 사용한다.
 *
 * 고려해야할 부분
 * - 어떤 식이 수라면 그에 해당하는 값을 반환한다.
 * - 어떤식이 합계라면 좌항 값을 재귀적으로 계산하고 우항 값도 재귀적으로 계한 후 두 값을 합한 값을 반환한다.
 *
 * 스마트 캐스트
 * - is 는 isInstance와 같지만 타입을 확인 후 그 타입을 변환하지 않아도 그 타입의 변수인 것처럼 사용할 수 있다. 이를 컴파일러가 대신 변환해준다.
 * - 클래스의 프로퍼티에 대해서 스마트 캐스트를 사용하려면 프로퍼티는 반드시 val 이어야 한다.
 */
interface Expr
// value 프로퍼티만 존재하는 단순한 클래스로 Expr 인터페이스를 구현한다.
class Num(val value: Int): Expr
// 어떤 Expr이나 Sum 연산의 안자가 될 수 있다. Num이나 다른 Sum이 인자가 될 수 있다.
class Sum(val left: Expr, val right: Expr): Expr // Sum(Sum(Num(1), Num(2)), Num(4)) 와 같은 형태가 됨

// if 사용
fun eval(e: Expr): Int {
    if (e is Num) {
        val n = e as Num // 명시적으로 Num 타입으로 변환. 조건식에서 스마트 캐스팅되므로 불필요한 중복이다.
        return n.value
    }
    if (e is Sum) {
        return eval(e.right) + eval(e.left) // 변수 e에 대해 스마트 캐스트를 진행
    }
    throw IllegalArgumentException("Unknown expression")
}

// 식처럼 사용
fun eval2(e: Expr): Int =
    if (e is Num) e.value
    else if (e is Sum) eval2(e.right) + eval2(e.left) // 변수 e에 대해 스마트 캐스트를 진행
    else throw IllegalArgumentException("Unknown expression")

// when 사용
fun eval3(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval3(e.right) + eval3(e.left)
        else -> throw IllegalArgumentException("Unknown expression")
    }

// 분기에서 블록 사용
fun evalWithLogging(e: Expr): Int =
    when (e) {
        is Num -> {
            println("num: ${e.value}")
            e.value
        }
        is Sum -> {
            val left = evalWithLogging(e.left)
            val right = evalWithLogging(e.right)
            println("sum: $left + $right")
            left + right
        }
        else -> throw IllegalArgumentException("Unknown expression")
    }

/**
 * 반복문
 * while문과 do-while 문은 자바와 다르지 않다.
 * - inner 반복문에서는 레이블을 지정해서 사용할 수 있다.
 *
 * 코틀린에서는 for 문은 범위를 사용한다. 어떤 범위에 속한 값을 일정한 순서로 반복하는 경우를 순열이라고 부른다.
 * - 범위를 쓸 때는 .. 연산자를 사용한다. for (i in 1..10)
 * - step을 사용해서 증가 값을 결정한다. for (i in 1..10 step 2)
 * - downTo를 사용해서 역방향 순열을 만들 수 있다. for (i in 10 downTo 1 step 2)
 *
 * in 연산자를 사용하면 어떤 값이 범위에 속하는지 검사할 수 있다.
 * !in 연산자를 사용하면 어떤값이 범위에 속하지 않는지 검사할 수 있다.
 */
fun iterationWhile() {
    var i: Int = 0
    while (i < 10) {
        println("i: $i")
        i++
    }
}

// 레이블을 사용하는 루프
fun iterationWithLabel() {
    var i: Int = 0
    outer@ while (true) {
        while (true) {
            i++
            println("iterationWithLabel i: $i")
            if (i < 3) continue
            if (i > 3) break@outer // 루프를 빠져나가 outer roof를 break 한다.
        }
    }
}

fun iterationFor() {
    for (i in 1..10 step 2) {
        println("ascending num $i")
    }

    for (i in 10 downTo 1 step 2) {
        println("descending num $i")
    }
    // 1~4까지 루프
    for (i in 1..<5) {
        println("num $i")
    }
}

// 리스트 iteration
fun iterationCollection() {
    val fruits = listOf("Apple", "Orange", "Cherry")
    for (fruit in fruits) {
        print("fruit: $fruit")
    }

    // 인덱스와 원소 출력
    for ((index, item) in fruits.withIndex()) {
        print("$index: $item")
    }

    println("Kotlin" in "Java".."Scala") // String 클래스의 Comparable 인터페이스로 문자열을 알파켓 순서로 비교 K는 J와 S의 사이이므로 true
    println("Kotlin" in setOf("Java", "Scala")) // false
}

// 맵을 초기화하고 iteration
fun iterationMap() {
    val binaryReps = mutableMapOf<Char, String>() // 코틀린의 가변 맵은 원소의 반복 순서를 보존
    for (char in 'A'..'F') { // A~F 문자의 범위를 사용
        val binary = char.code.toString(radix = 2) // 아스키 코드를 2진 표현으로 변경
        binaryReps[char] = binary // key value 설정
    }

    for ((key, value) in binaryReps) {
        println("$key = $value")
    }
}

// 컬렉션이나 범위의 원소 검사
fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'
fun isNotDigit(c: Char) = c !in '0'..'9'

fun recognize(c: Char) =
    when (c) {
        in '0'..'9' -> "It's a digit"
        in 'a'..'z', in 'A'..'Z' -> "It's a letter"
        else -> "I don't know"
    }

/**
 * 예외 처리
 * 코틀린의 예외 처리는 자바와 비슷하다. throw 를 사용해서 예외를 던진다.
 * throw 는 식이므로 다른 식으로 포함될 수 있다.
 * try, catch, finally를 사용한다.
 *
 * 코틀린은 throws 절이 없다. 코틀린은 체크 예외와 언체크 예외를 구별하지 않는다.
 * 코틀린은 함수가 던지는 예외를 지정하지 않고 발생한 예외를 잡거나 잡지 않아도 된다.
 *
 * try 도 식으로 사용 가능하다.
 */
fun exceptionFunc(num: Int) {
    val percentage =
        if (num in 1..100)
            num
        else
            throw IllegalArgumentException("over percentage")
}

fun readNumber(): Int? {
    val br = BufferedReader(StringReader("22"))
    try {
        val input = br.readLine()
        return Integer.parseInt(input)
    } catch (e: NumberFormatException) {
        return null
    } finally {
        br.close()
    }
}

// 자바에서는 IOException을 잡아야 하지만 코틀린에서는 예외 구분이 없음
fun readNumber2(): Int {
    val br = BufferedReader(StringReader("22"))
    val input = br.readLine()
    br.close()
    return Integer.parseInt(input)
}

fun readNumber3() {
    val br = BufferedReader(StringReader("22"))
    val num = try {
        Integer.parseInt(br.readLine())
    } catch (e: NumberFormatException) {
        return
    }
    println(num)
}
