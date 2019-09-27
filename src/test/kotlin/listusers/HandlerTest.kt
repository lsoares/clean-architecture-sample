package listusers

import domain.entities.UserInList
import io.javalin.Javalin
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.skyscreamer.jsonassert.JSONAssert
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

@DisplayName("List users handler")
object HandlerTest {

    private lateinit var server: Javalin
    private lateinit var useCase: UseCase

    @BeforeAll
    @JvmStatic
    fun setup() {
        useCase = mockk()
        server = Javalin.create().get("/", Handler(useCase)).start(1234)
    }

    @Test
    fun `GIVEN a list of users, WHEN requesting it, THEN it converts it to a json representation`() {
        every { useCase.list() } returns listOf(UserInList(id = 1, email = "email", name = "Luís"))

        val response = newHttpClient().send(
                newBuilder().GET().uri(URI("http://localhost:1234")).build(), ofString()
        )

        verify(exactly = 1) { useCase.list() }
        assertEquals(HttpStatus.OK_200, response.statusCode())
        JSONAssert.assertEquals(""" [ { "id": 1, "name": "Luís", "email": "email" } ] """, response.body(), true)
    }

    @AfterEach
    fun afterEach() = clearAllMocks()

    @AfterAll
    @JvmStatic
    fun tearDown() {
        server.stop()
    }
}