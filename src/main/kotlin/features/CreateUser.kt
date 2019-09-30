package features

import domain.InvalidUser
import domain.UserEntity
import domain.UserRepositoryCrud
import javax.validation.Validation

class CreateUser(private val userRepo: UserRepositoryCrud, private val passwordEncoder: PasswordEncoder) {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    fun execute(user: UserEntity) {
        validator.validate(user).apply {
            if (isNotEmpty()) throw InvalidUser(this)
        }

        userRepo.save(
            user.copy(
                hashedPassword = passwordEncoder.encode(
                    user.password ?: throw RuntimeException("password missing")
                )
            )
        )
    }
}

class PasswordEncoder {
    fun encode(toEncode: String) = toEncode.hashCode().toString() // really bad encoding for the sake of example
}
