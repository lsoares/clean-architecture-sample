package adapters

import domain.model.Profile
import domain.model.toEmail
import io.javalin.Javalin
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class ProfileGatewayTest {

    private lateinit var fakeProfile: Javalin

    @AfterEach
    fun `after each`() {
        fakeProfile.stop()
    }

    @Test
    fun `gets a user profile by id`() {
        fakeProfile = Javalin.create().get("profile/abc") {
            // it.json(mapOf("id" to "abc", "email" to "x123@gmail.com"))
            it.result(""" {"id": "abc", "email": "x123@gmail.com"} """)
                .contentType("application/json")
        }.start(1234)
        val profileGateway = ProfileGateway(apiUrl = "http://localhost:1234")

        val result = profileGateway.fetchProfile("abc")

        assertEquals(Profile(id = "abc", email = "x123@gmail.com".toEmail()), result)
    }

    @Test
    fun `posts a user profile`() {
        var postedBody: String? = null
        var contentType: String? = null
        fakeProfile = Javalin.create().post("profile") {
            postedBody = it.body()
            contentType = it.contentType()
            it.status(201)
        }.start(1234)
        val profileGateway = ProfileGateway(apiUrl = "http://localhost:1234")

        profileGateway.saveProfile(Profile(id = "abc", email = "x123@gmail.com".toEmail()))

        JSONAssert.assertEquals(
            """ { "id": "abc", "email": "x123@gmail.com"}  """,
            postedBody, true
        )
        assertEquals("application/json", contentType)
    }
}