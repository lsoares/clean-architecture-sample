package api

import domain.model.User
import domain.usecases.ListUsers
import io.javalin.http.Context
import io.javalin.http.Handler

class ListUsersHandler(private val listUsers: ListUsers) : Handler {

    override fun handle(ctx: Context) {
        ctx.json(listUsers().toRepresenter())
    }

    private fun List<User>.toRepresenter() =
        map { UserRepresenter(it.id.value, it.email.value, it.name) }

    @Suppress("unused")
    private class UserRepresenter(val id: String?, val email: String, val name: String)
}
