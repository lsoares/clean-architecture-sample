package users.web

import io.javalin.Javalin
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import users.domain.EmailAddress
import users.domain.User
import users.usecases.CreateUser
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

@DisplayName("Create user handler")
object CreateUserTest {

    private val useCase = mockk<CreateUser>()
    private val httpClient = newHttpClient()
    private lateinit var server: Javalin

    @BeforeAll
    @JvmStatic
    fun setup() {
        server = Javalin.create().post("/", CreateUser(useCase)).start(1234)
    }

    @Test
    fun `GIVEN a user json, WHEN posting it, THEN it creates it and replies 201`() {
        val userCapture = slot<User>()
        every { useCase.execute(capture(userCapture)) } just Runs
        val request = newBuilder()
            .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís", "password": "password"} """))
            .uri(URI("http://localhost:1234")).build()

        val response = httpClient.send(request, ofString())

        verify(exactly = 1) {
            useCase.execute(
                User(
                    userCapture.captured.id,
                    email = EmailAddress("lsoares@gmail.com"),
                    name = "Luís",
                    password = "password"
                )
            )
        }
        assertEquals(HttpStatus.CREATED_201, response.statusCode())
    }

    @Test
    fun `GIVEN an existing user json, WHEN posting it, THEN it handles the use case exception with 409`() {
        every { useCase.execute(any()) } throws User.UserAlreadyExists()
        val jsonBody = """{ "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"}"""

        val response = httpClient.send(
            newBuilder().POST(ofString(jsonBody)).uri(URI("http://localhost:1234")).build(), ofString()
        )

        verify(exactly = 1) { useCase.execute(any()) }
        assertEquals(HttpStatus.CONFLICT_409, response.statusCode())
    }

    @AfterEach
    fun afterEach() = clearAllMocks()

    @AfterAll
    @JvmStatic
    fun afterAll() {
        server.stop()
    }
}