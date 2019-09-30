package web

import features.PasswordEncoder
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import org.jetbrains.exposed.sql.Database
import repository.mysql.UserRepository

fun main() {
    WebAppConfig(
        dbUrl = System.getProperty("DB_URL"),
        port = System.getProperty("PORT")?.toInt() ?: 8080
    ).start()
}

class WebAppConfig(dbUrl: String, private val port: Int) {

    private var javalinApp: Javalin

    init {
        val database = Database.connect(url = dbUrl, driver = "com.mysql.cj.jdbc.Driver")
        val userRepo = UserRepository(database)
        userRepo.createSchema()

        javalinApp = Javalin.create().routes {
            ApiBuilder.get { it.result("check health") }
            ApiBuilder.path("users") {
                ApiBuilder.get(ListUsers(features.ListUsers(userRepo)))
                ApiBuilder.post(CreateUser(features.CreateUser(userRepo, PasswordEncoder())))
            }
        }
    }

    fun start() {
        javalinApp.start(port)
    }

    fun stop() {
        javalinApp.stop()
    }
}