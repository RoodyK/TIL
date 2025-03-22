// @file:JvmName("someClass") // 소스 파일로 생성될 클래스 이름 지정

package ch03.ex02

/**
 * 정적인 유틸리티 클래스 없애기
 * 자바에서는 함수를 클래스에 속하게 작성해야 하는데 어느 한 클래스에 속하기 어려운 경우가 있다. 이렇게 되면 특별한 상태나 인스턴스 메서드가 없는 클래스가 생긴다.
 * Collections 와 같은 클래스가 그 예로, 보통 유틸리티 클래스들이 이런 식으로 작성된다.
 *
 * 코틀린에서는 이런 무의미한 클래스가 필요없다. 대신 함수를 직접 소스 파일의 최상위 수준, 모든 다른 클래스의 밖에 위치시키면 된다.
 *
 * JVM은 클래스 안에 들어있는 코드만을 실행할 수 있기 때문에, 파일을 컴파일하는 과정에 새로운 클래스가 만들어져야 한다.
 * 코틀린 파일을 컴파일한 결과를 보면 코틀린에서 함수가 들어있는 소스 파일의 이름을 딴 클래스를 만들고 그 하위에 정적인 메서드를 생성한다.
 * 얘를들어 현재 파일명은 OnlyFunction.kt 이면 OnlyFunctionKt.class가 만들어지며, 메서드는 public static String onlyJoinToString()이 된다.
 *
 * 애노테이션 @file:JvmName("someClass")를 사용하면 소스 파일로 생성될 클래스 이름을 지정할 수 있다.
 */

fun<T> onlyJoinToString(
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
 * 최상위 프로퍼티 생성
 *
 * 함수처럼 프로퍼티도 파일의 최상위 수준에 놓을 수 있다.
 *
 * 자바에서 public static final 필드로 노출하려면 const 변경자를 추가한다.
 */
var topCount = 0

// public static final String WELCOME_MESSAGE = "Hello Kotlin";
const val WELCOME_MESSAGE = "Hello Kotlin"