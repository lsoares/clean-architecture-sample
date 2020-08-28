package usecases

import domain.UserPresenter
import domain.UserRepository

class ListUsers2(private val userRepo: UserRepository, private val userPresent: UserPresenter) {

    operator fun invoke() = userPresent.show(userRepo.findAll())
}
