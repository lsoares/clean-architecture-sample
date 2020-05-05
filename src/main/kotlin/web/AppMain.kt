package web

import Config
import domain.UserRepository
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import usecases.CreateUser
import usecases.ListUsers

fun main() {
    WebApp(
        userRepository = Config.userRepoMySql,
        port = System.getenv("PORT")?.toInt() ?: 8080
    ).use(WebApp::start)
}

class WebApp(userRepository: UserRepository, private val port: Int) : AutoCloseable {

    private var javalinApp: Javalin = Javalin.create().routes {
        get { it.result("check health") }
        path("users") {
            get(ListUsersHandler(ListUsers(userRepository)))
            post(CreateUserHandler(CreateUser(userRepository)))
        }
    }

    fun start() {
        javalinApp.start(port)
    }

    override fun close() {
        javalinApp.stop()
    }
}