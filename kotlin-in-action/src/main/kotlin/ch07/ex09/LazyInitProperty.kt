package ch07.ex09

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

/**
 * 실제로는 널이 아닌 프로퍼티인데 생성자 안에서 널이 아닌 값으로 초기화할 방법이 없는 경우가 있다. 이 상황을 코틀린에서 처리하는 방법을 확인한다.
 *
 * 객체를 생성한 후 나중에 전용 메서드로 초기화하는 프레임워크가 많다.
 * 안드로이드는 onCreate에서 액티비티를 초기화하고 Junit는 @BeforeAll, @BeforeEach로 애노테이션된 메서드 안에서 초기화 로직을 수행해야만 한다.
 *
 * 코틀린에서 클래스 안의 널이 아닌 프로퍼티를 생성자 안에서 초기화하지 않고 특별한 메서드 안에서 초기화할 수는 없다.
 * 일반적으로 생성자에서 모든 프로퍼티를 초기화 해야하며, 널이 될 수 없는 타입은 널이 될 수 없는 값으로 초기화해야 한다.
 * 이런 초기화 값을 제공할 수 없다면널이 될 수 있는 타입을 사용할 수 밖에 없는데, 그러면 null 검사를 넣거나 !! 연산자를 사용해야 한다.
 *
 * 아래 코드는 코드가 별로이며, 프로퍼티를 여러 번 사용해야 하면 더 못나진다.
 */
class MyService {
    fun performAction(): String = "Action Done!"
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTest {
    private var myService: MyService? = null

    @BeforeAll
    fun setUp() { // setup 메서드에 프로퍼티의 초기화 진행
        myService = MyService()
    }

    @Test fun testAction() { // 반드시 널 가능성에 신경써야 한다. !! 나 ? 를 사용해야 한다.
        assertEquals("Action Done!", myService!!.performAction())
    }
}

/**
 * 이를 해결하기 위해 myService 프로퍼티를 지연 초기화할 수 있다.
 * 지연 초기화 변수는 var 이어야 하며, 지연 초기화 프로퍼티는 널이 될 수 없는 타입이라해도 더 이상 생성자 안에서 초기화할 필요가 없다.
 * 그 프로퍼티를 초기화하기 전에 프로퍼티에 접근하면 "lateinit property myService has not been initialized" 에러가 나타난다.
 *
 * lateinit 프로퍼티는 널이 될 수 없는 타입이지만 값을 즉시 초기화할 필요가 없다. lateinit은 자바 의존관계 주입 프레임워크에서 함께 사용하는 경우가 많다.
 * 코틀린은 lateinit가 지정된 프로퍼티와 가시성이 똑같은 필드를 생성해준다. 지연 초기화 프로퍼티가 public 이면 코틀린이 생성한 필드도 public 이다.
 *
 * lateinit 프로퍼티가 반드시 클래스의 멤버일 필요는 없으며, 함수 본문 안의 지역 변수나 최상위 프로퍼티도 지연 초기화할 수 있다.
 */
class MyService2 {
    fun performAction(): String = "Action Done!"
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTest2 {
    private lateinit var myService: MyService

    @BeforeAll
    fun setUp() { // setup 메서드에 프로퍼티의 초기화 진행
        myService = MyService()
    }

    @Test fun testAction() { // 널 검사를 수행하지 않고 프로퍼티를 사용
        assertEquals("Action Done!", myService.performAction())
    }
}