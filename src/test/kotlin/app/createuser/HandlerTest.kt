package app.createuser

import app.User
import io.javalin.Javalin
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

@DisplayName("Create user handler")
object HandlerTest {

    private val useCase = mockk<UseCase>()
    private val httpClient = newHttpClient()
    private val user = User(email = "lsoares@gmail.com", name = "Luís Soares", password = "password")

    @BeforeAll
    @JvmStatic
    fun setup() {
        Javalin.create().post("/", Handler(useCase)).start(1234)
    }

    @Test
    fun `GIVEN a user json, WHEN posting it, THEN it creates it replies 201`() {
        every { useCase.createUser(user) } just Runs
        val request = newBuilder()
                .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:1234")).build()

        val response = httpClient.send(request, ofString())

        verify(exactly = 1) { useCase.createUser(user) }
        assertEquals(HttpStatus.CREATED_201, response.statusCode())
    }

    @Test
    fun `GIVEN an existing user json, WHEN posting it, THEN it handles the use case exception with 409`() {
        every { useCase.createUser(user) } throws UseCase.UserAlreadyExists()

        val request = newBuilder()
                .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:1234")).build()
        val response = httpClient.send(request, ofString())

        verify(exactly = 1) { useCase.createUser(user) }
        assertEquals(HttpStatus.CONFLICT_409, response.statusCode())
    }

    @AfterEach
    fun reset() = clearAllMocks()
}