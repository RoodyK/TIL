package ch06.ex01

data class Person(val name: String, val age: Int)

class Book(val title: String, val authors: List<String>)

/**
 * filter는 컬렉션 요소를 순회하며 조건에 맞는 요소만 필터링한다.
 * map은 입력 컬렉션의 원소를 변환한다.
 *
 * 걸러내거나 변환하는 연산이 원소의 값 뿐 아니라 인덱스에 따라서도 달라진다면 형제 함수인 filterIndexed와 mapIndexed를 사용하면 된다.
 * 이 함수는 제로 베이스 인덱스와 원소 자체를 함께 제공한다.
 *
 *
 * reduce와 fold는 컬렉션의 정보를 종합하는 데 사용한다.
 * 누산기(누적기)를 통해 컬렉션의 값을 점진적으로 누적된 하나의 값을 만든다.
 * reduce는 컬렉션의 첫 번째 값을 누적기에 넣고 그 후 람다가 호출되면서 누적값과 두 번째 원소가 인자로 전달된다.
 * fold는 첫 번째 원소를 누적 값으로 시작하는 대신, 임의의 시작 값을 선택할 수 있다.
 *
 * 중간 단계의 모든 누적값을 뽑아내고 싶다면 runningReduce, runningFold 를 사용한다.
 * runningReduce, runningFold는 결과를 리스트로 반환한다는 것만 다르다.
 *
 *
 * 컬렉션의 모든 원소가 어떤 조건을 만족하는지 판단하는 연산으로 all, any, none 이 있다.
 * count 함수는 조건(술어)을 만족하는 원소 개수를 반환한다.
 * find 함수는 조건을 만족하는 첫 번째 원소를 반환한다. 만족하는 원소가 없다면 null을 반환한다.
 * all 함수는 모든 원소가 이 조건을 만족하는지 판단한다.
 * any 함수는 조건을 만족하는 원소가 하나라도 있는지 판단한다.
 * none 함수는 모든 원소가 이 조건을 만족하지 않는지 판단한다.
 *
 * 컬렉션의 원소가 없을 때 (빈 컬렉션 일 때)
 * - any 함수는 만족하는 원소가 없으므로 false
 * - none 함수는 만족할수 있는 원소가 없기 때문에 true
 * - all 함수는 빈 컬렉션에 대해 항상 true 반환
 *
 *
 * 컬렉션을 어떤 술어를 만족하는 그룹과 그렇지 않은 그룹으로 나눌 필요가 있을 때 filter, filterNot 함수를 사용할 수 있다.
 * 보다 편하게 그룹을 나누려면 partition 함수를 사용하면 된다.
 *
 * 컬렉션을 partition이 반환하는 참과 거짓으로 이루어진 그룹으로 분리하는 것이 아닌, 원소를 어떤 특성에 따라 그룹화하고 싶을 때 groupBy 함수를 사용한다.
 *
 * associate 함수는 groupBy 함수처럼 원소를 그룹화하지 않고 Map으로 만들어내고 싶을 때 사용한다.
 * associateWith, associateBy 함수는 키와 커스텀 값의 쌍을 만들어내는 대신, 컬렉션의 원소와 다른 어떤 값 사이의 연관을 만들어내고 싶을 때 사용한다.
 *
 * associateWith 함수는 컬렉션의 원래 원소를 키로 사용하고, 우리가 만든 람다는 그 원소에 대응하는 값을 만든다.
 * associateBy 함수는 컬렉션의 원래 원소를 맵의 값으로 하고, 우리가 만든 람다의 값을 맵의 키로 사용한다.
 *
 *
 * replaceAll, fill 함수는 보통 불변 컬렉션을 권장하지만, 가변 컬렉션으로 작업하면 더 편리한 경우가 있을 때 사용한다.
 * replaceAll 함수는 람다로 얻은 결과로 컬렉션의 모든 원소를 변경한다.
 * fill 함수는 가변 리스트의 모든 원소를 똑같은 값으로 바꾸는 특별한 경우 사용한다.
 *
 *
 * ifEmpty 함수는 컬렉션이 비어있지 않은 경우에만 처리를 하는것이 타당한 경우 사용한다.
 *
 * chunked, windowed 함수는 컬렉션을 나눌 때 사용한다.
 * 컬렉션 값이 1,2,3,4,5가 있을 때 1,2,3 을 먼저 계산하고, 2,3,4를 계산하고 3,4,5를 나눠서 계산할 때 슬라이딩 윈도우를 사용하는데, windowed 함수가 그 예이다.
 * 입력 컬렉션에 슬라이딩 윈도우를 실행하는 대신, 컬렉션을 어떤 주어진 크기의 서로 겹치지 않는(서로소) 부분으로 나누고 싶을 때 chunked 함수를 사용한다.
 *
 *
 * zip 함수는 컬렉션을 합칠 때 사용한다.
 * 연관된 데이터가 들어있는 두 리스트를 종합해야 할 때,
 * 각 리스트의 값들이 서로의 인덱스에 따라 대응된다고 알고 있다면 zip 함수를 사용해서, 같은 인덱스에 있는 원소들의 쌍으로 이뤄진 리스트를 만들 수 있다.
 * 두 컬렉션의 길이가 다르다면 짧은 컬렉션의 길이에 맞춰진다. zip 함수도 중위함수 표기법을 사용할 수 있다. names zip ages
 * 두 개 이상의 컬렉션도 가능하지만 zip이 두 개의 리스트에만 동작하기 때문에 마지막 컬렉션을 제외한 컬렉션은 하나로 묶이게 된다.
 *
 *
 * 내포된 컬렉션의 원소를 처리할 때 flatMap, flatten 함수를 사용할 수 있다.
 * flatMap 함수는 우선 컬렉션의 각 원소를 파라미터로 주어진 함수를 사용해 변환(또는 매핑)하고, 그 후 변환한 결과를 하나의 리스트로 합친다(또는 펼친다).
 * flatten 함수는 변환할것이 없고 단지 컬렉션의 값을 평평한 컬렉션으로 만들려고 할 때 사용한다. listOfLists.flatten()
 */
