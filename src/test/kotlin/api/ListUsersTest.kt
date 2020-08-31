package api

import domain.model.User
import domain.model.toEmail
import domain.model.toPassword
import domain.model.toUserId
import domain.usecases.ListUsers
import io.javalin.Javalin
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.skyscreamer.jsonassert.JSONAssert
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

@DisplayName("List users handler")
class ListUsersTest {

    private lateinit var server: Javalin
    private lateinit var listUsers: ListUsers

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        listUsers = mockk()
        server = Javalin.create()
            .get("/", ListUsersHandler(listUsers))
            .start(1234)
    }

    @AfterEach
    fun `after each`() = clearAllMocks()

    @AfterAll
    @Suppress("unused")
    fun `tear down`() {
        server.stop()
    }

    @Test
    fun `it converts a list of users to json`() {
        every { listUsers() } returns listOf(
            User(
                id = "xyz".toUserId(),
                email = "email@test.com".toEmail(),
                name = "Luís",
                password = "hashed".toPassword()
            )
        )

        val response = newHttpClient().send(
            newBuilder().GET().uri(URI("http://localhost:1234")).build(), ofString()
        )

        assertEquals(HttpStatus.OK_200, response.statusCode())
        JSONAssert.assertEquals(
            """ [ { "id": "xyz", "name": "Luís", "email": "email@test.com" } ] """,
            response.body(),
            true
        )
    }
}