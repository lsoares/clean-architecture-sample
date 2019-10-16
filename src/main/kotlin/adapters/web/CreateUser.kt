package adapters.web

import app.CreateUser
import domain.EmailAddress
import domain.User
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class CreateUser(private val createUser: CreateUser) : io.javalin.http.Handler {

    override fun handle(ctx: Context) {
        try {
            createUser.execute(ctx.body<UserRepresenter>().toUser())
            ctx.status(HttpStatus.CREATED_201)
        } catch (ex: User.UserAlreadyExists) {
            ctx.status(HttpStatus.CONFLICT_409)
        }
    }

    private class UserRepresenter(val email: String, val name: String, val password: String) {
        fun toUser() = User(email = EmailAddress(email), name = name, password = password)
    }
}