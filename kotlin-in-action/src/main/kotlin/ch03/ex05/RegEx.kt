package ch03.ex05

/**
 * 코틀린과 자바의 문자열은 같기 때문에 상호 호환된다
 *
 * 자바의 split()은 정규식을 구분 문자열로 받아, 정규식에 따라 문자열을 나누기 때문에 마침표(.)는 모든 문자로 해석된다. 따라서 12.345를 split()하면 빈 배열이 출력된다.
 * 코틀린은 확장 함수로 이 문제를 해결한다. 파라미터 값에 따라 정규식인지 일반 텍스트인지 분리할 수 있다. 정규식 문법은 자바와 같다.
 *
 * 코틀린 표준 라이브러리에는 어떤 문자열에서 구분 문자열이 맨 나중(or 처음)에 나타난 곳 뒤(or 앞)의 부분 문자열을 반환하는 함수가 있다.
 * substringBeforeLast(): 구분 문자열이 맨 나중(or 처음)에 나타난 곳 앞의 부분 문자열
 * substringAfterLast: 구분 문자열이 맨 나중(or 처음)에 나타난 곳 뒤의 부분 문자열
 *
 * 코틀린에서는 정규식 없이도 문자열을 쉽게 파싱할 수 있다. 정규식은 강력하지만 알아보는 것이 쉽지 않아서 꼭 필요할때만 사용해야 한다.
 *
 * 3중 따옴표 문자열에서는 백슬래쉬를 포함한 어떤 문자도 이스케이프할 필요가 없다. 마침표를 이스케이스 하려면 \\.로 써야 하지만 3중 따옴표에서는 \.로 사용한다.
 * 3중 따옴표 문자열은 여러 줄 바꿈이 있는 텍스트를 쉽게 작성할 수 있다.
 * trimIndent()는 공백만으로 이뤄진 첫 번째 줄, 마지막 줄을 제거해준다.
 * 3중 따옴표 문자열에서는 이스케이를 사용할 수 없으므로 $ 를 사용한다.
 */
fun main() {
    println("12.345-6.A".split("[.\\-]".toRegex())) // toRegex() 함수를 통해 문자열을 정규식으로 변환
    println("12.345-6.A".split("."))

    // 간단한 경우 정규식을 쓰지 않아도 된다.
    println("12.345-6.A".split(".", "-")) // 여러 문자를 받을 수 있는 확장 함수를 제공한다.

    val path = "/Users/roody/kotlin-book/chapter.txt"
    val directory = path.substringBeforeLast("/") // 구분자 이전 문자열
    val fileName = path.substringAfterLast("/") // 구분자 이후 문자열
    val fullNameWithoutExt = path.substringBeforeLast(".")
    val fileExt = path.substringAfterLast(".")
    println(directory)
    println(fileName)
    println(fullNameWithoutExt)
    println(fileExt)

    // 3중 따옴표 문자열
    // 아래의 정규식은 디렉터리, 파일명, 확장자로 분리한다.
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
        println("Dir: $directory, name: $filename, extention: $extension")
    }

    val multiline = """
        hello
        my
        name
        is
        kang
    """.trimIndent()

    println(multiline)
}