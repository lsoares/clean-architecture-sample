package cmd

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.jetbrains.exposed.sql.Database

fun main() {
    Javalin.create()
            .routes {
                val database = Database.connect(System.getProperty("DB_URL"), "com.mysql.cj.jdbc.Driver")

                get { it.result("check health") }
                path("users") {
                    get(app.listusers.Handler(app.listusers.UseCase(app.listusers.Repository(database))))
                    post(app.createuser.Handler(
                            app.createuser.UseCase(repository = app.createuser.Repository(), passwordEncoder = app.createuser.PasswordEncoder()))
                    )
                }
            }
            .start(System.getProperty("PORT")?.toInt() ?: 8080)
}
