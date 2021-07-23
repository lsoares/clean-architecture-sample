package adapters

import domain.model.Profile
import domain.model.toEmail
import io.javalin.Javalin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class ProfileGatewayTest {

    @Test
    fun `gets a user profile by id`() = testProfileGateway { server, gatewayClient ->
        server.get("profile/abc") {
            it.json(mapOf("id" to "abc", "email" to "x123@gmail.com"))
        }

        val result = gatewayClient.fetchProfile("abc")

        assertEquals(Profile(id = "abc", email = "x123@gmail.com".toEmail()), result)
    }

    @Test
    fun `posts a user profile`() = testProfileGateway { server, profileGateway ->
        var postedBody: String? = null
        var contentType: String? = null
        server.post("profile") {
            postedBody = it.body()
            contentType = it.contentType()
            it.status(201)
        }

        profileGateway.saveProfile(Profile(id = "abc", email = "x123@gmail.com".toEmail()))

        JSONAssert.assertEquals(
            """ { "id": "abc", "email": "x123@gmail.com"}  """,
            postedBody, true
        )
        assertEquals("application/json", contentType)
    }

    private fun testProfileGateway(block: (Javalin, ProfileGateway) -> Unit) {
        val server = Javalin.create().start()
        val gatewayClient = ProfileGateway(apiUrl = "http://localhost:${server.port()}")
        block(server, gatewayClient)
        server.stop()
    }
}