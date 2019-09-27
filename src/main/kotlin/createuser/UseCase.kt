package createuser

import repository.UserRepositoryCrud

class UseCase(private val userRepo: UserRepositoryCrud, private val passwordEncoder: PasswordEncoder) {

    fun createUser(user: User) {
        userRepo.create(user.copy(password = passwordEncoder.encode(user.password)))
    }
}
