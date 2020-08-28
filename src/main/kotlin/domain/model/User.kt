package domain.model

import java.util.*

data class User(
    var id: String? = null,
    val email: Email,
    val name: String,
    val password: Password
) {

    init {
        if (id == null) id = IdGenerator.generate()
        require(name.length >= 2) { throw InvalidUser() }
    }

    class InvalidUser : Exception()
}

object IdGenerator {
    fun generate() = UUID.randomUUID().toString()
}
