package ch03.ex01

import kotlin.reflect.typeOf


/**
 * 컬렉션 생성
 *
 * to는 언어가 제공하는 키워드가 아닌 일반함수다.
 *
 * 코틀린은 표준 자바 클래스를 사용한다.
 * 자바와 달리 코틀린 컬렉션 인터페이스는 디폴트로 읽기 전용이다.
 * - ex) map = class java.util.LinkedHashMap
 */
fun main() {
    // set 생성
    val set1 = setOf(1, 2, 3, 2)
    val set2 = hashSetOf(1, 2, 3)

    // 리스트 생성
    val list = listOf(1, 3, 4)
    val list2 = listOf(1 to "one", 2 to "two", 3 to "three")

    // 맵 생성
    val map = mapOf(1 to "one", 2 to "two", 3 to "three")

    println(set1)
    println(set2)
    println(list)
    println(list2)
    println(map)
    println(map.javaClass)

    val fruits = listOf("Apple", "Orange", "Cherry")

    println(fruits.shuffled())
}