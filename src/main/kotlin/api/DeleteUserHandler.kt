package api

import domain.model.toEmail
import domain.usecases.DeleteUser
import io.javalin.http.Context
import io.javalin.http.Handler
import org.eclipse.jetty.http.HttpStatus

class DeleteUserHandler(private val deleteUser: DeleteUser) : Handler {
    override fun handle(ctx: Context) {
        deleteUser(ctx.pathParam("email").toEmail())
        ctx.status(HttpStatus.NO_CONTENT_204)
    }
}