package domain

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.constraints.Email
import javax.validation.constraints.Size

data class UserEntity(
    val id: Int? = null,
    @field:Email val email: String,
    @field:Size(min = 2) val name: String,
    @field:Size(min = 5) val password: String? = null,
    var hashedPassword: String? = null
) {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    init {
        password?.let {
            hashedPassword = PasswordEncoder.encode(password)
            validator.validate(this).apply {
                if (isNotEmpty()) throw InvalidUser(this)
            }
        }
    }

    class InvalidUser(private val violations: MutableSet<ConstraintViolation<UserEntity>>) : Exception()
    class UserAlreadyExists : Exception()
}

object PasswordEncoder {
    fun encode(toEncode: String) = toEncode.hashCode().toString() // really bad encoding for the sake of example
}