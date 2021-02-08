package api

import domain.model.User
import domain.model.toEmail
import domain.model.toPassword
import domain.ports.UserRepository.SaveResult.NewUser
import domain.ports.UserRepository.SaveResult.UserAlreadyExists
import domain.usecases.CreateUser
import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus

class CreateUserHandler(private val createUser: CreateUser) : Handler {

    override fun handle(ctx: Context) {
        createUser(ctx.body<UserRepresenter>().toUser()).let {
            ctx.status(
                when (it) {
                    NewUser -> HttpStatus.CREATED_201
                    UserAlreadyExists -> HttpStatus.CONFLICT_409
                }
            )
        }
    }

    private class UserRepresenter(val email: String, val name: String, val password: String) {
        fun toUser() = User(email = email.toEmail(), name = name, password = password.toPassword())
    }
}