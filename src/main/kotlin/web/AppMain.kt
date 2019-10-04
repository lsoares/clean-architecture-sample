package web

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
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
        val userRepo = UserRepository(database).apply { createSchema() }

        javalinApp = Javalin.create().routes {
            get { it.result("check health") }
            path("users") {
                get(ListUsers(features.ListUsers(userRepo)))
                post(CreateUser(features.CreateUser(userRepo)))
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