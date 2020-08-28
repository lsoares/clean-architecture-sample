package domain.ports

import domain.model.User
import javax.validation.ConstraintViolation

interface UserRepository {
    fun findAll(): List<User>
    fun save(user: User)

    class InvalidUser(private val violations: MutableSet<ConstraintViolation<User>>) : Exception()
    class UserAlreadyExists : Exception()
}