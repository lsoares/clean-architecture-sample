package domain.ports

import domain.model.User

interface UserRepository {
    fun findAll(): List<User>
    fun save(user: User)

    class UserAlreadyExists : Exception()
}