package app.createuser

import app.User

class UseCase {

    fun createUser(user: User) {

    }

    class UserAlreadyExists : Exception()
}
