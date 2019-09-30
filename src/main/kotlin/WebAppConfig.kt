
import createuser.PasswordEncoder
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import listusers.Handler
import listusers.UseCase
import org.jetbrains.exposed.sql.Database
import repository.mysql.UserRepository

class WebAppConfig(dbUrl: String, val port: Int) {

    private var javalinApp: Javalin

    init {
        val database = Database.connect(url = dbUrl, driver = "com.mysql.cj.jdbc.Driver")
        val userRepo = UserRepository(database)
        userRepo.createSchema()

        javalinApp = Javalin.create().routes {
            get { it.result("check health") }
            path("users") {
                get(Handler(UseCase(userRepo)))
                post(createuser.Handler(createuser.UseCase(userRepo, PasswordEncoder())))
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