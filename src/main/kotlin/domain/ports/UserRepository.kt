package domain.ports

import domain.model.Email
import domain.model.User

interface UserRepository {
    fun findAll(): List<User>
    fun save(user: User)
    fun delete(email: Email)

    class UserAlreadyExists : Exception()
}