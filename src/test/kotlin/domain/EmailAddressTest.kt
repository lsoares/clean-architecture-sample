package domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

object EmailAddressTest {

    @Test
    fun `GIVEN a valid email, WHEN creating it, THEN creates entity`() {
        assertEquals("lsoares@test.com", EmailAddress("lsoares@test.com").value)
    }

    @Test
    fun `GIVEN a invalid email, WHEN creating it, THEN an exception is thrown`() {
        assertThrows<EmailAddress.InvalidEmail> {
            EmailAddress("lsoares")
        }
    }
}