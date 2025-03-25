package ch04.ex01

import java.io.Serializable

/**
 * 코틀린의 클래스 계층 정의 방식 및 가시성과 접근 변경자를 확인한다.
 * 코틀린의 가시성과 접근 변경자는 자바와 비슷하지만, sealed 접근 제어자가 있는데 클래스 상속이나 인터페이스 구현을 제한한다.
 */

/**
 * 코틀린의 인터페이스에는 추상 메서드 뿐만 아니라 구현이 있는 메서드도 정의할 수 있다.
 * 기술적으로 아래의 함수는 Unit이라는 값을 반환하는데, 자바의 void와 같다.
 *
 * 자바에서는 코틀린의 디폴트 구현을 사용할 수 없고 하튀 타입의 클래스에서 직접 구현해야 한다.
 */
interface Clickable {
    fun click()
    fun showOff() = println("click function off")
}

interface Focusable {
    fun focus()
    fun showOff() = println("focus function off")
}


/**
 * 클래스 상속 or 인터페이스 구현
 * - 코틀린에서 상속이나 구현은 모두 클래스 이름 뒤에 콜론(:)을 붙이고 클래스명 or 인터페이스명을 적는다. 클래스는 단일 상속, 인터페이스는 여러 개 구현 가능하다.
 *
 * 상위 클래스, 인터페이스의 메서드를 구현할 때 override 변경자를 사용한다. override 변경자는 필수이다.
 * override 변경자는 실수로 상위 클래스의 메서드를 오버라이드하는 경우를 방지해준다.
 * 상위 클래스의 메서드 시그니처와 같은 메서드를 하위 클래스에서 구현한다면 override 변경자를 작성하지 않으면 컴파일되지 않는다.
 *
 * 구현부가 있는 함수는 오버라이딩 해도 되고 안해도 됨
 */
class Button : Clickable {
    override fun click() {
        println("click button")
    }
}

/**
 * 다중 인터페이스를 구현할 때, 두 인터페이스에 같은 메서드가 있다면 어느쪽도 선택되지 않고 클래스가 오버라이딩 메서드를 직접 구현해야 한다.
 * 구현하지 않는다면 컴파일러 오류가 발생한다.
 */
class Button2 : Clickable, Focusable {
    override fun click() = println("click button")
    override fun focus() = println("focus button")
    // 두 인터페이스가 공통으로 갖는 메서드는 직접 구현해야 한다.
    override fun showOff() = println("doubly function off")
}

class Button3 : Clickable, Focusable {
    override fun click() = println("click button")
    override fun focus() = println("focus button")

    // 상위 타입의 이름을 홀화살괄호(<>) 사이에 넣은 super를 사용하면 어떤 상위 타입 멤버 메서드를 호출할지 지정할 수 있다.
    override fun showOff() {
        // super를 사용한 상위 타입의 메서드가 실행된다.
        super<Clickable>.showOff()
        super<Focusable>.showOff()
    }
}

/**
 * open, final, abstract 변경자 - 기본은 final
 * 기본적으로 코틀린 클래스에 대해 하위 클래스를 만들 수 없고, 기반 클래스를 하위 클래스가 오버라이드 할 수 없다.
 * 코틀랜에서는 모든 클래스와 메서드는 기본적으로 final 이다.
 * 자바는 모든 클래스가 상속 가능하고, 하위 클래스에서 오버라이드 할 수 있다.
 *
 * 취약한 기반 클래스라는 문제는 기반 클래스 구현을 변경함으로써 하위 클래스가 잘못된 동작을 하게 되는 경우를 뜻한다.
 * 어떤 클래스를 상속하는 방법에 대한 정확한 규칙(어떤 메서드를 어떻게 오버라이드 하는가)을 제공하지 않으면, 클래스를 작성한 사람의 의도와 다른 방식으로 오버라이드할 위험이 있다.
 * 기반 클래스를 변경하는 경우, 하위 클래스의 동작이 예기치 않게 바뀔 수 있다는 면에서 기반 클래스는 '취약'하다.
 *
 * 이펙티브 자바에서는 상속을 위한 설계와 문서를 갖출 수 없다면 상속을 금지하라고 조언한다.
 *
 * 코틀린에서 클래스의 상속을 허용하려면 클래스명 앞에 open 변경자를 사용하며, 오버라이드를 허용할 메서드, 프로퍼티의 앞에도 open 변경자를 사용한다.
 * 기반 클래스나 인터페이스의 멤버를 오버라이드한 경우 자동으로 open 변경자로 간주된다. 금지하려면 final 변경자를 명시해야 한다.
 *
 * 코틀린 클래스는 final 이기 때문에 스마트 캐스트(별도의 타입 변환 없이 멤버에 접근할 수 있는 자동 캐스트)를 사용할 수 있다.
 */
