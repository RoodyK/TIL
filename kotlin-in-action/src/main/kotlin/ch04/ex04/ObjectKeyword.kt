package ch04.ex04

import ch04.ex02.Member
import java.io.File

/**
 * object 키워드를 사용하는 몇 가지 상황
 * - 객체 선언: 싱글턴을 정의하는 한 가지 방법이다.
 * - 동반 객체: 어떤 클래스와 관련이 있지만 호출하기 위해 그 클래스의 객체가 필요하지는 않은 메서드와 팩토리 메서드를 담을 때 쓰인다.
 *            동반 객체의 멤버에 접근할 때는 동반 객체가 포함된 클래스의 이름을 사용한다.
 * - 객체 식: 자바의 익명 내부 클래스 대신 쓰인다.
 * 모든 경우 클래스를 정의하는 동시에 인스턴스를 생성한다는 공통점이 있다.
 *
 * 코틀린 객체 선언은 자바로 컴파일 될 때 정적 필드가 있는 자바 클래스가 되며, 이름은 항상 INSTANCE다.
 */

/**
 * 객체지향 프로그래밍에서 인스턴스가 하나만 필요할 때 자바에서는 보통 싱글턴 패턴을 사용하는데, 코틀린은 객체 선언 기능을 통해 싱글톤을 언어에서 기본 지원한다.
 * 객체 선언은 클래스 선언과 그 클래스에 속한 단일 인스턴스의 선언을 합친 선언이다.
 * object로 선언(객체 선언)한 객체는 클래스의 인스턴스를 하나만 생성한다. 객체 선언문의 위치에서 생성자 호출 없이 즉시 만들어진다.
 * 클래스와 마찬가지로 객체 선언 안에 프로퍼티, 메서드, 초기화 블록 등이 들어갈 수 있다. 객체 선언에서는 생성자(주 생성자, 부 생성자) 모두 쓸 수 없다.
 *
 * 의존관계가 많은 대규모 소프트웨어 시스템에서 객체 선언은 적합하지 않다. 객체 생성을 제어할 방법이 없고 생성자 파라미터를 지정할 수 없기 때문이다.
 */
object Payroll { // 회사 급여 대장
    val allEmployees = arrayListOf<Person>()

    fun calculateSalary(): Int {
        var total: Int = 0
        for (person in allEmployees) {
            total += person.salary
        }
        return total
    }
}

class Person(
    val name: String, // 읽기 전용으로 필드(비공개)와 getter 메서드 생성
    var salary: Int // 쓰기 가능으로 필드(비공개)와 getter, setter 메서드 생성
)

/**
 * 객체 선언도 클래스나 인터페이스를 상속 가능하며, 그 구현 내부에 다른 상태가 필요없는 경우에 유용하다.
 * Comparator를 인자로 받는 함수에 이 객체를 인자로 넘길 수 있다.
 */
object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int {
        return file1.path.compareTo(file2.path, ignoreCase = true) // 대소문자 구분 없이 비교
    }
}

/**
 * 클래스 안에 객체를 선언할 수 있다. 어떤 클래스의 인스턴스를 비교하는 Comparator는 그 클래스 내부에 정의하는 것이 바람직하다
 */
data class MyPerson(val name: String) {
    object NameComparator : Comparator<MyPerson> {
        override fun compare(o1: MyPerson, o2: MyPerson): Int = o1.name.compareTo(o2.name)
    }
}


/**
 * 동반 객체: 팩토리 메서드, 정적 멤버가 들어갈 장소
 * companion 키워드는 코틀린에서 객체 지향 프로그래밍의 특성을 구현하는 데 유용한 기능이다.
 * 이는 클래스 내에서 객체를 생성할 수 없는 정적 메서드나 변수를 정의할 때 사용된다.
 * 자바에서 static 키워드와 비슷한 역할을 하지만, 코틀린은 static 키워드를 직접 사용하지 않으며, 대신 companion object를 사용한다.
 *
 * 코틀린은 static 키워드를 지원하지 않는 대신, 패키지 수준의 최상위 함수(자바의 정적 메서드를 대신함)와
 * 객체 선언(object 키워드: 코틀린 최상위 함수가 대신할 수 없는 역할이나 정적 필드를 대신함)을 활용한다.
 * 대부분 최상위 함수를 권장하지만, private인 클래스 멤버에 접근할 수 없다는 단점이 있다. 팩토리 메서드는 객체 생성을 책임지므로 클래스 private 멤버에 접근해야 한다.
 *
 * 클래스의 인스턴스와 관계없이 호출해야 하지만, 클래스 내부 정보에 접근해야 하는 함수가 필요할 때도 클래스에 내포된 객체 선언의 멤버 함수로 정의할 수 있다.
 * 클래스 안에 정의된 객체 중 하나에 companion 키워드를 사용하면 객체 멤버에 접근할 때 자신을 감싸는 클래스의 이름을 통해 직접 사용할 수 있다.
 *
 * 동반 객체는 자신이 대응하는 클래스에 속한다는 점이 중요하다. 자바와의 차이점은 클래스의 인스턴스는 접근할 수 없다.
 */
class MyClass {
    companion object {
        fun callMe() {
            println("Companion object called")
        }
    }
}

