package domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class EmailTest {

    @Test
    fun `creates a valid email address`() {
        assertEquals("lsoares@test.com", "lsoares@test.com".toEmail().value)
        assertEquals("lsoares@test.com".toEmail(), "lsoares@test.com".toEmail())
    }

    @Test
    fun `throws validation exception`() {
        assertThrows<Email.InvalidEmail>("lsoares"::toEmail)
    }
}