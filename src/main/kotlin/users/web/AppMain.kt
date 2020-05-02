package users.web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import users.Config
import users.domain.UserRepository
import users.usecases.CreateUser
import users.usecases.ListUsers

fun main() {
    WebApp(
        userRepository = Config.userRepoMySql,
        port = System.getProperty("PORT")?.toInt() ?: 8080
    ).use(WebApp::start)
}

class WebApp(userRepository: UserRepository, private val port: Int) : AutoCloseable {

    private var javalinApp: Javalin = Javalin.create().routes {
        get { it.result("check health") }
        path("users") {
            get(ListUsers(ListUsers(userRepository)))
            post(CreateUser(CreateUser(userRepository)))
        }
    }

    fun start() {
        javalinApp.start(port)
    }

    override fun close() {
        javalinApp.stop()
    }
}