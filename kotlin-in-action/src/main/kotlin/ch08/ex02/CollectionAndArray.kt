package ch08.ex02

/**
 * 널이 될 수 있는 값의 컬렉션과 널이 될 수 있는 컬렉션
 *
 * 널 가능성은 타입 시스템의 일관성을 지키기 위해 필수적으로 고려해야 할 사항이다.
 * 컬렉션에 null을 넣을 수 있는지 여부는 어떤 변수의 값이 null이 될 수 있는지 여부와 마찬가지로 중요하다.
 * 변수와 같이 타입 인자로 쓰인 타입에도 같은 표시를 사용할 수 있다.
 */
fun readNumbers(text: String): List<Int?> { // 널이 될 수 있는 값으로 이뤄진 컬렉션 생성
    val result = mutableListOf<Int?>()
    for (line in text.lineSequence()) {
        val numberOrNull = line.toIntOrNull()
        result.add(numberOrNull)
    }
    return result
}

/**
 * 변수 타입의 널 가능성과 타입 파라미터로 쓰이는 타입의 널 가능성 사이의 차이를 알아야 한다.
 * 1) val list1: List<Int?> = null // 불가능. list의 인자로 받는 Int 값의 null을 허용하는 것이다.
 * 2) val list2: List<Int>? = null // 가능
 *
 * 첫 번째 경우 리스트 자체는 항상 null이 아니다. 리스트의 원소는 null 일 수 있다.
 * 두 번째 경우 리스트를 가리키는 변수에는 null이 들어갈 수 있지만, 리스트 안의 원소는 null이 아닌 값만 들어간다.
 *
 * 함수형 프로그래밍과 람다의 map 함수로 리스트를 반환하는 로직을 더 단축시킬 수 있다.
 */
fun readNumbers2(text: String): List<Int?> = text.lineSequence().map { it.toIntOrNull() }.toList()

/**
 * 경우에 따라 널이 될 수 있는 값과 널이 될 수 있는 리스트를 정의해야 한다. 이렇게 하면 리스트의 각 원소도 없음을 표현할 수 있고 전체 리스트 자체도 없음을 표시할 수 있다.
 * 코틀린은 List<Int?>?로 이를 표현한다. 안쪽 물음표는 원소의 널 가능성, 바깥쪽 물음표는 리스트 자체의 널 가능성을 표현한다.
 */
fun addValidNumbers(numbers: List<Int?>) {
    var sumOfValidNumbers = 0
    var invalidNumbers = 0

    for (number in numbers) {
        if (number != null) {
            sumOfValidNumbers += number
        } else {
            invalidNumbers++
        }
    }

    println("Sum of valid numbers: $sumOfValidNumbers")
    println("Invalid numbers count: $invalidNumbers")
}

/**
 * 널이 될 수 있는 값으로 이뤄진 컬렉션으로 null 값을 걸러내는 경우가 자주 있어서 코틀린 표준 라이브러리는 filterNotNull 함수를 제공한다.
 * filterNotNull 함수가 컬렉션 안에 null이 들어있지 않음을 보장하므로 validNumbers는 List<Int> 타입이 된다.
 */
fun addValidNumbers2(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()
    println("Sum of valid numbers: ${validNumbers.sum()}")
    println("Invalid numbers count: ${numbers.size - validNumbers.size}")
}

