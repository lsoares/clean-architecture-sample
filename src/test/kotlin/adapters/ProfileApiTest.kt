package adapters

import domain.Profile
import domain.toEmail
import io.javalin.Javalin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProfileApiTest {

    @Test
    fun `gets the user profile by id`() {
        val fakeProfile = Javalin.create().get("profile/abc") {
            it.json(mapOf("id" to "abc", "email" to "x123@gmail.com"))
        }.start(1234)
        val profileApi = ProfileApi(apiUrl = "http://localhost:1234")

        val result = profileApi.fetchProfile("abc")

        assertEquals(Profile(id = "abc", email = "x123@gmail.com".toEmail()), result)

        fakeProfile.stop()
    }
}