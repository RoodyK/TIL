package ch04.ex02

import java.net.URI

/**
 * 객체지향 언어의 클래스에서 생성자는 여러 개 생성 가능하지만, 코틀린은 주 생성자와 부 생성자를 구분하며 초기화 블록을 통해 초기화 로직을 추가할 수 있다.
 * 주 생성자: 클래스를 초기화할 때 주로 사용하는 간략한 생성자로, 클래스 본문 밖에서 정의한다.
 * 부 생성자: 클래스 본문 안에서 정의한다.
 */

/**
 * 간단한 클래스 초기화
 * 클래스 선언에 중괄호 {}가 없고 괄호 사이에 val 선언만 존재하는데, 클래스 이름 뒤에 오는 괄호 코드를 주 생성자라고 한다.
 */
class User(val nickname: String) // val은 이 파라미터에 상응하는 프로퍼티가 생성된다는 것이다.

/**
 * 주 생성자를 풀어쓴 예
 *
 * 주 생성자는 생성자 파라미터를 지정하고 그 생성자 파라미터에 의해 초기화되는 프로퍼티를 정의하는 2가지 목적에 쓰인다.
 * constructor 키워드는 주 생성자나 부 생성자 정의를 시작할 때 사용하며, init 키워드는 초기화 블록(인스턴스 생성 시 실행되는 초기화 코드)을 시작한다.
 * 초기화 블록은 주 생성자와 함께 사용되며, 생성자는 별도의 코드를 포함할 수 없어서 초기화 블록이 필요하다.
 * 프로퍼티 초기화 코드를 프로퍼티 선언에 포함시킬 수 있고, 주 생성자 앞에 별다른 애노테이션이나 가시성 변경자가 없다면 constructor를 생략해도 된다.
 * 이 방식은 프로퍼티 초기화 식이나 초기화 블록 안에서만 주 생성자의 파라미터를 참조할 수 있다는 점을 유의해야 한다.
 */
class User2 constructor(nickname: String) {
    val nickname: String

    init {
        this.nickname = nickname
    }
}

/**
 * 생성자 파라미터도 기본값을 지정할 수 있다.
 * 자바 코드가 코틀린 생성자가 제공하는 디폴트 파라미터 중에 몇 가지만 생략해야 하는 경우 모든 파라미터에 대해 기본값을 정의한 생성자에 @JvmOverloads constructor를 지정한다.
 */
class User3(val nickname: String, val isSubscribed: Boolean = true)

/**
 * 클래스를 정의할 때 별도 생성쟈를 정의하지 않으면 컴파일러가 인자가 없는 디폴트 생성자륾 만들어준다.
 *
 * Button의 생성자는 아무 인자도 받지 않지만 상속하는 클래스는 반드시 Button 클래스의 생성자를 호출해야 한다.
 * 이로 인해서 기반 클래스의 이름 뒤에 빈 괄호가 필요했던 것이다.
 *
 * 인터페이스는 생성자가 없기 때문에 클래스가 인터페이스를 구현하는 경우 괄호는 필요없다.
 */
open class Button // 인자가 없는 디폴트 생성자 생성

class RadioButton: Button() // Button 클래스의 디폴트 생성자 호출로 인한 괄호가 필요함

/**
 * 클래스를 클래스 외부에서 인스턴스화하지 못하게 하려면 생성자를 private으로 만들어야 한다.
 * 다음 클래스 안에는 주 생성자 밖에 없고, 주 생성자가 비공개이므로 외부에서 인스턴스화할 수 없다.
 */
class Secretive private constructor(private val agentName: String) {}


/**
 * 인자에 대한 기본값을 제공하기 위해 부 생성자를 여러 개 작성하는 것보다, 파라미터 기본값을 생성자 시그니처에 직접 명시하는 것이 좋다.
 *
 * 생성자가 여러 개 필요한 경우 자바와 코틀린 코드
 */
//public class Downloader {
//    public Downloader(String url) {}
//    public Downloader(URI uri) {}
//}
open class Downloader {
    constructor(url: String?) {} // 부 생성자들
    constructor(uri: URI?) {}
}

/**
 * 부 생성자는 constructor 키워드로 시작하며, 원하는 만큼 생성할 수 있다.
 * 부 생성자는 super() 키워드로 상위 클래스의 생성자를 호출한다. 자바처럼 this()로 자신의 다른 생성자를 호출할 수 있다.
 *
 * 부 생성자가 필요한 이유는 자바 상호운용성 및 클래스 인스턴스를 생성할 때 파라미터 목록이 다른 생성 방법이 여럿 존재하는 경우 부 생성자를 여러 개 두어야 한다.
 */
class MyDownloader : Downloader {
    private val url: String?

    constructor(url: String?) : super(url) {
        this.url = url
    }
    constructor(uri: URI?) : super(uri) {
        this.url = uri.toString()
    }
}

class MyDownloader2 : Downloader {
    constructor(url: String?) : this(URI(url)) {}
    constructor(uri: URI?) : super(uri) {}
}