/**
 * 읽기 전용 컬렉션과 변경 가능한 컬렉션
 *
 * 자바와 코틀린의 컬렉션을 나누는 가장 중요한 특성 중 하나는 코틀린에서는 컬렉션 안의 데이터에 접근하는 인터페이스와 컬렉션 안의 데이터를 변경하는 인터페이스를 분리했다는 것이다.
 * 이런 구분은 코틀린 컬렉션을 다룰 때 사용하는 가장 기초 인터페이스인 kotlin.collections.Collection부터 존재한다.
 *
 * Collection 인터페이스를 사용해 컬렉션 안의 원소를 반복하고, 컬렉션 크기를 얻고, 어떤 값이 컬렉션 안에 들어있는지 검사하고, 컬렉션 데이터를 읽는 다른 여러 연산을 수행할 수 있다.
 * Collection 인터에피으에는 원소를 추가하거나 제거하는 메서드가 없다.
 *
 * 컬렉션의 데이터를 수정하려면 kotlin.collections.MutableCollection 인터페이스를 사용한다.
 * 이는 Collection 인터페이스를 확장하면서 원소 추가, 삭제, 모든 원소 제거 등의 메서드를 좀 더 제공한다.
 *
 * 가능하면 코드에서 항상 읽기 전용 인터페이스를 사용하는 것을 일반적인 규칙으로 삼아야 하고, 코드가 컬렉션을 변경할 필요가 있을 때만 변경 가능한 버전을 사용하자.
 *
 * 컬렉션을 읽기 전용(Collection), 변경 가능(MutableCollection)을 구분하는 이유는 프로그램에서 데이터에 어떤 일이 벌어지는지 쉽게 이해하기 위함이다.
 */
fun <T> copyElements(source: Collection<T>, target: MutableCollection<T>) {
    for (item in source) {
        target.add(item)
    }
}

/**
 * 컬렉션 인터페이스를 사용할 때 항상 염두해야하는 것은 읽기 전용 컬렉션이라도 꼭 변경 불가능한 컬렉션일 필요는 없다는 점이다.
 * 읽기전용 인터페이스 타입인 변수를 사용할 때 그 인터페이스는 실제로는 어떤 컬렉션 언스턴스를 가리키는 수많은 참조 중 하나일 수 있다.
 *
 * 코드의 일부분이 가변 컬렉션에 대한 참조를 갖고 있고 다른 부분에서 같은 컬렉션에 대한 '뷰'를 갖고 있다면 후자의 코드는 전자가 컬렉션을 동시에 변경할 수 없다는 가정에 의존할 수 없다.
 * 같은 컬렉션 객체를 가리키는 다른 타입(읽기 전용과 변경 가능 리스트)의 참조들이 있을 때 list에 접근하는 코드는 컬렉션 변경이 안되지만 mutableList는 컬렉션이 변경 가능하다.
 * 이 경우 코드가 컬렉션을 사용하는 도중에 다른 스레드에 의해 컬렉션이 변경되는 상황이 생기면, ConcurrentModificationException나 다른 오류가 발생할 수 있다.
 * 읽기 전용 컬렉션이 항상 스레드 안전하지 않다는 점을 명시해야 한다. 우리의 함수가 얻은 컬렉션의 '뷰'가 실제로는 내부에서 변경 가능한 컬렉션을 가리킬 수 없다.
 * 따라서 다중 스레드 환경에서 데이터를 다루는 경우 그 데이터를 적절히 동기화하거나 동시 접근을 허용하는 데이터 구조를 활용해야 한다.
 */

