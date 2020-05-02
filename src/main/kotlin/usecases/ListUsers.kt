package usecases

import domain.UserRepository

class ListUsers(private val userRepo: UserRepository) {

    operator fun invoke() = userRepo.findAll()
}