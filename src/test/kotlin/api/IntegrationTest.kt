package api

import domain.model.IdGenerator
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.AfterEach
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

    fun `it creates two users when posting two different requests`() {
        mockkObject(IdGenerator)
        every { IdGenerator.generate() } returns "1" andThen "2"
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
            """ [ { "id": "1", "name": "Luís Soares", "email": "luis.s@gmail.com" },
                            { "id": "2", "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            userList.body(),
            true
        )
    }

    fun `it does not create a repeated user when posting twice`() {
        val creationRequest = newBuilder()
            .POST(ofString(""" { "email": "luis.s@gmail.com", "name": "Luís Soares", "password": "password"} """))
            .uri(URI("http://localhost:8081/users")).build()
        val creation1Response = httpClient.send(creationRequest, discarding())
        assertEquals(HttpStatus.CREATED_201, creation1Response.statusCode())

        val creation2Response = httpClient.send(creationRequest, ofString())
        assertEquals(HttpStatus.CONFLICT_409, creation2Response.statusCode())

        val listResponse =
            httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())
        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.s@gmail.com" }] """,
            listResponse.body(), false
        )
    }

    fun `it deletes a user after creation`() {
        mockkObject(IdGenerator)
        every { IdGenerator.generate() } returns "1" andThen "2"
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
            """ [ { "id": "2", "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            usersAfter.body(),
            true
        )
    }

    @AfterEach
    fun `after each`() {
        unmockkObject(IdGenerator)
    }
}