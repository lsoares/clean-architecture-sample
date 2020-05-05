package domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class PasswordEncoderTest {

    @Test
    fun `encodes a string`() {
        assertNotEquals("abc", PasswordEncoder.encode("abc"))
    }

    @Test
    fun `encodes deterministically`() {
        assertEquals(PasswordEncoder.encode("abc"), PasswordEncoder.encode("abc"))
    }
}