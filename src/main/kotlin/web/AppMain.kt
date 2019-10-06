package web

import domain.UserRepository
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.jetbrains.exposed.sql.Database
import persistence.MySqlUserRepository

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
            get(ListUsers(features.ListUsers(userRepository)))
            post(CreateUser(features.CreateUser(userRepository)))
        }
    }

    fun start() {
        javalinApp.start(port)
    }

    fun stop() {
        javalinApp.stop()
    }
}