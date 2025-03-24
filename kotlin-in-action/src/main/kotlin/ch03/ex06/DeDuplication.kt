package ch03.ex06

/**
 * 코드의 리펙터링을 하는 방법 중 중복을 제거하는 것이 있다.
 * 하지만 메서드를 기능별로 세분화하다보면 코드가 가독성이 떨어지거나, 각 메서드 사이의 관계를 파악하기 힘들 수 있다.
 *
 * 코틀린에서는 함수에서 추출한 함수를 원래의 함수 내부에 내포시킬 수 있다. 이를 통해 문법적인 부가 비용을 들이지 않고 깔끔하게 코드를 조직할 수 있다.
 */
class User (val id: Int, val name: String, val address: String)

/**
 * 코드 중복 예제
 */
fun saveUser(user: User) {
    if (user.name.isEmpty()) {
        throw IllegalArgumentException("Can't save user ${user.id}: empty name")
    }

    if (user.address.isEmpty()) {
        throw IllegalArgumentException("Can't save user ${user.id}: empty address")
    }

    // save user
}

/**
 * 로컬함수를 사용한 코드 중복 제거 및 깔끔한 코드 구조 유지 v2
 * User 객체를 로컬 함수에게 일일이 전달해야 하는 단점이 존재한다.
 * 로컬 함수는 자신이 속한 바깥 함수의 모든 파라미터와 변수를 사용할 수 있다.
 */
fun saveUser2(user: User) {
    fun validate(user: User, value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can't save user ${user.id}: empty $fieldName")
        }
    }

    validate(user, user.name, "Name")
    validate(user, user.address, "Address")

    // save user
}

/**
 * 로컬함수를 사용한 코드 중복 제거 및 깔끔한 코드 구조 유지 v3
 * 로컬 함수로 바깥 함수의 파라미터로 접근
 */
fun saveUser3(user: User) {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can't save user ${user.id}: empty $fieldName")
        }
    }

    validate(user.name, "Name")
    validate(user.address, "Address")

    // save user
}

/**
 * 검증 로직을 확장함수로 추출
 * this는 생략 가능하다.
 *
 * 확장 함수를 사용하면 객체.멤버 형태로 수신 객체를 지정하지 않고도 공개된 멤버 프로퍼티나 메서드에 접근할 수 있다.
 */
fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can't save user ${this.id}: empty $fieldName")
        }
    }

    validate(this.name, "Name")
    validate(this.address, "Address")

    // save user
}

fun saveUser4(user: User) {
    user.validateBeforeSave()
}
