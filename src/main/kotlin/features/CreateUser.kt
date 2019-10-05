package features

import domain.User
import domain.UserRepository

class CreateUser(private val userRepo: UserRepository) {

    fun execute(user: User) {
        userRepo.save(user)
    }
}
