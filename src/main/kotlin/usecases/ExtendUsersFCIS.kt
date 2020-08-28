package usecases

import domain.User
import domain.UserRepository
import java.time.LocalDate.now

// http://www.javiercasas.com/articles/functional-programming-patterns-functional-core-imperative-shell

// Functional Core
object ExtendUsersFC {

    operator fun invoke(users: List<User>): List<User> =
        users
            .filter { it.validUntil < now() }
            .map { it.copy(validUntil = now().plusDays(1)) }
}

// Imperative Shell
class ExtendUsersIS(private val userRepo: UserRepository) {

    operator fun invoke() {
        val users = userRepo.findAll()
        val extended = ExtendUsersFC(users)
        extended.forEach { userRepo.save(it) }
    }
}
