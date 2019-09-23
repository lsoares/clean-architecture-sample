package app.createuser

import app.User

class UseCase(private val repository: Repository) {

    fun createUser(user: User) {
        repository.createUser(user)
    }

    class UserAlreadyExists : Exception()
}
