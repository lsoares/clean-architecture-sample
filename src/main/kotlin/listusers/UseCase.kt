package listusers

import repository.UserRepositoryCrud

class UseCase(private val userRepo: UserRepositoryCrud) {
    fun list() = userRepo.findAll()
}