package domain.model

import domain.ports.UserRepository
import java.util.*
import javax.validation.Validation
import javax.validation.constraints.Size

data class User(
    var id: String? = null,
    val email: Email,
    @field:Size(min = 2) val name: String,
    @field:Size(min = 5) val password: String? = null,
    var hashedPassword: String? = null
) {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    init {
        if (id == null) id = IdGenerator.generate()

        password?.let {
            hashedPassword = PasswordEncoder.encode(password)
            validator.validate(this).run {
                require(isEmpty()) { throw UserRepository.InvalidUser(this) }
            }
        }
    }
}

object PasswordEncoder {
    fun encode(toEncode: String) = toEncode.hashCode().toString() // really bad encoding for the sake of example
}

object IdGenerator {
    fun generate() = UUID.randomUUID().toString()
}
