package domain.usecases

import domain.model.*
import domain.ports.UserRepository

class CreateUser(private val userRepo: UserRepository, private val generateUserId: () -> UserId) {

    operator fun invoke(request: Request) = userRepo.save(
        User(
            id = generateUserId(),
            email = request.email.toEmail(),
            name = request.name,
            password = request.password.toPassword(),
        )
    )

    data class Request(
        val email: String,
        val name: String,
        val password: String
    )
}
