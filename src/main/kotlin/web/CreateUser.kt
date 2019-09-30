package web

import domain.UserAlreadyExists
import domain.UserEntity
import features.CreateUser
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class CreateUser(private val createUser: CreateUser) : io.javalin.http.Handler {

    override fun handle(ctx: Context) {
        try {
            createUser.execute(ctx.body<UserRepresenter>().toUser())
            ctx.status(HttpStatus.CREATED_201)
        } catch (ex: UserAlreadyExists) {
            ctx.status(HttpStatus.CONFLICT_409)
        }
    }

    private class UserRepresenter(val email: String, val name: String, val password: String) {
        fun toUser() = UserEntity(id = null, email = email, name = name, password = password)
    }
}