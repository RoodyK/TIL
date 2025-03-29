package ch05.ex03

/**
 * SAM 생성자
 * 코틀린에서 SAM(Single Abstract Method) 생성자는 람다식과 함께 자주 사용되는 기능으로, 주로 인터페이스를 람다로 변환할 때 사용된다.
 * 코틀린은 인터페이스가 단 하나의 추상 메서드만 가지고 있을 경우, 해당 인터페이스를 람다식으로 바로 변환할 수 있도록 지원한다. 이때, 그 인터페이스는 'SAM 인터페이스'라 부른다.
 * SAM 생성자는 주로 람다식의 간결함을 위해 사용된다.
 *
 * 단일 추상 메서드 인터페이스에만 사용 가능: 인터페이스에 단 하나의 추상 메서드만 있을 때 SAM 생성자가 가능하다.Runnable, Callable, Comparator 같은 인터페이스가 해당된다.
 * 람다 바인딩: 코틀린 컴파일러는 람다식을 적절한 메서드 호출로 변환한다. 즉, Runnable {}와 같은 람다식은 Runnable 인터페이스의 run 메서드를 구현하는 것과 동일하게 처리된다.
 */
// 자바 코드
//Runnable runnable = new Runnable() {
//    @Override
//    public void run() {
//        System.out.println("작업 실행");
//    }
//};
// 코틀린 변환
val samRunnable = Runnable { println("작업 실행") }
// Comparator
val samComparator: Comparator<Int> = Comparator { a, b -> a - b }

/**
 * 코틀린에서 좀 더 명시적으로 함수 타입을 적고 싶은 경우 fun interface를 정의하면 함수형 인터페이스를 정의할 수 있다.
 * 코틀린의 함수형 인터페이스는 정확히 하나의 추상 메서드만 포함하지만 다른 비추상 메서드를 여럿 가질 수 있다.
 * 이를 통해 함수 타입의 시그니처에 들어맞지 않는 여러 복잡한 구조를 표현할 수 있다.
 *
 * fun 인터페이스라고 정의된 타입의 파라미터를 받는 함수가 있을 때 람다 구현이나 람다에 대한 참조를 직접 넘길 수 있다.
 *
 * 코틀린에서 함수 타입을 자바에서 호출할 때 호출하는 쪽에서 명시적으로 Unit.INSTANCE를 반환해야 한다. (8장에서 다룸)
 */
fun interface IntCondition {
    fun check(i: Int): Boolean // 추상 메서드는 하나만 존재
    fun checkString(s: String) = check(s.toInt())
    fun checkChar(c: Char) = check(c.digitToInt())
}

fun checkCondition(i: Int, condition: IntCondition): Boolean {
    return condition.check(i)
}

fun main() {
    val isOdd = IntCondition { it % 2 != 0 }
    println(isOdd.check(1))
    println(isOdd.checkString("2"))
    println(isOdd.checkChar('3'))

    checkCondition(1, { it % 2 != 0}) // 람다 직접 사용
    val isOdd2: (Int) -> Boolean = { it % 2 != 0 }
    checkCondition(1, isOdd2) // 일치하는 람다에 대한 참조 사용
}