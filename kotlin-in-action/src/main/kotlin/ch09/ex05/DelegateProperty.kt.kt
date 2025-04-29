package ch09.ex05

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * 위임 프로퍼티를 사용하면 값을 뒷받침하는 필드에 단순히 저장하는 것보다 더 복잡한 방식으로 작동하는 프로퍼티를 접근자 로직에 매번 재구현할 필요 없이 쉽게 구현할 수 있다.
 * 예를 들어 프로퍼티는 위임을 사용해 자신의 값을 필드가 아니라 데이터베이스 테이블이나 브라우저 세션, 맵 등에 저장할 수 있다.
 *
 * 이런 특성의 기반에는 위임이 있으며, 위임은 객체가 직접 작업을 수행하지 않고 다른 도우미 객체가 그 작업을 처리하도록 맡기는 디자인 패턴을 말한다.
 * 이떄 작업을 처리하는 도우미 객체를 위임 객체라고 한다. (4.3.3 절에서 확인했다.)
 * 여기서는 그 패턴을 프로퍼티에 적용해서 접근자 기능을 도우미 객체가 수행하도록 위임한다. 도우미 객체를 직접 작성할 수도 있지만 더 나은 방법으로 코틀린 언어의 기능을 사용할 수 있다.
 */

/**
 * 위임 프로퍼티의 기본 문법과 내부 동작
 *
 * 위임 프로퍼티의 일반적인 문법 => var p: Type by Delegate()
 * p 프로퍼티는 접근자 로직을 다른 객체에 위임한다.
 * 여기서는 Delegate 클래스의 인스턴스를 위임 객체로 사용한다.
 * by 뒤에 식을 계산해 위임에 쓰일 객체를 얻는다.
 * 프로퍼티 위임 객체가 따라야 하는 관례를 따르는 모든 객체를 위임에 사용할 수 있다.
 *
 * class Foo {
 *     var p: Type by Delegate()
 * }
 * 컴파일러는 숨겨진 도우미 도우미 프로퍼티를 만들고 그 프로퍼티를 위임 객체의 인스턴스로 초기화한다. p 프로퍼티는 바로 그 위임 객체에게 자신의 작업을 위임한다.
 * 설명을 편하게 하기 위해 이 감취진 프로퍼티 이름을 delegate 라고 한다.
 *
 * class Foo {
 *     private val delegate: Delegate() // 컴파일러가 생성한 도우미 프로퍼티
 *
 *     // p 프로퍼티를 위해 컴파일러가 생성한 접근자는 delegate의 setValue, getValue 메서드를 호출한다.
 *     var p: Type
 *         set(value: Type) = delegate.setValue(/*...*/, value)
 *         get() = delegate.getValue(/*...*/)
 * }
 * 프로퍼티 위임 관례에 따라 Delegate 클래스는 getValue, setValue 메서드를 제공해야 하며, 변경 가능한 프로퍼티만 setValue를 사용한다.
 * 추가로 위임 객체는 (꼭 그래야할 필요x) 선택적으로 provideDelegate 함수 구현을 제공할 수도 있다.
 * 이 함수는 최초 생성시 검증 로직을 수행하거나 위임 인스턴스화되는 방식을 변경할 수 있다. 이런 함수들을 멤버로 구현할 수도, 확장 함수로 구현할 수도 있다.
 */

//class Delegate {
//    operator fun getValue() {}
//    operator fun setValue(name: String, value: Type) {}
//    operator fun providfe
//}
//
//class Foo {
//    var p: Type by Delegate()
//}

/**
 * 위임 프로퍼티 사용: by lazy()를 사용한 지연 초기화
 *
 * 지연 초기화는 객체의 일부분을 초기화하지 않고 남겨뒀다가 실제로 그 부분의 값이 필요한 경우 초기화할 때 흔히 쓰이는 패턴이다.
 * 초기화 과정에 자원을 많이 사용하거나 객체를 사용할 때마다 꼭 초기화하지 않아도 되는 프로퍼티에 대해 지연 초기화 패턴을 사용할 수 있다.
 *
 * person 클래스가 자신이 작성한 이메일 리스트를 제공할 때, 이메일은 DB에 들어있고 불러오려면 시간이 오래 걸린다.
 * 다음은 이메일을 불러오기 전에는 null을 저자앟고 불러온 다음에는 이메일 리스트를 저장하는 _emails 프로퍼티를 추갛서 지연 초기화를 구현한 클래스를 보여준다.
 *
 * 코드에서는 뒷받침하는 프로퍼티라는 기법을 사용한다.(비공개 프로퍼티는 밑줄을 붙임)
 * _emails 프로퍼티는 값을 저장하고, emails는 _emails 프로퍼테이 대한 읽기 연산을 제공한다.
 * _emails는 널이 될 수 있는 타입, emails은 널이 될 수 없는 타입으로 두 타입이 다르기 때문에 프로퍼티를 2개 모두 사용해야 한다.
 *
 * 이 코드의 단점은 지연 초기화할 프로퍼티가 많아지면 코드가 지저분해지고, 멀티 스레드에서 안전하지 않다.
 * 두 스레드가 접근 시 loadEmail 함수가 여러 번 호출되는 것을 막을 수 없다. 즉, 리소스의 일관성이 깨진다.
 *
 */
