package ch05.ex01

/**
 * 코드에서 일련의 동작을 변수에 저장하거나 다른 함수에 넘겨야 하는 경우가 자주 있다. 자바는 익명 내부 클래스로 목적을 달성했지만 코드가 매우 번거로웠다.
 * 문제 해결을 위해 함수를 값(일급 시민)으로 다루며, 클래스의 인스턴스를 함수로 넘기는 대신, 함수를 직접 다른 함수에 전달하는 람다식으로 해결했다.
 *
 * 함수형 프로그래밍 특성
 * - 입급 시민: 함수의 파라미터에 함수를 전달하고 변수로 사용하며 함수의 반환값으로 다른 함수를 반환할 수 있다.
 * - 불변성: 객체가 만들어진 후에는 내부 상태가 변하지 않음을 보장한다.
 * - 사이드 이펙트 없음: 함수가 같은 입력에 대해 항상 같은 출력을 내놓고 다른 객체나 외부 세계의 상태를 변경하지 않게 구성한다. (순수 함수)
 */

/**
 * 코틀린은 람다로 컬렉션을 다룰 때 여러 기능을 제공하는 편리한 표준 라이브러리를 제공한다.
 *
 * 사람들로 이뤄진 리스트에서 가장 나이가 많은 사람을 찾기
 */
data class Person(val name: String, val age: Int)

/**
 * 람다는 값처럼 여기저기 전달할 수 있는 동작의 조각으로, 따로 선언해서 변수에 저장할 수도 있다.
 *
 * 람다식 선언 문법: { x: Int, y: Int -> x + y }
 * 코틀린 람다식은 항상 중괄호로 둘러싸여 있다. 인자 목록 주변에 괄호가 없다는 사실을 기억하자. 화살표로 인자목록과 본문을 구분한다.
 *
 * 람다식에서 파라미터 중 일부분만 타입을 지정하고 나머지는 타입을 지정하지 않아도 컴파일러가 추론해준다.
 * 컴파일러가 타입을 추론하지 못하는 경우는 나중에 설명한다. 우선 컴파일러가 타입을 불평하는 경우에는 타입을 명시한다는 규칙을 알아두자.
 * 람다의 디폴트 파라미터 이름인 it로 사용하면 람다식을 더 간단하게 만들 수 있다. (ex] it.age) 하지만 이 파라미터가 뭔지 알기 힘드므로 남용하는것은 좋지 않다.
 *
 * 람다를 변수에 저장할 때는 파라미터 타입을 추론할 문맥이 존재하지 않기 때문에 타입을 명시해야 한다.
 * val getAge = { p: Person -> p.age }
 *
 * 본문이 여러 줄로 이뤄진 경우 본문의 맨 마지막에 있는 식이 람다의 결과값이 된다.
 */

/**
 * 함수 안에서 익명 내부 클래스를 선언하면 그 클래스 안에서 함수의 파라미터와 로컬 변수를 참조할 수 있다.
 */
fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
    messages.forEach {
        println("$prefix $it") // 람다 안에서 함수의 파라미터 사용 (자신을 둘러싼 영역의 변수까지 사용 가능)
    }
}

/**
 * 함수 안에서 람다를 정의하면 파라미터 뿐만 아니라 람다 정의보다 앞에 선언된 로컬 변수까지 모두 사용할 수 있다.
 * 코틀린과 자바의 차이는 람다 안에서 final 변수가 아닌 변수에 접근할 수 있다는 것이다. (바깥의 변수를 변경할 수 있다.)
 * 람다 안에서 접근할 수 있는 외부 변수를 '람다가 캡처한 변수'라고 부른다.
 *
 * 로컬 변수의 생명 주기는 함수가 종료되면 끝나는데, 함수가 로컬 변수를 캡처한 람다를 반환하거나 다른 변수에 저장한다면 로컬 변수, 함수 각각의 생명 주기가 달라질 수 있다.
 * 캡처한 변수가 있는 람다를 저장해서 함수가 끝난 뒤에 실행해도 람다 본문 코드는 캡처한 변수를 읽거나 쓸 수 있다.
 * 파이널 변수를 캡처한 경우에는 람다 코드를 변수 값과 함께 저장하고, 일반 변수를 캡처한 경우에는 ㅇ특별한 래퍼로 감싸서 나중에 변경하거나 읽을 수 있게 한다.
 *
 * 람다를 이벤트 핸들러나 다른 비동기적으로 실행되는 코드로 활용하는 경우 로컬 변수 변경읜 람다가 실행될 때만 일어난다는 것을 알아야 한다.
 */
