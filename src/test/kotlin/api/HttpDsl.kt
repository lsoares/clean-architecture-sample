package api

import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.net.http.HttpResponse.BodyHandlers.ofString

object HttpDsl {

    private val httpClient = newHttpClient()

    fun `create user`(email: String, name: String, password: String): HttpResponse<Void> =
        httpClient.send(
            newBuilder()
                .POST(ofString(""" { "email": "$email", "name": "$name", "password": "$password"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )

    fun `list users`(): HttpResponse<String> =
        httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())

    fun `delete user`(email: String): HttpResponse<String> =
        httpClient.send(
            newBuilder().DELETE().uri(URI("http://localhost:8081/users/$email")).build(),
            ofString()
        )
}
