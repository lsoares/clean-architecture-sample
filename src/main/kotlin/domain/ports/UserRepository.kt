package domain.ports

import domain.model.Email
import domain.model.User

interface UserRepository {
    fun findAll(): List<User>
    fun save(user: User): SaveResult
    fun delete(email: Email)

    sealed class SaveResult {
        object NewUser : SaveResult()
        object UserAlreadyExists : SaveResult()
    }
}