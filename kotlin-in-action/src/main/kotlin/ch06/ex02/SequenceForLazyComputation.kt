package ch06.ex02

import java.io.File

data class Person(val name: String, val age: Int)

/**
 * map 이나 filter 컬렉션 함수들을 연쇄적으로 호출하는 경우는 결과 컬렉션을 즉시(eager)생성한다.
 * 이는 컬렉션 함수를 체이닝해서 사용하면 매 단계마다 계산 중간 결과를 새로운 컬렉션에 임시로 담는다.
 *
 * 시퀀스는 자바 8의 스트림과 비슷하게 중간 임시 컬렉션을 사용하지 않고 컬렉션 연산을 연쇄하는 방법을 제공한다.
 *
 * 코틀린 표준 라이브러이 참조 문서에서 filter, map 함수는 리스트릴 반환한다고 하며, 이를 체이닝하면 리스트를 2개 만들게 된다. 원소가 많은 리스트일수록 효율이 떨어진다.
 * 이를 해결하기 위해 각 연산이 컬렉션을 직접 사용하는 대신 시퀀스를 사용하게 해야 한다. people.asSequence().filter()....
 *
 * 코틀린 지연 계산 시퀀스는 Sequence 인터페이스에서 시작하며, interator라는 단 하나의 메서드를 통해 시퀀스에서 열거된 원소의 값들을 얻을 수 있다.
 * Sequence 인터페이스의 강점은 그 인터페이스 위에 구현된 연산이 계산을 수행하는 방법 때문에 생기는데, 시퀀스의 원소는 필요할 때 지연(lazy) 계산된다.
 * 중간 처리 결과 컬렉션을 만들지 않고 연산을 연쇄적으로 적용해서 효율적으로 수행할 수 있다.
 *
 * asSequence() 확장 함수를 호출해서 어떤 컬렉션이든 시퀀스로 바꿀 수 있다.
 * 시퀀스 연산을 마치고 원소를 차례로 순회한다면 시퀀스를 직접 써도 되지만, 원소의 인덱스를 사용해 접근 등 다른 API 메서드를 호출해야 한다면 toList()로 리스트로 변환해야 한다.
 *
 * 시퀀스의 연산은 중간 연산, 최종 연산이 있다.
 * 중간 연산은 최초 시퀀스의 원소를 변환하는 방법을 아는 다른 시퀀스를 반환한다. 중간 연산은 항상 지연 계산된다.
 * 최종 연산은 최초 컬렉션에 대해 변환을 적용한 시퀀스에서 일련의 계산을 수행해 얻을 수 있는 컬렉션이나 원소, 수, 또 다른 객체를 반환한다.
 *
 * 시퀀스를 사용하면 모든 체이닝된 함수에 대해서 원소가 순차적으로 연산을 처리한다.
 * 시퀀스를 사용하지 않으면 함수가 계산된 새로운 컬렉션이 만들어지고 다음 함수를 계산한다.
 *
 *
 * 시퀀스는 generateSequence 함수를 사용해서 만들 수 있다. 이 함수는 이전의 원소를 인자로 받아 다음 원소를 계산한다.
 */
fun main() {
    // 코드를 실행하면 아무 내용도 출력되지 않고, Sequence 객체 자체에 대한 출력을 볼 수 있다.
    // 이는 지연 계산이 적용돼 최종 연산이 호출될 때 코드가 수행됨을 알 수 있다.
    println(
        listOf(1, 2, 3, 4)
            .asSequence()
            .map {
                print("map($it)")
                it * it
            }
            .filter {
                println("filter($it)")
                it % 2 == 0
            }
    )

    // 연산이 처리될 때 네 개의 원소가 있다면 map, filter 함수에 수행될 연산이 map을 모두 수행하고 filter가 수행되는 것이 아니다.
    // 원소가 순차적으로 map, filter가 수행됨을 확인할 수 있다.
    // map(1)filter(1)
    // map(2)filter(4)
    // map(3)filter(9)
    // map(4)filter(16)
    println(
        listOf(1, 2, 3, 4)
            .asSequence()
            .map {
                print("map($it)")
                it * it
            }
            .filter {
                println("filter($it)")
                it % 2 == 0
            }
            .toList()
    )

    println(
        listOf(1, 2, 3, 4)
            .asSequence()
            .map { it * it }
            .find { it > 3 }
    )

    val people = listOf(
        Person("Bob", 25),
        Person("Jonny", 33),
        Person("Lee", 24),
        Person("Alice", 21)
    )

    println(
        people
            .asSequence()
            .map(Person::name)
            .filter { it.length < 4 }
            .toList()
    )
    println(
        people
            .asSequence()
            .filter { it.name.length < 4 }
            .map(Person::name)
            .toList()
    )

    // 시퀀스 만들기
    // 시퀀스를 만드는 행위는 모두 시퀀스를 지연 계산한다.
    val naturalNumbers = generateSequence(0, { it + 1 })
    val numbersTo100 = naturalNumbers.takeWhile { it <= 100 } // 100보다 작거나 같은 원소들만 남김
    // 최종연산
    println(numbersTo100.sum())

    // 조상 디렉터리의 시퀀스를 생성하고 사용
    val file = File("/Users/roody/.HiddenDir/a.txt")
    println(file.isInsideHiddenDirectory())
}

// 시퀀스를 사용해서 조건을 만족하는 디렉터리를 찾은 뒤에는 더 이상 상위 디렉터리를 뒤지지 않는다.
fun File.isInsideHiddenDirectory() =
    generateSequence(this, { it.parentFile }).any { it.isHidden }