fun main() {
    println("==filter==")
    val list = listOf(1, 2, 3, 4)
    println(list.filter { it % 2 == 0 })

    val people = listOf(Person("Bob", 22), Person("Mike", 33))
    println(people.filter { p -> p.age > 30 })
    // println(people.filter { it.age > 30 }) // 같은 의미

    println("==map==")
    val list2 = listOf(1, 2, 3, 4)
    println(list2.map { it * it })

    val people2 = listOf(Person("Bob", 22), Person("Mike", 33))
    println(people2.map { it.name })

    // 나이 가장 많은 사람 찾기
    // oldestPerson 을 구하는 것처럼 꼭 필요하지 않은 연산을 반복하지 말아야 한다.
    // 단순해보이는 식이 내부 로직의 복잡도로 인해 실제로 엄청 불합리한 계산식이 될 때가 있다.
    val oldestPerson = people2.maxByOrNull(Person::age)
    println(people2.filter {it.age == oldestPerson?.age})

    // filterIndexed, mapIndexed
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7)
    val filtered = numbers.filterIndexed {index, element ->
        index % 2 == 0 && element > 3
    }
    val mapped = numbers.mapIndexed {index, element ->
        index + element
    }
    println(filtered)
    println(mapped)

    // Map 에서 사용
    val numbersMap = mapOf(0 to "zero", 1 to "one")
    println(numbersMap.mapValues { it.value.toUpperCase() })

    // reduce, fold
    println("==reduce, fold==")
    val reduceSum = numbers.reduce { acc, element ->
        acc + element
    }
    val reduceMul = numbers.reduce { acc, element ->
        acc * element
    }
    println(reduceSum)
    println(reduceMul)

    val people3 = listOf(Person("Park", 20), Person("Mike", 30))
    val foldResult = people3.fold("initName", { acc, person ->
        acc + " " + person.name
    })
    // val foldResult = people3.fold("initName") { acc, person -> acc + person.name }
    println(foldResult)

    // runningReduce, runningFold
    val list3 = listOf(1, 2, 3, 4)
    val summed = list3.runningReduce { acc, element ->
        acc + element
    }
    val multiplied = list3.runningReduce { acc, element ->
        acc * element
    }
    println(summed)
    println(multiplied)

    val people4 = listOf(Person("Park", 20), Person("Mike", 30))
    println(people4.runningFold("initName", { acc, element ->
        acc + element.name
    }))

    // all, any, none
    // count, find
    val canBeInClub25 = { p: Person -> p.age <= 25 }
    val people5 = listOf(Person("Bob", 25), Person("Mike", 33))
    println("==all, any, none==")
    println(people5.all(canBeInClub25))
    println(people5.any(canBeInClub25))
    println(people5.none(canBeInClub25))

    val people6 = listOf(
        Person("Bob", 25),
        Person("Mike", 33),
        Person("Park", 24),
        Person("Lee", 21)
    )
    println("==count, find==")
    println(people6.count())
    println(people6.find(canBeInClub25))
    println(people6.find { it.age <= 20 })

    // partition
    // 두 그룹으로 분리
    val conditionIn = people6.filter(canBeInClub25)
    val conditionNotIn = people6.filterNot(canBeInClub25)
    println("==partition==")
    println(conditionIn)
    println(conditionNotIn)

    val (conditionIn2, conditionNotIn2) = people6.partition(canBeInClub25)
    println(conditionIn2)
    println(conditionNotIn2)

    // groupBy
    println("==groupBy==")
    val people7 = listOf(
        Person("Bob", 33),
        Person("Mike", 33),
        Person("Park", 21),
        Person("Lee", 21)
    )
    // 구분하는 특성에 따른 그룹 생성. age가 키가 되고 각 그룹이 값인 맵 => 결과 Map<Int, List<Person>>
    println(people7.groupBy {it.age })

    // Map<String, List<String>>
    val list4 = listOf("apple", "banana", "cherry", "avocado")
    println(list4.groupBy(String::first)) // first는 String의 확장함수

    println("==associate, associateWith, associateBy==")
    val people8 = listOf(Person("Park", 20), Person("Mike", 30))
    println(people8.associate { it.name to it.age }) // 좌항과 우항을 쌍으로 만드는 중위함수
    println(people8.associateWith { it.age })
    println(people8.associateBy { it.age })

    println("==replaceAll, fill==")
    val names = mutableListOf("Mark", "Park")
    names.replaceAll { it.toUpperCase() }
    println(names)
    names.fill("(redacted)")
    println(names)

    println("==ifEmpty==")
    val emptyList = emptyList<String>()
    val fullList = listOf("apple", "banana", "cherry", "avocado")
    println(emptyList.ifEmpty { listOf("no", "values", "here") })
    println(fullList.ifEmpty { listOf("no", "values", "here") })

    // windowed
    println("==windowed==")
    val temperatures = listOf(27.7, 29.8, 22.0, 35.5, 19.1)
    println(temperatures.windowed(3))
    println(temperatures.windowed(3, transform = { it.sum() / it.size }))
    println(temperatures.windowed(3){ it.sum() / it.size })
    // chunked
    println(temperatures.chunked(2))
    println(temperatures.chunked(2, transform = { it.sum() }))
    println(temperatures.chunked(2){ it.sum() })

    // zip
    println("==zip==")
    // 반대편에 대응되는 컬렉션에 값이 없다면 무시한다.
    val names2 = listOf("Mark", "Park", "Mike")
    val ages2 = listOf(22, 33, 44, 55, 0)
    println(names2.zip(ages2))
    println(names2.zip(ages2, { name,age -> Person(name, age)}))
    println(names2 zip ages2)

    // flatMap, flatten
    println("==flatMap, flatten==")
    val library = listOf(
        Book("Kotlin in Action", listOf("Isakova", "Elizarov", "Aigner", "Jemerov")),
        Book("Atomic Kotlin", listOf("Eckel", "Isakova")),
        Book("The Three-Body Problem", listOf("Liu"))
    )

    val authors = library.flatMap { it.authors }
    println(authors)
    println(authors.toSet())
}