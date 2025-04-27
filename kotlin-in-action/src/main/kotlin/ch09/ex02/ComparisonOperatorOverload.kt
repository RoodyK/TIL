package ch09.ex02

/**
 * 코틀린은 산술 연산자와 마찬가지로 모든 객체에 대해 비교 연산(==, !=, >, < 등)을 수행할 수 있다.
 * 자바는 equals, compareTo 함수를 사용하지만, 코틀린은 == 비교 연산자를 직접 사용할 수 있어 간결하고 이해하기 쉽다.
 */

/**
 * 동등선 연산자: equals
 *
 * 코틀린은 ==, != 호출을 equals 메서드 호출로 컴파일한다.
 * ==, != 연산은 내부에서 인자가 null인지 검사하므로 다른 연산과 달리 널이 될 수 있는 값에도 적용할 수 있다.
 * a == b 에서 a가 null인지 판단하고 null이 아닌 경우에 a.equals(b)를 호출한다. 양쪽 인자가 모두 null 이면 true 이다.
 */
class Point(val x: Int, val y: Int) {
    override fun equals(obj: Any?): Boolean {
        if (obj === this) return true
        if (obj !is Point) return false
        return obj.x == x && obj.y == y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}

/**
 * 코틀린의 동등성 연산자(===)는 자바의 == 연산자와 같다. 객체 참조를 비교한다.
 * equals 함수는 다른 연산자 오버로딩 관례와 달리 Any에 정의된 메서드이므로 override가 필요하다.
 * 상위 클래스에서 정의된 메서드를 오버라이드한다는 사실을 알면 equals 앞에 operator 키워드를 붙이지 않는 이유를 알 수 있다.
 * Any의 equals에는 operator가 붙어있지만 그 메서드를 오버라이드하는 메서드 앞에는 operator 변경자를 붙이지 않아도 자동으로 상위 클래스의 operator 지정이 적용된다.
 * 또한 Any에서 상속받은 equals가 확장 함수보다 우선순위가 높기 때문에 equals를 확장 함수로 정의할 수 없다는 사실에 유의하라.
 */

/**
 * 순서 연산자: compareTo(>, <, >=, <=)
 *
 * 자바에서 정렬, 최댓값, 최솟값 등 비교 알고리즘에 사용할 클래스는 Comparable 인터페이스의 compareTo 메서드를 구현해야 한다.
 * 자바에서는 compareTo 메서드를 짧게 호출할 수 있는 방법이 없다. >, < 연산은 기본 타입만 비교 가능하다.
 *
 * 코틀린도 Comparable 인터페이스를 지원하며 compareTo 메서드를 호출하는 관례를 제공하고, 비교 연산자를 사용하는 코드를 compareTo 호출로 컴파일한다.
 * p1 < p2 는 p1.compareTo(p2] < 0 과 같다.
 *
 * 2차원에서 한 점을 다른 점과 비교할 수 있는 정해진 방법은 없으므로 Person 클래스를 만들어서 비교해본다.
 *
 * Comparable의 compareTo 메서드도 operator 변경자가 붙어있으므로 하위 클래스에서 오버라이드할 때 operator 변경자를 붙일 필요 없다.
 *
 * compareValuesBy 함수는 두 객체와 여러 비교 함수를 인자로 받는다.
 * 첫 번째 비교 함수에 두 객체를 넘겨 두 객체가 같지 않다는 결과(0이 아닌 값)가 나오면 그 결괏값을 즉시 반환하고,
 * 두 수객체가 같다는 결과(0)가 나오면 두 번째 비교 함수를 통해 두 객체를 비교한다.
 * 이런 식으로 두 객체의 대소를 알려주는 0이 아닌 값이 처음 나올 때까지 인자로 받은 함수를 차례로 호출해 두 값을 비교하며, 모든 함수가 0을 반환하면 0을 반환한다.
 *
 * 필드를 직접 비교하면 코드는 복잡해지지만 비교 속도는 훨씬 더 빨라진다. 처음에는 성능을 신경쓰지 말고, 성능이 문제가 될 때 점차 개선하라.
 */
class Person(val firstName: String, val lastName: String): Comparable<Person> {
    override fun compareTo(other: Person): Int {
        return compareValuesBy(this, other, Person::lastName, Person::firstName)
    }
}


fun main() {
    println(Point(10, 20) == Point(10, 20))
    println(Point(10, 20) != Point(5, 10))
    println(Point(10, 20) == null)
    println(null == Point(5, 10))


    println("")
    val p1 = Person("Robert", "Smith")
    val p2 = Person("Bob", "Johnson")
    println(p1 < p2)
}