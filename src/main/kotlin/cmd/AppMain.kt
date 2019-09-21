package cmd

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import org.jetbrains.exposed.sql.Database

fun main() {
    val database = Database.Companion.connect(System.getProperty("DB_URL"), "com.mysql.cj.jdbc.Driver")

    Javalin.create()
            .routes {
                get { it.result("check health") }
                path("users") {
                    get(app.listusers.Handler(app.listusers.UseCase(app.listusers.Repository(database))))
                }
            }
            .start(8080)
}
