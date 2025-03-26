package ch04.ex03

/**
 * second
 * 대규모 객체지향 시스템을 설계할 때 취약한 문제는 보통 상속에서 발생한다.
 * 상위 클래스의 메서드의 일부를 오버라이딩하면 하위 클래스는 상위 클래스의 세부 구현 사항에 의존하게 된다.
 * 상위 클래스에서 변경이 발생하면 구현 클래스들이 정상 작동하지 못하는 경우가 생길 수 있다.
 *
 * 코틀린은 이 문제를 인식하고 클래스가 기본적으로 final이다. 상속하려면 open 변경자를 사용해야 한다.
 *
 * 상속을 허용하지 않는 클래스에 새로운 동작을 추가해야 할 때 일반적인 방법은 데코레이터 패턴을 사용하는 것이다.
 * 이 패턴의 핵심은 상속을 허용하지 않는 기존 클래스 대신 사용할 수 있는 새로운 클래스를 만들되,
 * 기존 클래스와 같은 인터페이스를 데코레이터가 제공하고 기존 클래스를 데코레이터 내부 필드로 유지하는 것이다.
 * 새로 정의해야 하는 기능은 데코레이터의 메서드로 새로 정의하고 기존 기능이 그대로 필요한 부분은 데코레이터 메서드가 기존 클래스의 메서드에게 요청을 전달(forward)한다.
 *
 * 단점은 준비 코드가 상당히 많이 필요하다는 점이다.
 */

/**
 * 아래처럼 단순한 인터페이스를 구현하는 데코레이터를 만들면서 아무 행동을 변경하지 않아도 복잡한 코드가 작성된다/
 */
class DelegatingCollection<T> : Collection<T> {
    private val innerList = arrayListOf<T>()

    override val size: Int = innerList.size
    override fun isEmpty(): Boolean = innerList.isEmpty()
    override fun iterator(): Iterator<T> = innerList.iterator()
    override fun containsAll(elements: Collection<T>): Boolean = innerList.containsAll(elements)
    override fun contains(element: T): Boolean = innerList.contains(element)
}

/**
 * 이런 위임을 언어가 제공하는 일급 시민 기능으로 지원하는 점이 코틀린의 장점이다.
 * 인터페이스를 구현할 때 by 키워드를 통해 그 인터페이스에 대한 구현을 다른 객체에 위임 중이라는 사실을 명시할 수 있다.
 *
 * 클래스 안의 모든 메서드 정의는 없어졌다.
 * 컴파일러가 그런 전달 메서드를 자동으로 생성하며 자동 생성한 코드의 구현은 DelegatingCollection과 비슷하다.
 * 메서드 중 일부의 동작을 변경하고 싶은 경우 오버라이드 하면 그 메서드가 사용된다.
 */
class DelegatingCollection2<T>(
    innerList: Collection<T> = mutableListOf<T>()
): Collection<T> by innerList

/**
 * add(), addAll() 메서드를 오버라이드 해서 카운터를 증가시키고, MutableCollection 인터페이스의 나머지 메서드는 내부 컨테이너(innerSet)에 위임한다.
 * CountingSet에 MutableCollection의 구현 방식에 대한 의존관계가 생기지 않는다는 점이 중요하다. 구현은 간단히 했지만 최적화를 한 코드를 작성하면 된다.
 * 클라이언트 코드가 CountingSet의 코드를 호출할 때 발생하는 일은 CountingSet 안에서 제어할 수 있지만
 * CountingSet 코드는 위임 대상 내부 클래스인 MutableCollection에 문서화된 API를 활용해 기능을 구현한다.
 * MutableCollection이 변경되지 않는한 CountingSet 코드는 정상적으로 동작한다.
 */
class CountingSet<T>(
    private val innerSet: MutableCollection<T> = hashSetOf<T>()
) : MutableCollection<T> by innerSet {
    var objectsAdded = 0

    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        objectsAdded += elements.size
        return innerSet.addAll(elements)
    }
}