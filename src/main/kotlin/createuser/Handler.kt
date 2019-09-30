package createuser

import domain.UserAlreadyExists
import domain.UserEntity
import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus

class Handler(private val useCase: UseCase) : io.javalin.http.Handler {

    override fun handle(ctx: Context) {
        try {
            useCase.createUser(ctx.body<UserRepresenter>().toUser())
            ctx.status(HttpStatus.CREATED_201)
        } catch (ex: UserAlreadyExists) {
            ctx.status(HttpStatus.CONFLICT_409)
        }
    }

    private class UserRepresenter(val email: String, val name: String, val password: String) {
        fun toUser() = UserEntity(id = null, email = email, name = name, password = password)
    }
}