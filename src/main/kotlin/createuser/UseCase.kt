package createuser

import domain.UserEntity
import domain.UserRepositoryCrud
import javax.validation.Validation

class UseCase(private val userRepo: UserRepositoryCrud, private val passwordEncoder: PasswordEncoder) {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    fun createUser(user: UserEntity) {
        validator.validate(user).apply {
            if (isNotEmpty()) throw InvalidUserException(this)
        }

        userRepo.save(user.copy(password = passwordEncoder.encode(user.password)))
    }
}
