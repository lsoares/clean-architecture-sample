package features

import domain.UserEntity
import domain.UserRepository

class CreateUser(private val userRepo: UserRepository) {

    fun execute(user: UserEntity) {
        userRepo.save(user)
    }
}
