package ch07.ex07

/**
 * 안전한 호출, 안전한 캐스트, 엘비스 연산자는 코틀린에서 자주 사용된다.
 * 하지만 때로는 코틀린의 null 처리 지원을 쓰는 대신, 직접 컴파일러에게 어떤 값이 실제로는 null이 아니라는 사실을 알려주고 싶은 경우에 이 정보를 컴파일러에게 넘길 수 있는지 확인한다.
 *
 * 널 아님 단언(not-null assertion)은 코틀린에서 널이 될 수 있는 타입의 값을 다룰 때 사용할 수 있는 도구중 가장 단순하면서 무딘 도구다.
 * 느낌표를 이중(!!)으로 사용하면 어떤 값이든 널이 아닌 타입으로 강제로 바꿀 수 있다. 널에 대해서 !!를 적용하면 NPE가 발생한다.
 *
 * 예외가 발생했을 !! 단언문이 위치하는 곳을 가리키는데, 이는 컴파일러에게 "나는 이 값이 null이 아님을 잘 알고 있다. 내가 잘못 생각했다면 예외가 발생해도 감수하겠다"라고 말하는 것이다.
 *
 * !! 는 무례해보이는 느낌으로 컴파일러에게 단언하는 것인데 이는 의도된 것으로, 코틀린 설계자는 컴파일러가 검증할 수 없는 단언을 사용하지 말고 더 나은 방법을 찾으라고 지시하는 것이다.
 */
fun ignoreNulls(str: String?) {
    val strNotNull: String = str!! // 예외는 이 지점을 가리킨다.
    println(strNotNull.length)
}

/**
 * !! 단언문은 null에 대해 사용해서 발생하는 예외의 스택 트레이스에는 어떤 파일의 몇 번째 줄인지에 대한 정보는 들어있지만 어떤 식에서 예외가 발생했는지에 대한 정보는 들어있지 않다.
 * 어떤 값이 null이었는지 확실히 하기 위해 여러 !! 단언문을 한 줄에 함께 쓰는 일을 피하면 가장 좋다.
 * person.company!!.address!!.country 같은 코드를 지양하라
 */
class SelectableTextList(val contents: List<String>, var selectedIndex: Int? = null)

class CopyRowAction(val list: SelectableTextList) {
    fun isActionEnabled(): Boolean = list.selectedIndex != null

    fun executeCopyRow() {
        val index = list.selectedIndex!!
        val value = list.contents[index]
        println(value)
    }
}

fun main() {
    ignoreNulls("abc")
    // ignoreNulls(null) // at ch07.ex07.NotNullAssertionKt.ignoreNulls(NotNullAssertion.kt:11)

    val selectableTextList = SelectableTextList(listOf("1", "2", "3", "4"), 2)
    val copyRowAction = CopyRowAction(selectableTextList)
    copyRowAction.executeCopyRow()
}