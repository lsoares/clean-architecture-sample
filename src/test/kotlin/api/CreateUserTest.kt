package api

import domain.model.User
import domain.model.toEmail
import domain.model.toPassword
import domain.model.toUserId
import domain.ports.UserRepository.SaveResult.NewUser
import domain.ports.UserRepository.SaveResult.UserAlreadyExists
import domain.usecases.CreateUser
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
class CreateUserTest {

    private lateinit var server: Javalin

    @AfterEach
    fun `after all`() {
        server.stop()
    }

    @Test
    fun `create a user when posting its json`() {
        val createUser = mockk<CreateUser> {
            every { this@mockk.invoke(any()) } returns NewUser
        }
        server = startFakeServer(createUser)
        val request = newBuilder()
            .POST(ofString(""" { "email": "luis.s@gmail.com", "name": "Luís", "password": "password"} """))
            .uri(URI("http://localhost:1234"))

        val response = newHttpClient().send(request.build(), ofString())

        verify(exactly = 1) {
            createUser(
                User(
                    id = "id123".toUserId(),
                    email = "luis.s@gmail.com".toEmail(),
                    name = "Luís",
                    password = "password".toPassword()
                )
            )
        }
        assertEquals(HttpStatus.CREATED_201, response.statusCode())
    }

    @Test
    fun `reply with 409 when posting an existing user`() {
        server = startFakeServer(createUser = mockk {
            every { this@mockk.invoke(any()) } returns UserAlreadyExists
        })
        val request = newBuilder()
            .uri(URI("http://localhost:1234"))
            .POST(ofString("""{ "email": "luis.s@gmail.com", "name": "Luís Soares", "password": "password"}"""))

        val response = newHttpClient().send(request.build(), ofString())

        assertEquals(HttpStatus.CONFLICT_409, response.statusCode())
    }

    private fun startFakeServer(createUser: CreateUser) = Javalin.create()
        .post("/", CreateUserHandler(createUser) { "id123".toUserId() })
        .start(1234)
}