open class RichButton: Clickable {
    fun disable() = println("RichButton disable")
    open fun animate() = println("add the animation to button")
    // 이 메서드는 인터페이스를 구현한 것이므로 하위 클래스에서 이 메서드를 오버라이드 해도 된다.
    override fun click() = println("click RichButton")
}

class ThemedButton : RichButton() {
    // override fun disable() {} // 'disable' in 'RichButton' is final and cannot be overridden
    override fun animate() = println("override animate effect")
    override fun click() = println("override button click")
}

/**
 * 클래스에 abstract 변경자를 사용할 경우 인스턴스화할 수 없다.
 *
 * 코틀린 접근 변경자
 * - final: 오버라이드 할 수 없으며, 클래스의 기본 변경자다.
 * - open: 오버라이드할 수 있으며, 메서드나 클래스에 open을 명시해야 한다.
 * - abstract: 반드시 오버라이드 해야하며, 추상 클래스 멤버에만 사용 가능하다.
 * - override: 상위 클래스나 인스턴스 멤버를 오버라이드할 때 사용하며, 하위 클래스에 오버라이드를 금지하려면 final을 명시해야 한다.
 */
abstract class Animated {
    abstract val animationSpeed: Double
    val keyFrames: Int = 20
    open val frames: Int = 60

    abstract fun animate()
    open fun stopAnimating() = println("stop the animation effect")
    fun animateTwice() = println("animationTwice")
}


/**
 * 가시성 변경자는 코드 기반에 있는 선언에 대한 클래스 외부 접근을 제어한다.
 * 클래스의 구현에 대한 접근을 제한함으로써, 그 클래스에 의존하는 외부 코드를 깨지 않고도 클래스 내부 구현을 변경할 수 있다.
 * 코틀린은 public, protected, private 변경자를 제공한다. 기본값은 public 이다.
 *
 * 모듈은 함께 컴파일되는 코틀린 파일의 집합으로, 코틀린은 모듈 안으로만 한정된 가시성을 위해 internal을 제공한다.
 *
 * 코틀린은 패키지 전용 가시성이 없다. 패키지는 네인스페이스를 관리하기 위한 용도로만 사용한다.
 *
 * 코틀린의 가시성 변경자
 * - public(기본): 모든 곳에서 볼 수 있다.
 * - internal: 같은 모듈 안에서만 볼 수 있다.
 * - protected: 하위 클래스 안에서만 볼 수 있다. 최상위 선언에 적용할 수 없다.(최상위 선언은 파일의 최상위에 선언된 함수를 말한다.)
 * - private: 같은 클래스 or 같은 파일 안에서만 볼 수 있다.
 *
 * 코틀린의 가시성 변경자는 컴파일된 자바 바이트코드 안에서도 그대로 유지된다.
 * 자바와 다른 점은 private 클래스인데, 자바는 클래스를 private으로 할 수 없기 때문에 코틀린은 private 클래스를 패키지 전용 클래스로 컴파일한다.
 *
 * 자바와 달리 코틀린에서는 외부 클래스가 내부 클래스나 내포된 클래스의 private 멤버에 접근할 수 없다.
 *
 *
 * 코틀린의 가시성 변경자와 자바
 *
 * internal 변경자는 자바에서는 없는데, 모듈은 보통 여러 패키지로 이뤄지며 서로 다른 모듈에 같은 패키지에 속한 선언이 들어있을 수 있다.
 * 따라서 internal 변경자는 바이트 코드상에서 public이 된다.
 * 코틀린 컴파일러는 internal 멤버의 이름을 기분 나쁘게 변경해서(mangle) 자바에서 문제없이 사용할 수는 있지만 멤버 이름을 통해 코드가 못생기게 만든다.
 * 이로 인해 모듈에 속한 어떤 클래스를 모듈 밖에서 상속할 때 메서드가 상위 internal 메서드와 같아져서 내부 메서드를 오버라이드 하는 경우를 방지한다.
 */
internal open class TalkativeButton {
    private fun yell() = println("Hey!")
    protected fun whisper() = println("Let's talk!")
}

/**
 * 코틀린은 public 함수인 giveSpeech 안에서 그보다 가시성이 낮은 타입은 TalkativeButton을 참조하지 못하게 한다.
 * 하위 타입이 상위 타입의 가시성보다 높을 수 없으므로 에러가 발생
 */
//fun TalkativeButton.giveSpeech() {}

/**
 * 코틀린에서도 클래스 안에 다른 클래스를 선언할 수 있지만, 자바와 달리 내포 클래스(nested class)는 명시적으로 요청하지 않는 한 바깥쪽 클래스 인스턴스에 대한 접근 권한이 없다.
 */
interface State: Serializable

interface View {
    fun getCurrentState(): State
    fun restoreState(state: State)
}

