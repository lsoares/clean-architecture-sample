package api

import domain.model.Email
import domain.model.User
import domain.ports.UserRepository
import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus
import domain.usecases.CreateUser

class CreateUserHandler(private val createUser: CreateUser) : Handler {

    override fun handle(ctx: Context) {
        try {
            createUser(ctx.body<UserRepresenter>().toUser())
            ctx.status(HttpStatus.CREATED_201)
        } catch (ex: UserRepository.UserAlreadyExists) {
            ctx.status(HttpStatus.CONFLICT_409)
        }
    }

    private class UserRepresenter(val email: String, val name: String, val password: String) {
        fun toUser() = User(email = Email(email), name = name, password = password)
    }
}