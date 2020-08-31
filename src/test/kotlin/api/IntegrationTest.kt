package api

import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.skyscreamer.jsonassert.JSONAssert
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.net.http.HttpResponse.BodyHandlers.ofString

object IntegrationTest {

    private val httpClient = newHttpClient()

    fun `create two users when posting two different requests`() {
        httpClient.send(
            newBuilder()
                .POST(ofString(""" { "email": "luis.s@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )
        httpClient.send(
            newBuilder()
                .POST(ofString(""" { "email": "miguel.s@gmail.com", "name": "Miguel Soares", "password": "f47!3#$5g%"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )

        val userList = httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())

        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.s@gmail.com" },
                            { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            userList.body(),
            false
        )
    }

    fun `do not create a repeated user when posting twice`() {
        val creationRequest = newBuilder()
            .POST(ofString(""" { "email": "luis.1@gmail.com", "name": "Luís Soares", "password": "password"} """))
            .uri(URI("http://localhost:8081/users")).build()
        val creation1Response = httpClient.send(creationRequest, discarding())
        assertEquals(HttpStatus.CREATED_201, creation1Response.statusCode())

        val creation2Response = httpClient.send(creationRequest, ofString())
        assertEquals(HttpStatus.CONFLICT_409, creation2Response.statusCode())

        val listResponse =
            httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())
        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.1@gmail.com" }] """,
            listResponse.body(), false
        )
    }

    fun `delete a user after creation`() {
        httpClient.send(
            newBuilder()
                .POST(ofString(""" { "email": "luis.s@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )
        httpClient.send(
            newBuilder()
                .POST(ofString(""" { "email": "miguel.s@gmail.com", "name": "Miguel Soares", "password": "f47!3#$5g%"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )

        val deleteResponse = httpClient.send(
            newBuilder().DELETE().uri(URI("http://localhost:8081/users/luis.s@gmail.com")).build(),
            ofString()
        )

        assertEquals(HttpStatus.NO_CONTENT_204, deleteResponse.statusCode())
        val usersAfter = httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())
        JSONAssert.assertEquals(
            """ [ { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            usersAfter.body(),
            false
        )
    }
}