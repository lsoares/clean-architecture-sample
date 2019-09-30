package features

import domain.UserRepositoryCrud

class ListUsers(private val userRepo: UserRepositoryCrud) {
    fun execute() = userRepo.findAll()
}