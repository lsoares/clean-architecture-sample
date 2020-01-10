package users.usecases

import users.domain.UserRepository

class ListUsers(private val userRepo: UserRepository) {
    fun execute() = userRepo.findAll()
}