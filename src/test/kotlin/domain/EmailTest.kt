package domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EmailTest {

    @Test
    fun `creates a valid email address`() {
        assertEquals("lsoares@test.com", Email("lsoares@test.com").value)
    }

    @Test
    fun `throws validation exception`() {
        assertThrows<Email.InvalidEmail> {
            Email("lsoares")
        }
    }
}