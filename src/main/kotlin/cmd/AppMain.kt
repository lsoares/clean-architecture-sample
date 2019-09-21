package cmd

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*

fun main() {
    Javalin.create { it.showJavalinBanner = false }
            .routes {
                get("/") { it.result("health check") }
            }
            .start(7000)
}
