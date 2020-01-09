package domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

object UserTest {

    @Test
    fun `GIVEN a valid user, WHEN creating it, THEN creates its entity`() {
        assertDoesNotThrow {
            User(email = EmailAddress("l@a.b"), password = "12345", name = "Luís")
        }
    }

    @Test
    fun `GIVEN a invalid user, WHEN creating it, THEN throws exception`() {
        assertThrows<User.InvalidUser> {
            User(email = EmailAddress("l@a.b"), password = "1", name = "Luís")
        }
        assertThrows<User.InvalidUser> {
            User(email = EmailAddress("l@a.b"), password = "12345", name = "L")
        }
    }
}