package ch07.ex08

/**
 * 널이 될 수 있는 값을 널이 아닌 값만 인자로 받는 함수에 넘기려고 할 때, 안전하지 않기 때문에 컴파일러는 호출을 허용하지 않는다.
 * 코틀린 언어는 이런 경우 특별한 지원을 제공하지 않지만 표준 라이브러리에 도움이 될 수 있는 let 함수를 제공한다.
 *
 * 7.8.1
 * let 함수를 안전한 호출 연산자와 함께 사용하면 원하는 식을 평가해서 결과가 null 인지 검사한 다음 그 결과를 변수에 넣는 작업을 간단한 식을 사용해 한꺼번에 처리할 수 있다.
 * let을 사용하는 가장 흔한 사례는 널이 될 수 있는 값을 널이 아닌 값만 인자로 받는 함수에 넘기는 경우다.
 *
 * let 함수를 통해 인자를 전달할 수 있으며, let 함수는 자신의 수신 객체를 인자로 전달받은 람다에 넘긴다.
 * 널이 될 수 있는 값에 대해 안전한 호출 구문을 사용해 let을 호출하되 널이 아닌 타입을 인자로 받는 람다를 let에 전달한다.
 * 이렇게 하면 널이 될 수 있는 타입의 값을 널이 될 수 없는 타입의 값으로 바꿔 람다에 전달하게 된다.
 *
 * 7.8.2
 * 아주 긴 식이 있고 그 값이 null이 아닐 때 수행해야 하는 로직이 있을 때 let을 쓰면 더 편리하다. let을 쓰면 긴 식의 결과를 저장하는 변수를 따로 만들 필요가 없다.
 *
 * 여러값이 null인지 검사해야 한다면 let 호출을 내포시켜 처리할 수 있다. 그렇게 let을 내포시켜 처리하면 코드가 복잡해져서 알아보기 어려워진다.
 * 그런 경우 일반적인 if를 사용해 모든 값을 한꺼번에 검사하는 편이 낫다.
 *
 *
 * ########################################
 * 코틀린의 영역함수 비교: with, apply, let, run, also를 언제 사용할까?
 * 이 영역 함수들은 코드 블록을 어떤 객체의 맥락에서 실행해준다. 이들은 람다 안에서 대상 객체를 어떻게 점근하는지와 반환값이 무엇인지에 따라 달라진다.
 *
 * 함수           | x를 어떤 방식으로 참조하는가 | 반환값
 * x.let {...}   | it                    | 람다의 결과
 * x.also {...}  | it                    | x
 * x.apply {...} | this                  | x
 * x.run {...}   | this                  | 람다의 결과
 * x.with {...}  | this                  | 람다의 결과
 *
 * - 다루는 객체가 null이 아닌 경우에만 코드 블록을 실행하고 싶으면 let을 안전한 호출 연산자 ?.와 함깨 사용하라.
 *   어떤 식의 결과를 변수에 담되 그 영역을 한정시키고 싶을 때는 let을 독립적으로 사용하라
 * - 빌더 스타일의 API(예: 인스턴스 생성)를 사용해 객체 프로퍼티를 설정할 때는 apply를 사용하라
 * - 객체에 어떤 동작을 실행한 후 원래의 객체를 다른 연산에 사용하고 싶을 때 also를 사용하라
 * - 하나의 객체에 대해 이름을 반복하지 않으면서 여러 함수 호출을 그룹으로 묶고 싶을 때 with를 사용하라
 * - 객체를 설정한 다음에 별도의 결과를 돌려주고 싶을 떄 run을 사용하라
 *
 * 여러 영역함수의 용법은 세부 사항에 따라 달라진다. 따라서 둘 이상의 영역 함수가 비슷하게 잘 들어맞는 것으로 생각되는 경우도 생길 것인데, 팀에서 관례를 정해서 사용하라
 * ########################################
 */
fun sendEmailTo(email: String) {
    println("Sending email to $email")
}

class Person(val name: String, val email: String)

fun getTheBestPersonInTheWorld(email: String?): Person? =
    if (email != null) Person("unknown", email) else null

fun main() {
    var email: String? = "foo@bar.com"
    // sendEmailTo(email) // 널이 될 수 있는 타입의 값을 넘길 수 없다. 넘기려면 널을 검사해야 한다.
    if (email != null) sendEmailTo(email)

    // 7.8.1
    // let 함수는 이메일 주소 값이 널이 아닌 경우에만 호출된다. 다음 코드는 람다 안에서는 널이 아닌 타입으로 email을 사용할 수 있다.
    email?.let { email -> sendEmailTo(email) }

    // it를 사용하는 단축 구문을 쓰면 더 짧은 코드도 가능하다.
    email?.let { sendEmailTo(it) }
    email = null
    email?.let { sendEmailTo(it) } // 동작 x

    val email2 = "let@bar.com"
    // 7.8.2
    val person: Person? = getTheBestPersonInTheWorld(email2)
    if (person != null) sendEmailTo(person.email)

    // 별도의 변수를 사용하지 않는 식 비교
    // 다음 getTheBestPersonInTheWorld() 함수는 null을 반환하므로 람다가 실행되지 않는다.
    getTheBestPersonInTheWorld(null)?.let { sendEmailTo(it.email) }
    // 람다가 실행된다.
    getTheBestPersonInTheWorld(email2)?.let { sendEmailTo(it.email) }
}