/**
 * 코틀린 컬렉션과 자바 컬렉션은 밀접히 연관
 *
 * 모든 코틀린 컬렉션은 그에 상응하는 자바 컬렉션 인터페이스의 인스턴스라는 점은 사실이다. 코틀린과 자바 사이를 오갈 때 아무 변환도 필요 없다.
 * 코틀린은 모든 자바 컬렉션 인터페이스마다 읽기 전용 인터페이스와 변경 가능한 인터페이스라는 2가지 표현을 제공한다. (List와 MutableList, Map과 MutableMap 등)
 *
 * 코틀린의 읽기 전용, 변경 가능 인터페이스의 구조는 java.util 패키지에 있는 자바 컬렉션 인터페이스의 구조와 같다.
 * 추가로 각 변경 가능 인터페이스는 자신과 대응하는 읽기 전용 인터페이스를 확장(상속)한다.
 * 변경 가능한 인터페이스는 java.util 패키지에 있는 인터페이스와 직접적으로 연관되지만 읽기 전용 인터페이스에는 컬렉션을 변경할 수 있는 모든 요소가 빠져있다.
 *
 * 예를 들어 코틀린에서 ArrayList와 HashSet 클래스는 코틀린의 MutableList, MutableSet 인터페이스를 상속한 것처럼 취급한다.
 * 이런 방식으로 코틀린은 자바 호환성을 제공하는 한편 읽기 전용 인터페이스와 변경 가능 인터페이스를 분리한다.
 *
 * 컬렉션 타입   | 읽기 전용 타입  | 변경 가능 타입
 * List       | listOf, List | mutableListOf, MutableList, arrayListOf, buildList
 * Set        | setOf        | mutableSetOf, hashSetOf, linkedSetOf, sortedSetOf, buildSet
 * Map        | mapOf        | mutableMapOf, hashMapOf, linkedMapOf, sortedMapOf, buildMap
 *
 * setOf(), MapOf() 함수는 Set, Map 읽기 전용 인터페이스의 인스턴스를 반환하지만 내부적으로는 변경 가능한 클래스다.
 * JVM에서 Collections.unmodifiable 호출 시 변경을 금지할 수 있지만 간접적인 부가비용 때문에 코틀린이 자동으로 이를 수행하지는 않는다.
 * 미래에는 불변으로 바뀔 수 있으니 변경 가능한 클래스라는 사실에 의존하지 말자
 *
 * 자바 컬렉션은 읽기 전용, 변경 가능한 컬렉션을 구분하지 않으므로 코틀린에서 읽기 전용으로 선언된 컬렉션도 자바 코드에서는 그 내용은 변경할 수 있고 코틀린을 이를 막을 수 없다.
 * 컬렉션을 자바로 넘기는 코틀린 프로그램을 작성하면 호출하려는 자바 코드가 컬렉션을 변경할지 여부에 따라 올바른 파라미터 타입을 사용할 책임은 개발자에 있다.
 */
fun printInUppercase(list: List<String>) {
    println(CollectionUtils.uppercaseAll(list))
    println(list.first())
}

/**
 * 자바에서 선언한 컬렉션은 코틀린에서 플랫폼 타입으로 보임
 *
 * 자바 코드에서 정의한 타입을 코틀린에서는 플랫폼 타입으로 본다.(자바는 null 안정성이 제공되지 않기 때문에, 코틀린 코드로 타입이 넘어올 때 null 안정성이 보장되지 않음)
 * 플랫폼 타입의 경우 코틀린에는 널 정보가 없기 때문에, 널이 될 수 있거나 없는 타입 어느쪽으로나 사용 가능하다.
 * 마찬가지로 자바 쪽에서 선언한 컬렉션 타입의 변수를 코틀린에서는 플랫폼 타입으로 보고, 이 컬렉션은 기본적으로 변경 가능성에 대해 알 수 없다.
 * 코틀린은 그 타입을 읽기 전용 컬렉션이나 변경 가능한 컬렉션 어느쪽으로든 다룰 수 있다.
 *
 * 컬렉션 타입이 시그니처에 들어간 자바 메서드 구현을 오버라이드하려는 경우 읽기 전용 컬렉션과 변경 가능 컬렉션의 차이가 문제가 된다.
 * 플랫폼 타입에서 널 가능성을 다룰 때처럼 이런 경우에도 오버라이드하려는 메서드의 자바 컬렉션 타입을 어떤 코틀린 컬렉션 타입으로 표현할지 결정해야 한다.
 * 이런 상황에서 선택해야 하는 사항
 * - 컬렉션이 null이 될 수 있는가?
 * - 컬렉션의 원소가 null이 될 수 있는가?
 * - 여러분이 작성한 메서드가 컬렉션을 변경할 수 있는가?
 */
class PersonParser : DataParser<Person> {
    override fun parseData(input: String, output: MutableList<Person>, errors: MutableList<String?>) {
        TODO("...")
    }
}

class Person(val name: String)

