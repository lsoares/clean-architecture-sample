package api

import domain.EmailAddress
import domain.User
import io.javalin.Javalin
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import usecases.CreateUser
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

@DisplayName("Create user handler")
class CreateUserTest {

    private val createUser = mockk<CreateUser>()
    private val httpClient = newHttpClient()
    private lateinit var server: Javalin

    @BeforeAll
    fun setup() {
        server = Javalin.create()
            .post("/", CreateUserHandler(createUser))
            .start(1234)
    }

    @Test
    fun `it creates a user when posting its json`() {
        val userCapture = slot<User>()
        every { createUser(capture(userCapture)) } just Runs
        val request = newBuilder()
            .POST(ofString(""" { "email": "luis.s@gmail.com", "name": "Luís", "password": "password"} """))
            .uri(URI("http://localhost:1234")).build()

        val response = httpClient.send(request, ofString())

        verify(exactly = 1) {
            createUser(
                User(
                    userCapture.captured.id,
                    email = EmailAddress("luis.s@gmail.com"),
                    name = "Luís",
                    password = "password"
                )
            )
        }
        assertEquals(HttpStatus.CREATED_201, response.statusCode())
    }

    @Test
    fun `it replies with 409 when posting an existing user`() {
        every { createUser(any()) } throws User.UserAlreadyExists()
        val jsonBody = """{ "email": "luis.s@gmail.com", "name": "Luís Soares", "password": "password"}"""

        val response = httpClient.send(
            newBuilder().POST(ofString(jsonBody)).uri(URI("http://localhost:1234")).build(), ofString()
        )

        verify(exactly = 1) { createUser(any()) }
        assertEquals(HttpStatus.CONFLICT_409, response.statusCode())
    }

    @AfterEach
    fun `after each`() = clearAllMocks()

    @AfterAll
    fun `after all`() {
        server.stop()
    }
}