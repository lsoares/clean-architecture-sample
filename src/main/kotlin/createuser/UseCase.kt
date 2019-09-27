package createuser

import domain.entities.UserToCreate
import repository.UserRepositoryCrud

class UseCase(private val userRepo: UserRepositoryCrud, private val passwordEncoder: PasswordEncoder) {

    fun createUser(user: UserToCreate) {
        userRepo.create(user.copy(password = passwordEncoder.encode(user.password)))
    }
}
