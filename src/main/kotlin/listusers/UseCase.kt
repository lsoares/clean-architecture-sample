package listusers

import domain.UserRepositoryCrud

class UseCase(private val userRepo: UserRepositoryCrud) {
    fun list() = userRepo.findAll()
}