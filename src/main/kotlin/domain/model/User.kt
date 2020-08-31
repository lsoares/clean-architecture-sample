package domain.model

import java.util.*

data class User(
    var id: UserId = IdGenerator.generate().toUserId(),
    val email: Email,
    val name: String,
    val password: Password
) {
    init {
        require(name.length >= 2) { throw InvalidUser() }
    }

    class InvalidUser : Exception()
}

object IdGenerator {
    fun generate() = UUID.randomUUID().toString()
}

data class UserId(val value: String)

fun String.toUserId() = UserId(this)
