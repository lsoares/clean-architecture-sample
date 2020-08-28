package domain.model

import java.util.*

data class User(
    var id: String? = null,
    val email: Email,
    val name: String,
    val password: String? = null,
    var hashedPassword: String? = null
) {

    init {
        if (id == null) id = IdGenerator.generate()

        require(name.length >= 2) { throw InvalidUser() }
        require(password == null || password.length >= 5) { throw InvalidUser() }

        password?.let {
            hashedPassword = PasswordEncoder.encode(password)
        }
    }

    class InvalidUser : Exception()
}

object PasswordEncoder {
    fun encode(toEncode: String) = toEncode.hashCode().toString() // really bad encoding for the sake of example
}

object IdGenerator {
    fun generate() = UUID.randomUUID().toString()
}
