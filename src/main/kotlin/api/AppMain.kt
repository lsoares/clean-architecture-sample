package api

import Config
import domain.usecases.CreateUser
import domain.usecases.ListUsers
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*

fun main() {
    with(Config) {
        WebApp(
            listUsers = listUsers,
            createUser = createUser,
            port = port
        ).use { it() }
    }
}

class WebApp(
    private val listUsers: ListUsers,
    private val createUser: CreateUser,
    private val port: Int
) : AutoCloseable {

    private var javalinApp: Javalin = Javalin.create().routes {
        get { it.result("check health") }
        path("users") {
            get(ListUsersHandler(listUsers))
            post(CreateUserHandler(createUser))
        }
    }

    operator fun invoke(): WebApp {
        javalinApp.start(port)
        return this
    }

    override fun close() {
        javalinApp.stop()
    }
}