fun printProblemCounts(responses: Collection<String>) {
    var clientErrorCount: Int = 0
    var serverErrorCount: Int = 0
    responses.forEach {
        if (it.startsWith("4")) clientErrorCount++
        else if (it.startsWith("5")) serverErrorCount++
    }

    print("$clientErrorCount client errors, $serverErrorCount server errors")
}


/**
 * 람다로 코드 블럭을 함수의 인자로 넘겼는데, 이미 함수로 선언된 코드를 인자로 넘기려면 이중 콜론(::)을 통해 함수를 값으로 바꿀 수 있다.
 * val getAge = Person::age
 *
 * 이중 콜론(::)을 사용하는 식을 '멤버 참조'라고 한다. 멤버 참조는 정확히 한 메서드를 호출하거나 한 프로퍼티에 접근하는 함수 값을 만들어준다.
 * 참조 대상이 함수인지 프로퍼티인지와는 관계없이 멤버 참조 뒤에는 괄호를 넣으면 안된다. 해당 대상을 참조하는 것이지 호출하려는 것이 아니기 때문이다.
 *
 * 최상위에 선언된 함수나 프로퍼티를 참조할 수 도 있다.
 * run(::salute)
 */
fun salute() = println("Salute!")


/**
 * '값과 엮인 호출 가능 참조'를 사용하면 같은 멤버 참조 구문을 사용해 특정 객체 인스턴스의 메서드 호출에 대한 참조를 만들 수 있다.
 *
 * // 멤버 참조
 * val seb = Person("Sebastian", 22)
 * val personAgeFunction = Person::age
 * println(personAgeFunction(seb))
 * // 값과 엮인 호출 가능 참조
 * val sebAgeFunction = seb::age
 * println(sebAgeFunction())
 */

fun main() {
    // 표준 라이브러리를 사용
    val people = listOf(Person("Bob", 22), Person("Jane", 33))
    // 람다가 인자를 하나(컬렉션 원소)만 받고 그 인자에 구체적 이름을 붙이고 싶지 않기 때문에 it 라는 암시적 이름을 사용
    println(people.maxByOrNull { it.age })
    // 람다가 단순히 함수나 프로퍼티에 위임할 경우 멤버 참조 사용 가능
    println(people.maxByOrNull(Person::age))
    // 람다를 풀어낸 예 - 구분자가 너무 많아 가독성이 떨어지고 코드가 번잡하다. 컴파일러가 문맥으로부터 유추할 수 있는 인자 타입을 적을 필요는 없다
    println(people.maxByOrNull({ p: Person -> p.age }))
    // 람다가 유일한 인자이므로 마지막 인자이기도 해서 괄호 뒤에 람다를 둘 수 있다.
    println(people.maxByOrNull(){ p: Person -> p.age })
    // 람다가 어떤 함수의 윺일한 인자이고 괄호 뒤에 람다를 썼다면 호출 시 빈 괄호를 없에도 된다. 가장 읽기 쉬움
    println(people.maxByOrNull{ p: Person -> p.age })
    // 컴파일러는 람다 파라미터의 타입을 추론하기 때문에 명시할 필요는 없음
    println(people.maxByOrNull{ p -> p.age })

    // 람다식을 변수에 저장
    val sum = { x: Int, y: Int -> x + y}
    println(sum(1, 2))

    // 람다식을 직접 호출(권장 x)
    // { println(22) }()

    // 코드의 일부분을 블록으로 둘러쌀 필요가 있을 때 run은 인자로 받은 람다를 실행해 주는 라이브러리 함수다.
    val a = run{
        println("I'm thinking")
        42
    }
    println(a)

    // 이름 붙인 인자를 사용해 람다 넘기기
    val names = people.joinToString(
        separator = " ",
        transform = { p: Person -> p.name }
    )
    println(names)

    // 본문이 여러 줄로 이뤄진 경우 본문의 맨 마지막에 있는 식이 람다의 결과값이 된다.
    val sum2 = { x: Int, y: Int ->
        println("computing the sum...")
        x + y
    }
    println(sum2)

    val errors = listOf("403 Forbidden", "404 Not Found")
    printMessagesWithPrefix(errors, "Error:")

    val responses = listOf("200 OK", "403 Forbidden", "404 Not Found", "500 Internal Server Error")
    printProblemCounts(responses)

    // 최상위 선언 함수 람다 사용
    run(::salute)

    // 멤버 참조
    val seb = Person("Sebastian", 22)
    val personAgeFunction = Person::age
    println(personAgeFunction(seb))
    // 값과 엮인 호출 가능 참조
    val sebAgeFunction = seb::age
    println(sebAgeFunction())
}