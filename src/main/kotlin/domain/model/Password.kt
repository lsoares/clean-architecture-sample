package domain.model

import domain.model.Password.InvalidPassword
import java.lang.Exception

data class Password(val hashed: String) {
    class InvalidPassword : Exception()
}

private object PasswordEncoder {
    fun encode(toEncode: String) = toEncode.hashCode().toString() // really bad encoding for the sake of example
}

fun String.toPassword(): Password {
    require(length > 4) { throw InvalidPassword() }
    return Password(PasswordEncoder.encode(this))
}