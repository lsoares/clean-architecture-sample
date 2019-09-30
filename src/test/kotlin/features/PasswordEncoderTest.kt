package features

import domain.PasswordEncoder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

object PasswordEncoderTest {

    @Test
    fun `GIVEN a string, WHEN encoding it, THEN it encodes to another string`() {
        assertNotEquals("abc", PasswordEncoder.encode("abc"))
    }

    @Test
    fun `GIVEN a string, WHEN encoding it twice, THEN it encodes it the same way`() {
        assertEquals(PasswordEncoder.encode("abc"), PasswordEncoder.encode("abc"))
    }
}