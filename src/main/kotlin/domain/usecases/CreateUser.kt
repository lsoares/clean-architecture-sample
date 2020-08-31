package domain.usecases

import domain.model.User
import domain.ports.UserRepository

class CreateUser(private val userRepo: UserRepository) {

    operator fun invoke(user: User) {
//        println(user)
        userRepo.save(user)
    }
}