/**
 * 인터페이스는 아무 상태를 포함할 수 없으므로 선언된 프로퍼티는 구현하는 클래스에서 상태 저장을 위한 프로퍼티 등을 만들어야 한다.
 *
 * 추상 프로퍼티를 구현할 때도 override 키워드를 명시해야 한다.
 */
interface Member {
    val nickname: String
}

class PrivateMember(override val nickname: String): Member

class SubscribingMember(val email: String) : Member {
    // 뒷받침하는 필드에 값을 저장하지 않고 매번 이메일 주소에서 별명을 계산해서 반환
    override val nickname: String
        get() = email.substringBefore("@") // getter 설정
}

class SocialMember(val accountId: Int) : Member {
    override val nickname = getNameFromSocialNetwork(accountId) // 초기화 식 사용
}

fun getNameFromSocialNetwork(accountId: Int) = "kotlin$accountId" // 이 함수는 다른 곳에 정의됐다고 가정

/**
 * 인터페이스에서 추상 프로퍼티 뿐만 아니라 getter, setter 를 가진 프로퍼티를 선언할 수 있다. 이 게터 세터는 뒷받침하는 필드를 참조할 수 없다.
 * 뒷받침하는 필드가 있다면 인터페이스에 상태를 추가하는 셈인데, 인터페이스는 상태를 저장할 수 없다.
 * email은 반드시 오버라이드 해야하며 nickname은 상속할 수 있다.
 *
 * 코틀린에서 함수 대신 프로퍼티를 사용할 때 (이 외에는 함수를 사용하자)
 * - 예외를 던지지 않을 떄
 * - 계산 비용이 적게 들거나 최초 실행 후 결과를 캐시해 사용할 수 있을 떄
 * - 객체 상태가 바뀌지 않으면 여러 번 호출해도 항상 같은 결과를 돌려줄 때
 */
interface EmailMember {
    val email: String
    val nickname: String
        get() = email.substringBefore("@") // 프로퍼티에 뒷받침하는 필드가 없다. 매번 결과를 계산해 돌려준다.
}


/**
 * 뒷받침하는 필드
 *
 * 코틀린에서 뒷받침하는 필드는 클래스의 프로퍼티와 관련된 내부 저장소를 의미한다.
 * 프로퍼티는 일반적으로 getter와 setter를 통해 접근할 수 있지만, 뒷받침하는 필드는 이러한 프로퍼티의 실제 값을 저장하는 역할을 한다.
 *
 * 예를 들어, 프로퍼티가 private으로 선언된 경우, 외부에서 직접 접근할 수 없고, 뒷받침하는 필드를 통해서만 값을 읽거나 쓸 수 있다.
 * 뒷받침하는 필드는 보통 `field` 키워드를 사용하여 정의되며, 프로퍼티의 getter와 setter에서 이 필드를 참조하여 값을 처리한다.
 *
 * 이러한 구조는 데이터 은닉을 가능하게 하여, 클래스의 내부 상태를 보호하고, 프로퍼티에 대한 접근을 제어할 수 있게 한다.
 * 따라서 뒷받침하는 필드는 코틀린에서 객체 지향 프로그래밍의 중요한 개념 중 하나로, 코드의 안전성과 유지보수성을 높이는 데 기여한다.
 */

/**
 * 어떤 값을 저장하되 그 값을 변경, 읽을때마다 정해진 로직을 실행하는 프로퍼티를 만드려면, 저장과 동시에 접근자 안에서 프로퍼티를 뒷받침하는 필드에 접근해야 한다.
 *
 * 프로퍼티에 저장된 값의 변경 이력을 로그에 남기려는 경우
 * 접근자의 본문에서 field라는 특별한 식별자를 통해 뒷받침하는 필드에 접근할 수 있다.
 * setter를 정의하고, getter는 값을 반환만 하므로 직접 정의할 필요는 없다.
 */
class Member2(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println(
                """
                    Address was changed for $name
                    "$field" -> "$value".
                """.trimIndent()
            )
            field = value // 필드 값 변경. field는 프로퍼티 address 를 나타낸다.
        }
}


/**
 * 접근자의 가시성은 기본적으로 프로퍼티의 가시성과 같은데, get과 set에 가시성 변경자를 추가해서 접근자의 가시성을 변경할 수 있다.
 */
class LengthCounter {
    var counter: Int = 0
        private set // 클래스 밖에서 이 값을 변경할 수 없음

    fun addWord(word: String) {
        counter += word.length
    }
}

fun main() {
    // 인스턴스 생성은 new 키워드를 사용하지 않는다.
    val alice = User3("Alice")
    val bob = User3("Bob", false)
    val mike = User3(nickname = "Mike", isSubscribed = true)

    println(PrivateMember("Test").nickname)
    println(SubscribingMember("Wood@gmail.com").nickname)
    println(SocialMember(1234).nickname)

    val member = Member2("Mike")
    member.address = "Lee"

    val lengthCounter = LengthCounter()
    lengthCounter.addWord("hello, world")
    println(lengthCounter.counter )
}
