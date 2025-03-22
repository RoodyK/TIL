package ch03.ex02

/**
 * 코틀린에서는 함수를 클래스안에 작성할 필요는 없다.
 */
fun main() {
    val list = listOf(1, 2, 3, 4)

    // 함수 호출 부분의 가독성이 좋지 않음, IDE가 인자를 알려주지만, 없는 IDE도 있다.
    println(joinToString(list, "; ", "(", ")"))
    // 코틀린에서는 작성한 함수를 호출할 때 함수에 전달하는 인자 중 일부 또는 전부의 이름을 명시할 수 있다. 인자의 순서도 변경 가능하다.
    println(joinToString(separator = "; ", prefix = "(", postfix = ")", collection = list))
    // 파라미터 기본값을 지정한 함수 사용
    println(joinToString2(list))
    println(joinToString2(list, "-", "[", "]"))
}

/**
 * 컬렉션의 요소를 구분자로 연결하는 함수
 */
fun<T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {

    val result = StringBuilder(prefix)

    for ((index, element) in collection.withIndex()) {
        if (index  > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

/**
 * 코틀린은 함수 선언에서 파라미터의 기본값을 지정할 수 있다.
 * 이는 오버로딩으로 인한 중복된 함수의 수를 줄일 수 있다.
 *
 * 자바에서는 함수 인자의 기본값이 없으므로 자바에서 사용할 경우 모든 인자를 명시해야 한다.
 * 코틀린 함수를 자주 호출해야 해서 자바 측에서 좀더 편하게 코틀린 함수를 호출하고 싶다면 @JvmOverloads 애노테이션을 함수에 추가한다. 자바에서 자동으로 오버로딩 함수를 추가한다.
 *
 */
@JvmOverloads
fun<T> joinToString2(
    collection: Collection<T>,
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {

    val result = StringBuilder(prefix)

    for ((index, element) in collection.withIndex()) {
        if (index  > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

