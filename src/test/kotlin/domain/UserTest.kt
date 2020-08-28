package domain

import domain.model.Email
import domain.model.User
import domain.ports.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class UserTest {

    @Test
    fun `creates a user`() {
        assertDoesNotThrow {
            User(email = Email("l@a.b"), password = "12345", name = "Luís")
        }
    }

    @Test
    fun `throws validation exception`() {
        assertThrows<UserRepository.InvalidUser> {
            User(email = Email("l@a.b"), password = "1", name = "Luís")
        }
        assertThrows<UserRepository.InvalidUser> {
            User(email = Email("l@a.b"), password = "12345", name = "L")
        }
    }
}