/**
 * 배열을 확인하기 전 배열보다는 컬렉션을 사용하는 것을 우선해야 한다. 하지만 자바 여러 자바 API에서 배열을 사용하는 경우가 있기 때문에 알아두면 좋다.
 *
 * 성능과 상호 운용을 위해 객체의 배열이나 원시 타입의 배열을 만들기
 *
 * 자바의 main 함수의 표준 시그니처에는 배열 파라미터가 들어있기 때문에 코틀린을 시작하자마자 코틀린 배열 타입과 마주치게 된다. fun main(args: Array<String>) {}
 *
 * 코틀린 배열은 타입 파라미터를 받아 원소 타입이 정해지는 클래스다.
 * 배열을 만드는 방법
 * - arrayOf 함수는 인자로 받은 원소들을 포함하는 배열을 만든다.
 * - arrayOfNulls 함수는 모든 원소가 null인 정해진 크기의 배열을 만들 수 있다. 원소 타입이 null이 가능해야 한다.
 * - Array 생성자는 배열 크기와 람다를 인자로 받아 람다를 호출해서 각 배열 원소를 초기화해준다. 원소를 하나하나 전달하지 않으며 원소가 널이 아닌 배열을 만들 때 이 생성자를 사용한다.
 *
 * 컬렉션을 배열로 변환하기
 * val strings = listOf("a", "b", "c")
 * println("%s%s%s".format(*strings.toTypedArray())) // vararg 인자를 넘기기 위해 스프레드 연산자(*)를 사용해야 한다.
 *
 * 배열 타입의 타입 인자도 항상 객체 타입으로 래퍼타입이 사용된다.
 * 코틀린은 원시 타입의 배열을 표현하는 별도 클래스를 각 원시 타입마다 하나씩 제공한다.
 * 정수형 IntArray, 바이트 ByteArray, 문자형 CharArray, 불리언 BooleanArray 등의 원시 타입 배열을 제공한다. 자바에서 int[], byte[] 등으로 컴파일된다.
 *
 * 원시 탑의 배열을 만드는 방법
 * - 각 배열 타입의 생성자는 size 인자를 받아 해당 원시 타입의 기본값(보통 0)으로 초기화된 size 크기의 배열을 반환한다.
 * - 팩토리 함수(IntArray를 생성하는 IntArrayOf 등)는 여러 값을 가변 인자로 받아 그런 값이 들어간 배열을 반환한다.
 * - 크기와 람다를 인자로 받는 다른 생성자를 사용한다.
 *
 * 배열 생성 코드 (2가지)
 * val fiveZeros = IntArray(5)
 * val fiveZeros2 = intArrayOf(0, 0, 0, 0, 0)
 *
 * 람다 사용
 * val squares = IntArray(5, {i -> (i + 1) * (i + 1)})
 * println(squares.joinToString(""))
 *
 * 박싱된 값이 들어있는 컬렉션이나 배열이 있다면 toIntArray 등의 변환 함수를 사용해 박싱하지 않은 원시 타입 값이 들어있는 배열로 변환할 수 있다.
 */

fun main(args: Array<String>) {
    val str = "123\naef\n54"
    println(readNumbers(str))

    val input = """
        1
        abc
        42
    """.trimIndent()
    val numbers = readNumbers(input)
    addValidNumbers(numbers)
    addValidNumbers2(numbers)

    val source: Collection<Int> = arrayListOf(3, 5, 7)
    val target: MutableCollection<Int> = arrayListOf(1)
    copyElements(source, target)
    println(target)

    // val target2: Collection<Int> = arrayListOf(1)
    // copyElements(source, target2) // Type mismatch Error.


    val list = listOf("apple", "banana", "cherry")
    printInUppercase(list)

    // main 함수 파라미터인 args 사용
    for (i in args.indices) { // 배열의 인덱스 값에 접근해 반복하기 위한 indices 확장함수 사용
        println("main Argument $i is : $args[i]")
    }

    // 배열 만들기
    val letters = Array<String>(26, {i -> ('a' + i).toString()})
    println(letters.joinToString(""))

    // 컬렉션을 배열로 변환
    val strings = listOf("a", "b", "c")
    println("%s%s%s".format(*strings.toTypedArray())) // vararg 인자를 넘기기 위해 스프레드 연산자(*)를 사용해야 한다.

    // 배열 생성
    val fiveZeros = IntArray(5)
    val fiveZeros2 = intArrayOf(0, 0, 0, 0, 0)
    println(fiveZeros.joinToString(","))
    println(fiveZeros2.joinToString(","))

    val squares = IntArray(5, {i -> (i + 1) * (i + 1)})
    println(squares.joinToString(""))

    // 배열에 forEachIndexed 사용
    squares.forEachIndexed({index, element -> println("Argument $index is: $element")})
}