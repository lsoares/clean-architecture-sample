package listusers

import domain.entities.UserInList
import io.javalin.http.Context

class Handler(private val useCase: UseCase) : io.javalin.http.Handler {

    override fun handle(ctx: Context) {
        ctx.json(useCase.list().toRepresenter())
    }

    private fun List<UserInList>.toRepresenter() =
            map { UserRepresenter(it.id, it.email, it.name) }

    private class UserRepresenter(val id: Int, val email: String, val name: String)
}

