package domain.model

import domain.model.User.InvalidUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class UserTest {

    @Test
    fun `creates a user`() {
        assertDoesNotThrow {
            User(email = "l@a.b".toEmail(), password = "12345".toPassword(), name = "Luís", id = "id123".toUserId())
        }
    }

    @Test
    fun `throws validation exception`() {
        assertThrows<InvalidUser> {
            User(email = "l@a.b".toEmail(), password = "12345".toPassword(), name = "L", id = "id123".toUserId())
        }
    }
}