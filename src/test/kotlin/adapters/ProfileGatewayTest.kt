package adapters

import com.fasterxml.jackson.databind.ObjectMapper
import domain.model.Profile
import domain.model.toEmail
import io.javalin.Javalin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class ProfileGatewayTest {

    @Test
    fun `gets a user profile by id`() {
        Javalin.create().get("profile/abc") {
            it.json(mapOf("id" to "abc", "email" to "x123@gmail.com"))
        }.start().use {
            val gatewayClient = ProfileGateway(apiUrl = "http://localhost:${it.port()}")

            val result = gatewayClient.fetchProfile("abc")

            assertEquals(Profile(id = "abc", email = "x123@gmail.com".toEmail()), result)
        }
    }

    @Test
    fun `posts a user profile`() {
        var postedBody: String? = null
        var contentType: String? = null
        Javalin.create().start().post("profile") {
            postedBody = it.body()
            contentType = it.contentType()
            it.status(201)
        }.use {
            val gatewayClient = ProfileGateway(apiUrl = "http://localhost:${it.port()}")

            gatewayClient.saveProfile(Profile(id = "abc", email = "x123@gmail.com".toEmail()))

            assertEquals(
                ObjectMapper().valueToTree(mapOf("id" to "abc", "email" to "x123@gmail.com")),
                ObjectMapper().readTree(postedBody)
            )
            assertEquals("application/json", contentType)
        }
    }

}
