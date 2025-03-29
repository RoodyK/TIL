package ch05.ex04

/**
 * 코틀린 람다의 독특한 기능으로 수신 객체를 명시하지 않고 람다의 본문 안에서 다른 객체의 메서드를 호출할 수 있게 하는 '수신 객체 지정 람다'가 있다.
 * 코틀린 표준 라이브러리의 with, apply, also 함수가 사용된다.
 */

/**
 * 많은 언어가 객체의 이름을 반복하지 않고도 그 객체에 대해 다양한 연산을 수행하는 기능을 제공하는데, 코틀린은 언어 구성 요소가 아닌 with 함수로 제공한다.
 */
fun alphabet(): String {
    val result = StringBuilder()
    for (letter in 'A'..'Z') {
        result.append(letter)
    }
    result.append("\nNow I know the alphabet!")
    return result.toString()
}

/**
 * with 함수는 파라미터가 두 개 있으며, T 타입 파라미터와, 람다를 받는다. inline fun <T, R> with(receiver: T, block: T.() -> R): R
 * 람다를 괄호 밖으로 빼내는 관례를 사용해서 전체 함수 호출이 언어가 제공하는 특별 구문처럼 보인다. 소괄호 안에 써도 상관은 없다.
 *
 * with 함수는 첫 번째 인자로 받은 객체를 두 번째 인자로 받은 람다의 수신 객채로 만든다.
 * 람다 안에서 명시적인 this 참조를 사용해 그 수신 객체에 접근할 수 있다. this는 생략도 가능하다.
 *
 * 확장 함수 안에서도 this는 그 함수가 확장하는 타입의 인스턴스를 가리킨다. 어떤 의미에서 확장 함수를 수신 객체 지정 함수라고 할 수 있다.
 * 일반 함수: 일반 람다, 확장 함수: 수신 객체 지정 람다
 *
 * with에 인자로 넘긴 객체의 클래스와 with를 사용하는 코드가 들어있는 클래스 안에 이름이 같은 메서드가 있을 수 있다.
 * 그럴 때 this 참조 앞에 레이블을 붙이면 호출하고 싶은 메서드를 명확하게 정의할 수 있다.
 * OuterClass 클래스에 정의된 this를 호출한다면 this@OuterClass.toString()
 */
fun alphabet2(): String { // result 이름이 반복 사용됐는데, with 함수로 개선한다.
    val stringBuilder = StringBuilder()
    return with(stringBuilder) {
        for (letter in 'A'..'Z') {
            this.append(letter)
        }
        this.append("\nNow I know the alphabet!")
        this.toString() // 반환되는 값
    }
}

// stringBuilder 변수를 생략 가능하다
fun alphabet3(): String {
    return with(StringBuilder()) {
        for (letter in 'A'..'Z') {
            append(letter)
        }
        append("\nNow I know the alphabet!")
        toString()
    }
}


/**
 * with 함수의 리턴 결과값 대신 수신 객체가 필요할 때는 apply 라이브러리 함수를 사용한다.
 * apply 함수는 with와 거의 동일하며, 차이점은 항상 자신에게 전달된 객체(수신 객체)를 반환한다는 것이다.
 *
 * apply를 임의의 타입의 확장 함수로 호출할 수 있다. apply를 호출한 객체는 apply에 전달된 람다의 수신 객체가 된다.
 * 인스턴스를 만들면서 즉시 프로퍼티 중 일부를 초기화해야 하는 경우 apply가 유용하다. 자바에서는 보통 Builder가 이 역할을 한다.
 */
fun alphabet4(): String {
    return StringBuilder().apply {
        for (letter in 'A'..'Z') {
            append(letter)
        }
        append("\nNow I know the alphabet!")
    }.toString()
}

/**
 * with, apply 함수는 수신 객체 지정 람다를 사용하는 일반적인 예로, 더 구체적인 함수를 비슷한 패턴으로 사용할 수 있다.
 * buildString 함수로 alphabet 함수를 단순화한다. StringBuilder 객체를 생성하는 것과 toString을 호출하는 일을 알아서 해준다.
 */
fun alphabet5() = buildString {
    for (letter in 'A'..'Z') {
        append(letter)
    }
    append("\nNow I know the alphabet!")
}

/**
 * buildString 함수는 StringBuilder로 String을 만드는 경우 사용가능하다.
 * buildList, buildSet, buildMap 함수처럼 컬렉션 빌더 함수들도 있다.
 */
val fibonacci = buildList {
    addAll(listOf(1, 1, 2))
    add(3)
    add(index = 0, element = 3)
}

val fruits = buildSet {
    add("Apple")
    addAll(listOf("Apple", "Banana", "Cherry"))
}

val medals = buildMap<String, Int> {
    put("Gold", 1)
    putAll(listOf("Silver" to 2, "Bronze" to 3))
}

/**
 * apply와 같이 also 함수도 수신 객체를 받으며, 그 수신 객체에 대해 어떤 동작을 수행한 후 수신 객체를 돌려준다.
 * 주된 차이는 also의 람다 안에서는 수신 객체를 인자로 참조한다는 점이다. 파라미터 이름을 부여하거나 디폴트 이름인 it을 사용해야 한다.
 * 객체 자체의 프로퍼티나 함수를 다루는 동작이 아니라 원래의 수신 객체를 인자로 받는 동작을 실행할 때 also가 유용하다.
 * also를 코드에서 보면 어떤 효과를 추가로 수행하는 것으로 해석할 수 있다.
 * "그리고(also) 다음을 객체에게 수행한다."로 읽을 수 있다.
 */


fun main() {
    println(alphabet())

    println(fibonacci)

    // also 예제
    val fruits = listOf("Apple", "Banana", "Cherry")
    val uppercaseFruits = mutableListOf<String>()
    val reversedLongFruits = fruits
        .map { it.uppercase() }
        .also { uppercaseFruits.addAll(it) }
        .filter { it.length > 5 }
        .also { println(it) }
        .reversed()
    println(reversedLongFruits)
}