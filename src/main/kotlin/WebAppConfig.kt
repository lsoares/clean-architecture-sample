import createuser.PasswordEncoder
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import listusers.Handler
import listusers.Repository
import listusers.UseCase
import org.jetbrains.exposed.sql.Database

class WebAppConfig(dbUrl: String, val port: Int) {

    private var javalinApp: Javalin

    init {
        val database = Database.connect(url = dbUrl, driver = "com.mysql.cj.jdbc.Driver")

        javalinApp = Javalin.create()
                .routes {
                    get { it.result("check health") }
                    path("users") {
                        get(Handler(UseCase(Repository(database))))
                        post(createuser.Handler(
                                createuser.UseCase(repository = createuser.Repository(database), passwordEncoder = PasswordEncoder()))
                        )
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