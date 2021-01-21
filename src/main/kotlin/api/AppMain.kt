package api

import Config
import ConfigWithMongoDb
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*

fun main() {
    WebApp(ConfigWithMongoDb, System.getenv("PORT")?.toInt() ?: 8080).start()
}

class WebApp(config: Config, private val port: Int) : AutoCloseable {

    private val javalinApp by lazy {
        with(config) {
            Javalin.create().routes {
                get { it.result("check health") }
                path("users") {
                    get(ListUsersHandler(listUsers))
                    post(CreateUserHandler(createUser))
                    delete(":email", DeleteUserHandler(deleteUser))
                }
            }
        }
    }

    fun start(): WebApp {
        javalinApp.start(port)
        return this
    }

    override fun close() {
        javalinApp.stop()
    }
}