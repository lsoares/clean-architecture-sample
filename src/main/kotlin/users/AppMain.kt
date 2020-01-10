package users

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.jetbrains.exposed.sql.Database
import users.domain.UserRepository
import users.persistence.MySqlUserRepository
import users.usecases.CreateUser
import users.usecases.ListUsers

fun main() {
    val database = Database.connect(url = System.getProperty("DB_URL"), driver = "com.mysql.cj.jdbc.Driver")
    val userRepo = MySqlUserRepository(database).apply { createSchema() }

    WebAppConfig(
        userRepository = userRepo,
        port = System.getProperty("PORT")?.toInt() ?: 8080
    ).start()
}

class WebAppConfig(userRepository: UserRepository, private val port: Int) {

    private var javalinApp: Javalin = Javalin.create().routes {
        get { it.result("check health") }
        path("users") {
            get(users.web.handlers.ListUsers(ListUsers(userRepository)))
            post(users.web.handlers.CreateUser(CreateUser(userRepository)))
        }
    }

    fun start() {
        javalinApp.start(port)
    }

    fun stop() {
        javalinApp.stop()
    }
}