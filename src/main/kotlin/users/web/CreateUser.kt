package users.web

import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus
import users.domain.EmailAddress
import users.domain.User
import users.usecases.CreateUser

class CreateUser(private val createUser: CreateUser) : Handler {

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