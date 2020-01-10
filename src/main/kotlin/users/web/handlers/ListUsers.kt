package users.web.handlers

import io.javalin.http.Context
import io.javalin.http.Handler
import users.domain.User
import users.usecases.ListUsers

class ListUsers(private val listUsers: ListUsers) : Handler {

    override fun handle(ctx: Context) {
        ctx.json(listUsers.execute().toRepresenter())
    }

    private fun List<User>.toRepresenter() =
        map { UserRepresenter(it.id, it.email.value, it.name) }

    private class UserRepresenter(val id: String?, val email: String, val name: String)
}
