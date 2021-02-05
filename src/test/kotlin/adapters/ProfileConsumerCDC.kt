package adapters

import au.com.dius.pact.consumer.MockServer
import au.com.dius.pact.consumer.dsl.PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact
import domain.model.Profile
import domain.model.toEmail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(PactConsumerTestExt::class)
@PactTestFor(port = "8080")
class ProfileConsumerCDC {

    @Pact(provider = "profile_service", consumer = "users")
    @Suppress("unused")
    private fun `create pact`(builder: PactDslWithProvider): RequestResponsePact =
        builder.uponReceiving("can fetch a user profile")
            .path("/profile/abc")
            .willRespondWith()
            .status(200)
            .body(""" {"id": "abc", "email": "abc@email.com"} """)
            .toPact()

    @Test
    fun `get user`(mockServer: MockServer) {
        val profileGateway = ProfileGateway(mockServer.getUrl())

        val result = profileGateway.fetchProfile("abc")

        assertEquals(Profile(id = "abc", email = "abc@email.com".toEmail()), result)
    }
}