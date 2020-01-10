package users.usecases

import users.domain.User
import users.domain.UserRepository

class CreateUser(private val userRepo: UserRepository) {

    fun execute(user: User) {
        userRepo.save(user)
    }
}
