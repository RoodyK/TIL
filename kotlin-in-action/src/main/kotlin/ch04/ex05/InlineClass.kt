package ch04.ex05

/**
 * 코틀린의 inline class는 간단한 래퍼 클래스를 생성할 때 성능을 최적화하는 기능을 제공한다. 주로 불변 값(예: String, Int 등)을 래핑하는데 사용된다.
 *
 * inline class는 주로 단일 속성만을 가지는 클래스로 정의된다. 클래스를 inline으로 선언하면, 해당 클래스는 인스턴스를 생성하지 않고 컴파일 타임에 다른 타입으로 치환된다.
 * 즉, inline class의 객체를 사용할 때마다 객체를 생성하는 대신 해당 객체의 내부 값이 직접 사용된다. 이로 인해 불필요한 메모리 할당을 줄이고, 성능을 개선할 수 있다.
 * - inline class는 @JvmInline 어노테이션을 사용하여 정의된다.
 * - inline class는 기본 타입을 감싸는 래퍼(wrapper) 역할을 하며, 이로 인해 타입 안전성을 제공한다.
 * - inline class는 런타임에 실제로는 해당 프로퍼티의 타입으로 변환되어 사용되므로, 메모리 오버헤드가 발생하지 않는다.
 *
 *
 */
@JvmInline
value class UserId(val id: Int)

// 함수 시그니처가 Int 타입을 받기 때문에 함수를 호출하는 사람이 서로 다른 의미의 값을 전달하는 것을 막을 방법이 없음(센트, 엔, 원 등)
fun addExpenses(expense: Int) {
    // 비용을 미국 달러의 센트 단위로 저장
}

/**
 *  클래스를 생성하면 해결할 수 는 있지만 addExpenses2 함수를 호출할 때마다 UsdCent 객체를 생성하고 사용후 버리는 현상으로 가비지 컬렉션의 일을 늘리게 된다.
 *  이럴 떄 인라인 클래스를 사용하면 성능을 희생하지 않고 타입 안정성을 얻을 수 있다.
 */
class UsdCent(val amount: Int)

fun addExpenses2(expense: UsdCent) {
    // 비용을 미국 달러의 센트 단위로 저장
}

/**
 * 인라인 객체는 실제로 인스턴스를 생성하지 않고 컴파일 시점에 값으로 치환된다.
 * 인라인으로 표시하는 클래스는 프로퍼티를 하나만 가져야 하며 실행 시점에 인스턴스는 단일 프로퍼티 값으로 치환된다.
 * 인라인 클래스의 프로퍼티는 주 생성자에서 초기화 되야 하며, 인라인 클래스는 클래스 계층에 참여하지 않는다.
 * 인라인 클래스는 다른 클래스를 상속하거나, 다른 클래스가 인라인 클래스를 상속 할 수 없다.
 * 인라인 클래스는 인터페이스를 상속하거나, 메서드 정의, 계산된 프로퍼티를 제공할 수 있다.
 *
 * 대부분 기본 타입 값의 의미를 명확하게 하고 싶을 떄 인라인 클래스를 사용할 것이다.
 * - 일반적인 숫자 타입의 값으로 측정한 값의 단위 표현
 * - 다른 여러 문자열의 서로 다른 의미를 구분
 *
 * 인라인 클래스를 사용하면 함수를 호출하는 쪽에서 실수로 잘못된 의미로 값을 전달하는 경우를 막을 수 있다.
 */
@JvmInline
value class UsdCent2(val amount: Int)

interface PrettyPrintable {
    fun prettyPrint()
}

@JvmInline
value class UsdCent3(val amount: Int) : PrettyPrintable {
    val salesTax
        get() = amount * 0.06
    override fun prettyPrint() = println("${amount}¢")
}

fun main() {
    val expense = UsdCent3(1_99)
    println(expense.salesTax)
    expense.prettyPrint()
}