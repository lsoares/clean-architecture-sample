package domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EmailAddressTest {

    @Test
    fun `creates a valid email address`() {
        assertEquals("lsoares@test.com", EmailAddress("lsoares@test.com").value)
    }

    @Test
    fun `throws validation exception`() {
        assertThrows<EmailAddress.InvalidEmail> {
            EmailAddress("lsoares")
        }
    }
}