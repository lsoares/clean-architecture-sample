package domain.model

data class User(
    var id: UserId,
    val email: Email,
    val name: String,
    val password: Password
) {
    init {
        require(name.length >= 2) { throw InvalidUser() }
    }

    class InvalidUser : Exception()
}

data class UserId(val value: String)

fun String.toUserId() = UserId(this)