/**
 * 동반 객체는 자신을 둘러싼 바깥 객체의 private 멤버에 접근할 수 있으므로, private 생성자도 접근할 수 있다. 즉, 팩토리 패턴을 구현하기 적합한 위치가 될 수 있다.
 */
class PrivateUser(override val nickname: String): Member

class SubscribingUser(val email: String) : Member {
    // 뒷받침하는 필드에 값을 저장하지 않고 매번 이메일 주소에서 별명을 계산해서 반환
    override val nickname: String
        get() = email.substringBefore("@") // getter 설정
}

class SocialUser(val accountId: Int) : Member {
    override val nickname = ch04.ex02.getNameFromSocialNetwork(accountId) // 초기화 식 사용
}

fun getNameFromSocialNetwork(accountId: Int) = "kotlin$accountId" // 이 함수는 다른 곳에 정의됐다고 가정

class User {
    val nickname: String

    // 부 생성자
    constructor(email: String) {
        nickname = email.substringBefore("@")
    }
    constructor(socialAccountId: Int) {
        nickname = getNameFromSocialNetwork(socialAccountId)
    }
}

/**
 * 부 생성자를 팩토리 메서드로 대신하기
 *
 * 팩토리 메서드는 이름을 지을 수 있고, 그 팩토리 메서드가 선언된 클래스의 하위 클래스 객체를 반환할 수 있다. (SubscribingUser 객체, SocialUser 객체 중 원하는 것 반환)
 */
class User2 private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) = User2(email.substringBefore("@"))
        fun newSocialUser(accountId: Int) = User2(getNameFromSocialNetwork(accountId))
    }
}

/**
 * 동반 객체는 클래스 안에 정의된 일반 객체다.
 * 다른 객체 선언처럼 동반 객체에 이름을 붙이거나, 동반 객체가 인터페이스를 상속하거나, 동반 객체 안에 확장 함수와 프로퍼티를 정의할 수 있다.
 * 동반 객체에 이름을 붙이지 않으면 Companion 이라는 이름으로 참조에 접근할 수 있고, 이름을 붙이면 그 이름이 쓰인다.
 */
interface JSONFactory<T> {
    fun fromJSON(jsonText: String): T
}

class Person2(val name: String) {
    companion object : JSONFactory<Person2> {
        override fun fromJSON(jsonText: String): Person2 = Person2("Bob")
    }
}

/**
 * 클래스에 동반 객체가 있으면 객체 안에 함수를 정의함으로써 클래스에 대해 호출할 수 있는 확장 함수를 만들 수 있다.
 *
 * Person 클래스는 핵심 비즈니스 모듈의 일부로, 특정 데이터 타입에 의존하는것을 바라지 않는다.
 * 역직렬화 함수를 비즈니스 모듈이 아니라 클라이언트와 서버 사이의 통신을 담당하는 모듈 안에 포함시키고 싶을 때, 확장 함수를 사용하면 구조를 다음처럼 잡을 수 있다.
 *
 * 동반 객체에 확장 함수를 작성하려면, 원래 클래스에 동박 객체를 꼭 선언해야 한다.
 */
class Person3(val firstName: String, val lastName: String) {
    companion object {
        // 빈 객체 생성
    }
}

fun Person3.Companion.fromJson(json: String): Person3 = Person3("Bob", "jo")

/**
 * 객체 식: 익명 내부 클래스를 다른 방법으로 지정
 * object 키워드는 싱글턴 뿐만 아니라 익명 객체를 정의할 때도 사용한다. 자바의 익명 내부 클래스를 대신한다.
 *
 * 이벤트 리스너를 구현하며 확인한다.
 * 익명 객체와 객체 선언의 차이는 객체 이름이 빠졌다는 것이다.
 * 익명 객체는 인터페이스를 구현하지 않거나 하나 혹은 여럿 구현할 수 있다.
 * 코틀린의 익명 객체 안에서는 자바와 달리 final이 아닌 변수 값도 사용할 수 있고 값을 변경할 수 있다.
 *
 * 객체 식은 익명 객체 안에서 여러 메서드를 오버라이드해야 하는 경우에 더 유용하다.
 */
interface MouseListener {
    fun onEnter()
    fun onClick()
}

class Button(private val listener: MouseListener)


fun main() {
    // object 객체 프로퍼티 접근
    println(Payroll.allEmployees.size)
    println(Payroll.calculateSalary())

    println(CaseInsensitiveFileComparator.compare(File("/Users"), File("/users")))

    val files = listOf(File("/Z"), File("/a"))
    println(files.sortedWith(CaseInsensitiveFileComparator))

    val persons = listOf(MyPerson("Bob"), MyPerson("Mike"))
    println(persons.sortedWith(MyPerson.NameComparator))

    MyClass.callMe()
    val myClass = MyClass()
    // myClass.callMe() // Unresolved reference: callMe

    // 익명 객체로 이벤트 리스너 구현
    Button(object : MouseListener {
        override fun onEnter() {
            TODO("Not yet implemented")
        }
        override fun onClick() {
            TODO("Not yet implemented")
        }
    })

    var clickCount: Int = 0;
    val listener = object : MouseListener {
        override fun onEnter() {
            TODO("Not yet implemented")
        }

        override fun onClick() {
            clickCount++
        }
    }
    Button(listener)
}