class Email {}

class Person(val name: String) {
    private var _emails: List<Email>? = null

    val emails: List<Email>
        get() {
            if (_emails == null) {
                _emails = loadEmail(this)
            }
            return _emails!! // 저장해둔 데이터가 있으면 그 데이터를 반환
        }
}

fun loadEmail(person: Person): List<Email> {
    println("${person.name}의 이메일을 가져옴")
    return listOf()
}

/**
 * 코틀린은 위임 프로퍼티로 코드를 더 간단히 작성할 수 있다.
 * 위임 프로퍼티는 데이터를 저장할 때 쓰이는 뒷받침 프로퍼티와 값이 오직 한 번만 초기화됨을 보장하는 게터 로직을 함께 캡슐화해준다.
 * 위임 객체를 반환하는 라이브러리 함수는 lazy 이다.
 *
 * lazy 함수는 코틀린 관례에 맞는 시그니처의 getValue 메서드가 들어있는 객체를 반환한다. lazy를 by 키워드와 함께 사용해 위임 프로퍼티를 만들 수 있다.
 * lazy 함수의 인자는 값을 초기화할 때 호출할 람다이다.
 * lazy 함수는 기본적으로 스레드 안전하다.
 * 필요하면 동기화에 사용할 락을 lazy 함수에 전달할 수도 있고, 멀티 스레드 환경에서 사용하지 않을 프로퍼티를 위해 lazy 함수가 동기화를 생략하게 할 수도 있다.
 */
class Person2(val name: String) {
    val emails by lazy { loadEmail(this) }
}

fun loadEmail(person: Person2): List<Email> {
    println("${person.name}의 이메일을 가져옴")
    return listOf()
}

/**
 * 위임 프로퍼티 구현
 *
 * 어떤 객체의 프로퍼티가 바뀔 때마다 리스너에게 변경 통지를 보내고 싶은 경우, 어떤 객체를 UI에 표시하는 경우 객체가 바뀌면 자동으로 UI도 바뀌어야 함
 * 이런 경우를 옵저버블(Observable)이라고 한다.
 *
 * Observable 클래스는 Observable들의 리스트를 관리한다.
 * notifyObservers가 호출되면 옵저버는 등록된 모든 Observer의 onChange 함수를 통해 프로퍼티의 이전 값과 새 값을 전달한다.
 */
fun interface Observer { // onChange 메서드 구현만 제공하면 되므로 함수형 인터페이스로 구현
    fun onChange(name: String, oldValue: Any?, newValue: Any?)
}

open class Observable {
    val observers = mutableListOf<Observer>()

    fun notifyObservers(propName: String, oldValue: Any?, newValue: Any?) {
        for (obs in observers) {
            obs.onChange(propName, oldValue, newValue)
        }
    }
}
// 사람의 이름은 변하지 않으므로 읽기 전용, 나머지는 변경 가능한 프로퍼티
class Person3(val name: String, age: Int, salary: Int): Observable() {
    // field를 사용해 프로퍼티를 뒷받침하는 필드에 접근
    var age: Int = age
        set(newValue) {
            val oldValue = field
            field = newValue
            notifyObservers(
                "age", oldValue, newValue
            )
        }

    var salary: Int = salary
        set(newValue) {
            val oldValue = field
            field = newValue
            notifyObservers(
                "salary", oldValue, newValue
            )
        }
}

/**
 * 위 코드의 세터 코드에서 중복이 많이 보인다. 프로퍼티의 값을 저장하고 필요에 따라 통지를 보내주는 클래스를 추출해본다.
 */
class ObservableProperty(val propName: String, var propValue: Int, val observable: Observable) {
    fun getValue(): Int = propValue
    fun setValue(newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        observable.notifyObservers(propName, oldValue, newValue)
    }
}

class Person4(val name: String, age: Int, salary: Int): Observable() {
    val _age = ObservableProperty("age", age, this)
    var age: Int
        get() = _age.getValue()
        set(newValue) {
            _age.setValue(newValue)
        }

    val _salary = ObservableProperty("salary", salary, this)
    var salary: Int
        get() = _salary.getValue()
        set(newValue) {
            _salary.setValue(newValue)
        }
}

