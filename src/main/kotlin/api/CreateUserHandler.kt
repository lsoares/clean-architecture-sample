package api

import domain.ports.UserRepository.SaveResult.NewUser
import domain.ports.UserRepository.SaveResult.UserAlreadyExists
import domain.usecases.CreateUser
import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus

class CreateUserHandler(
    private val createUser: CreateUser,
) : Handler {

    override fun handle(ctx: Context) {
        when (createUser(ctx.bodyAsClass(CreateUser.Request::class.java))) {
            NewUser -> HttpStatus.CREATED_201
            UserAlreadyExists -> HttpStatus.CONFLICT_409
        }.let(ctx::status)
    }
}
