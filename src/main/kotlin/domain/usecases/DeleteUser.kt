package domain.usecases

import domain.model.Email
import domain.ports.UserRepository

class DeleteUser(private val userRepo: UserRepository) {
    operator fun invoke(email: Email) {
        userRepo.delete(email)
    }
}