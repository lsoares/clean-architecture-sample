package app.listusers

import app.User
import io.javalin.Javalin
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.ofString

@DisplayName("List users handler")
object HandlerTest {

    @Test
    fun `GIVEN a list of users, WHEN requesting it, THEN it converts it to a json representation`() {
        val useCase = mockk<UseCase> {
            every { list() } returns listOf(User("1", "email", "Luís", "password"))
        }
        Javalin.create().get("/", Handler(useCase)).start(1234)

        val response = newHttpClient().send(
                newBuilder().GET().uri(URI("http://localhost:1234")).build(), ofString()
        )

        verify(exactly = 1) { useCase.list() }
        assertEquals(200, response.statusCode())
        JSONAssert.assertEquals(""" [ { "id": "1", "name": "Luís", "email": "email" } ] """, response.body(), true)
    }
}