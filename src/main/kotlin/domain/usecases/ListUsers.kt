package domain.usecases

import domain.ports.UserRepository

class ListUsers(private val userRepo: UserRepository) {

    operator fun invoke() = userRepo.findAll()
}