/**
 * 구현된 코드는 코틀린의 위임이 실제로 작동하는 방식과 비슷해졌다.
 * 프로퍼티 값을 저장하고 그 값이 바뀌면 자동으로 변경 통지를 전달해주는 클래스를 만들었다.
 * 로직의 중복을 많이 제거했지만 아직 각각의 프로퍼티마다 ObservableProperty를 만들고 게터, 세터에서 ObservableProperty에 작업을 위임하는 준비 코드가 상당 부분 필요하다.
 * 코틀린의 위임 프로퍼티 기능을 활용하면 이런 준비 코드를 없엘 수 있다.
 *
 * 그 전에 ObservableProperty에 있는 두 메서드의 시그니처를 코틀린의 관례에 맞게 수정해야 한다.
 *
 * 이전 코드와 비교
 * - 코틀린 관례에 사용하는 다른 함수와 마찬가지로 getValue, setValue 함수에도 operator 변경자가 붙는다.
 * - 게터, 세터 함수는 파라미터 2개를 받는다. 바로 설정하거나 읽을 프로퍼티가 들어있는 인스턴스(thisRef)와 프로퍼티를 표현하는 객체(prop)다.
 *   코틀린은 KProperty 타입의 객체를 사용해 프로퍼티를 표현한다. KProperty.name을 통해 메서드가 처리할 프로퍼티 이름을 알 수 있다.(자세한건 12장)
 * - KProperty 인자를 통해 프로퍼티 이름을 전달받으므로 주 생성자에서는 name 프로퍼티를 없앤다.
 */
class ObservableProperty2(var propValue: Int, val observable: Observable) {
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): Int = propValue
    operator fun setValue(thisRef: Any?, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        observable.notifyObservers(prop.name, oldValue, newValue)
    }
}

/**
 * 위임 프로퍼티를 사용해본다.
 *
 * by 키워드를 사용해 위임 객체를 지정하면 이전 예제에서 직접 코드를 작성해야 했던 여러 작업을 코틀린 컴파일러가 자동으로 처리해준다.
 * 코틀린 컴파일러가 만들어주는 코드는 작성했던 이전 Person4와 비슷하다.
 * by 오른쪽에 오는 객체를 위임 객체라고 부른다.
 * 코틀린은 위임 객체를 감춰진 프로퍼티에 저장하고 주 객체의 프로퍼티를 읽거나 쓸 때마다 위임 객체의 getValue, setValue를 호출해준다.
 */
class Person5(val name: String, age: Int, salary: Int): Observable() {
    var age by ObservableProperty2(age, this)
    var salary by ObservableProperty2(salary, this)
}

/**
 * 관찰 가능한 프로퍼티 로직을 직접 작성하는 대신 코틀린 표준 라이브러리를 사용해도 된다.
 * 라이브러리에는 ObservableProperty2와 비슷한 클래스가 있다.
 * 다만 이 라이브러리의 클래스는 앞에서 정의한 Observable과는 연결돼있지 않다. 따라서 프로퍼티 값의 변경을 통지받을 때 쓰일 람다를 라이브러리 클래스에 넘겨야 한다.
 *
 * by의 오른쪽에 있는 식이 꼭 새 인스턴스를 만들 필요는 없다.
 * 함수 호출, 다른 프로퍼티, 다른 식 등이 by 오른쪽에 올 수 있다.
 * 다만 오른쪽 식을 계산한 결과인 객체는 컴파일러가 호출할 수 있는 올바른 타입의 getValue, setValue를 반드시 제공해야 한다.
 * 다른 관례와 마찬가지로 getValue와 setValue 모두 객체 안에 정의된 메서드이거나 확장 함수일 수 있다.
 */
class Person6(val name: String, age: Int, salary: Int): Observable() {
    private val onChange = { property: KProperty<*>, oldValue: Any?, newValue: Any? ->
        notifyObservers(property.name, oldValue, newValue)
    }

    var age by Delegates.observable(age, onChange)
    var salary by Delegates.observable(salary, onChange)
}

/**
 * 위임 프로퍼티는 커스텀 접근자가 있는 감춰진 프로퍼티로 변환된다.
 *
 * 위임 프로퍼티가 어떤 방식으로 동작하는지 정리해본다.
 * class C {
 *     var prop: Type by MyDelegate()
 * }
 * val c = C()
 *
 * MyDelegate 클래스의 인스턴스는 감춰진 프로퍼티에 저장되며, 그 프로퍼티를 <delegate>라는 이름으로 부를 것이다.
 * 컴파일러는 프로퍼티를 표현하기 위해 KProperty 타입의 객체를 사용한다. 이 객체를 <property>라고 부를 것이다.
 *
 * 컴파일러는 다음 코드를 생성한다.
 * class C {
 *     private val <delegate> = MyDelegate()
 *     var prop: Type
 *         get() = <delegate>.getValue(this, <property>)
 *         set(value: Type) = <delegate>.setValue(this, <property>, value)
 * }
 *
 * 컴파일러는 모든 프로퍼티 접근자 안에 getValue, setValue 호출 코드를 생성해준다.
 * 이 메커니즘은 프로퍼티 값이 저장될 장소를 (맵, DB 테이블, 세션, 쿠키 등으로) 바꿀 수도 있고 프로퍼티를 읽거나 쓸 때 벌어질 일을 (값 검증, 변경 통지 등으로) 변경할 수도 있다.
 */