/**
 * 자바에서는 outer 클래스 안에 내포된 클래스가 Serializable을 구현할 때 직렬화가 되지 않는다.
 * 이는 inner class가 outer class의 참조를 암시적으로 포함해서 outer class는 직렬화할 수 없기 때문에 inner class도 직렬화할 수 없다
 * 이를 해결하려면 내부 클래스(inner class)를 내포 클래스(static inner class)로 선언하면 바깥쪽 클래스에 대한 암시적인 참조가 사라져서 직렬화가 가능하다.
 *
 * 코틀린에서는 내포된 클래스에 아무런 변경자도 없으면 자바 static 내포 클래스와 같다.
 * 이를 inner class로 변경해서 outer class에 대한 참조를 포함하게 만들고 싶다면 inner 변경자를 붙여야 한다.
 *
 * 내포 클래스: outer class에 대한 참조를 저장하지 않음
 * 내부 클래스: outer class에 대한 참조를 저장함
 * 내포 클래스 안에는 바깥쪽 클래스에 대한 참조가 없지만 내부 클래스는 참조가 존재한다.
 */
class MyButton : View {
    override fun getCurrentState(): State = ButtonState()

    override fun restoreState(state: State) = println("override restore state")
    class ButtonState : State
}

/**
 * 코틀린에서 내부 클래스 Inner 안에서 바깥쪽 클래스 Outer의 참조에 접근하려면 this@Outer 를 써야한다.
 */
class Outer {
    inner class Inner {
        fun getOuterReference(): Outer = this@Outer
    }
}

/**
 * 클래스 계층을 만들되 그 계층에 속한 클래스의 수를 제한하고 싶은 경우 내포 클래스를 쓰면 편리하다
 *
 * 코틀린은 봉인된 클래스(sealed 클래스)를 통해 상위 클래스에 sealed 변경자를 붙이면 그 상위 클래스를 상속한 하위 클래스의 가능성을 제한할 수 있다.
 * sealed 클래스의 직접적인 하위 클래스들은 반드시 컴파일 시점에 알려져야 하며, 봉인된 클래스와 같은 패키지에 속해야 하며, 모든 하위 클래스가 같은 모듈 안에 위치해야 한다.
 *
 * 코틀린에서 sealed 키워드는 클래스나 인터페이스 앞에 사용되며, 해당 클래스나 인터페이스가 상속될 수 있는 범위를 제한한다.
 * sealed 클래스나 인터페이스는 반드시 같은 파일 내에서만 상속될 수 있으며, 이를 통해 클래스 계층 구조를 제한하여 코드의 안전성을 높인다.
 *
 * sealed 클래스를 정의하면 그 클래스를 상속할 수 있는 하위 클래스들을 컴파일 타임에 모두 알 수 있다.
 * 즉, 외부에서 새로운 클래스를 상속하는 것이 불가능하고, 모든 하위 클래스는 같은 파일 내에서만 정의해야 한다.
 * 이를 통해 when 구문을 사용할 때 모든 가능한 하위 클래스가 다뤄진다는 점에서 안전성을 보장할 수 있다.
 *
 * when 식에서 sealed 클래스의 모든 하위 클래스를 처리한다면 디폴트 분기(else 분기)가 필요 없다. 컴파일러가 모든 분기를 처리하는지 확인해준다.
 * selaed 변경자는 클래스가 추상 클래스임을 명시한다. 따라서 봉인된 클래스에 abstract를 붙일 필요가 없으며, 추상 멤버를 선언할 수 있다.
 * 봉인된 클래스를 상속한 하위 클래스는 모두 컴파일 타임에 알려져야 한다.
 * sealed 클래스를 상속한 클래스를 추가하면 when식이 컴파일되지 않으면서 변경헤야하는 코드를 알려준다.
 */
sealed class Expr
class Num(val value: Int): Expr()
class Sum(val left: Expr, val right: Expr): Expr() // Sum(Sum(Num(1), Num(2)), Num(4)) 와 같은 형태가 됨
class Mul(val left: Expr, val right: Expr): Expr()

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.right) + eval(e.left)
        is Mul -> eval(e.right) * eval(e.left)
    }

/**
 * 인터페이스에도 sealed 변경자를 붙일 수 있으며, 봉인된 인터페이스도 같은 규칙을 따른다.
 */
sealed interface Toggleable {
    fun toggle()
}

class LightSwitch : Toggleable {
    override fun toggle() = println("light switch toggle!")
}

class Camera : Toggleable {
    override fun toggle() = println("camera toggle!")
}

fun executionToggle(toggle: Toggleable) =
    when (toggle) {
        is LightSwitch -> toggle.toggle()
        is Camera -> toggle.toggle()
    }

fun main() {
    Button().click()
    Button2().showOff()
    Button3().showOff()
}