package usecases

import domain.User
import domain.UserRepository

class CreateUser(private val userRepo: UserRepository) {

    operator fun invoke(user: User) {
        userRepo.save(user)
    }
}
