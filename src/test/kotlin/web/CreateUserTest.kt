package web

import domain.UserEntity
import features.CreateUser
import io.javalin.Javalin
import io.mockk.*
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

@DisplayName("Create user handler")
object CreateUserTest {

    private val useCase = mockk<CreateUser>()
    private val httpClient = newHttpClient()
    private val user = UserEntity(email = "lsoares@gmail.com", name = "Luís Soares", password = "password")
    private lateinit var server: Javalin

    @BeforeAll
    @JvmStatic
    fun setup() {
        server = Javalin.create().post("/", CreateUser(useCase)).start(1234)
    }

    @Test
    fun `GIVEN a user json, WHEN posting it, THEN it creates it replies 201`() {
        every { useCase.execute(user) } just Runs
        val request = newBuilder()
                .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:1234")).build()

        val response = httpClient.send(request, ofString())

        verify(exactly = 1) { useCase.execute(user) }
        assertEquals(HttpStatus.CREATED_201, response.statusCode())
    }

    @Test
    fun `GIVEN an existing user json, WHEN posting it, THEN it handles the use case exception with 409`() {
        every { useCase.execute(user) } throws UserEntity.UserAlreadyExists()
        val request = newBuilder()
                .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:1234")).build()

        val response = httpClient.send(request, ofString())

        verify(exactly = 1) { useCase.execute(user) }
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