/**
 * 맵에 위임에서 동적으로 애트리뷰트 접근
 *
 * 자신이 프로퍼티를 동적으로 정의할 수 있는 객체를 만들 때 위임 프로퍼티를 활용하는 경우가 자주 있는데, C#은 그런 객체를 확장 가능한 객체라고 부르기도 한다.
 *
 * 연락처 관리 시스템에서 각 연락처별로 임의의 정보를 저장할 수 있게 허용하는 경우(특별히 처리해야하는 일부 필수 정보, 사람마다 달라지는 추가 정보)
 */
class Person7 { // 속성을 맵에 저장하되 특별한 처리가 필요한 정보에 접근하도록 프로퍼티를 제공하는 방법 사용
    private val _attributes = mutableMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    var name: String
        get() = _attributes["name"]!!
        set(value) {
            _attributes["name"] = value
        }
}

/**
 * 여기서 추가 데이터를 객체에 읽어 들이기 위해 일반적인 API를 사용하고(실제 프로젝트는 JSON 역직렬화 등의 기술 사용) 한 프로퍼티(name)을 처리하기 위해 구체적인 API를 제공한다.
 * 위임 프로퍼티를 활용하기 위해 by 키워드 뒤에 Map을 직접 넣으면 된다.
 * 이 코드가 동작하는 이유는 Map과 MutableMap 인터페이스에 대해 getValue, setValue 확장 함수를 제공하기 때문이다.
 */
class Person8 {
    private val _attributes = mutableMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    var name: String by _attributes
}

/**
 * 실전 프레임워크가 위임 프로퍼티를 활용하는 방법
 *
 * 객체 프로퍼티를 저장하거나 변경하는 방법을 바꿀 수 있으면 프레임워크를 개발할 때 유용하다. 자바 개발자는 JPA 를 생각하면 됨
 *
 * DB에 User 테이블이 있고 name, age 컬럼이 있다고 가정한다. User 클래스를 만들고 User 엔티티의 데이터를 가져오고 저장할 수 있다.
 * object Users: IdTable() { // DB 테이블에 해당 - 단하나만 존재하므로 싱글턴으로 생성
 *     val name = varchar("name", length = 50).index()
 *     val age = integer("age")
 * }
 *
 * class User(id: EntityID): Entity(id) {
 *     var name: String by Users.name
 *     var age: Int by Users.age
 * }
 *
 * Entity 클래스는 데이터베이스 컬럼을 엔티티의 속성값으로 연결해주는 매핑이 있다.
 */


fun main() {
    val p = Person("Alice")
    p.emails // load Email for Alice
    p.emails

    val p2 = Person2("Bob")
    p2.emails // load Email for Alice
    p2.emails

    println()
    println("===== 위임 프로퍼티 구현 =====")
    val p3 = Person3("Seb", 28, 1000)
    p3.observers += Observer {propName, oldValue, newValue ->
        println(
            """
                Property $propName changed from $oldValue to $newValue
            """.trimIndent()
        )
    }
//    p3.observers.add(Observer {propName, oldValue, newValue ->
//        println(
//            """
//                Property $propName changed from $oldValue to $newValue
//            """.trimIndent()
//        )
//    })
    p3.age = 29
    p3.salary = 1500
    println()
    val p5 = Person5("Seb", 28, 1000)
    p5.observers += Observer { propName, oldValue, newValue ->
        println(
            """
                Property $propName changed from $oldValue to $newValue
            """.trimIndent()
        )
    }
    p5.age = 29
    p5.salary = 1500

    println()
    println("===== Map 위임 프로퍼티 구현 =====")
    val p7 = Person7()
    val data = mapOf("name" to "Seb", "company" to "JetBrains")
    for ((attrName, value) in data) {
        p7.setAttribute(attrName, value)
    }
    println(p7.name)
    p7.name = "Sebastian"
    println(p7.name)

    val p8 = Person8()
    val data2 = mapOf("name" to "Seb", "company" to "JetBrains")
    for ((attrName, value) in data2) {
        p8.setAttribute(attrName, value)
    }
    println(p8.name)
    p8.name = "Sebastian"
    println(p8.name)
}