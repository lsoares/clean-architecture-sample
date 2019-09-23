package app.createuser

import app.User
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class Handler(private val useCase: UseCase) : io.javalin.http.Handler {

    override fun handle(ctx: Context) {
        try {
            useCase.createUser(ctx.body<UserRepresenter>().toUser())
            ctx.status(HttpStatus.CREATED_201)
        } catch (ex: UseCase.UserAlreadyExists) {
            ctx.status(HttpStatus.CONFLICT_409)
        }
    }

    private class UserRepresenter(val email: String, val name: String, val password: String) {
        fun toUser() = User(email = email, name = name, password = password)
    }
}