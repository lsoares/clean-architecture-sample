package web

import domain.UserEntity
import features.ListUsers
import io.javalin.http.Context

class ListUsers(private val listUsers: ListUsers) : io.javalin.http.Handler {

    override fun handle(ctx: Context) {
        ctx.json(listUsers.execute().toRepresenter())
    }

    private fun List<UserEntity>.toRepresenter() =
        map { UserRepresenter(it.id, it.email.value, it.name) }

    private class UserRepresenter(val id: String?, val email: String, val name: String)
}
