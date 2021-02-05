import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path

lateinit var webApi: Javalin

fun main() {
    webApi = Javalin.create().routes {
        path("profile") {
            get(":id") {
                val profileId = it.pathParam("id")
                it.json(
                    mapOf(
                        "id" to profileId,
                        "email" to "$profileId@email.com",
                    )
                )
            }
        }
    }.start(4444)
}

fun stopApp(): Javalin = webApi.stop()