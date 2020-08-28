package domain.model

import domain.model.Password.InvalidPassword
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PasswordTest {

    @Test
    fun `do not allow invalid passwords`() {
        assertThrows<InvalidPassword> { "1234".toPassword() }
    }

    @Test
    fun `encodes a string`() {
        assertNotEquals("abcde", "abcde".toPassword().hashed)
    }

    @Test
    fun `encodes deterministically`() {
        assertEquals("abcde".toPassword().hashed, "abcde".toPassword().hashed)
    }
}