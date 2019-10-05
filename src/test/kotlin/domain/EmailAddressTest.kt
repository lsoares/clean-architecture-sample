package domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

object EmailAddressTest {

    @Test
    fun `GIVEN a valid email, WHEN creating it, THEN creates its entity`() {
        assertEquals("lsoares@test.com", EmailAddress("lsoares@test.com").value)
    }

    @Test
    fun `GIVEN a invalid email, WHEN creating it, THEN throws exception`() {
        assertThrows<EmailAddress.InvalidEmail> {
            EmailAddress("lsoares")
        }
    }
}