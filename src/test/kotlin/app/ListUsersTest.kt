package app

import domain.EmailAddress
import domain.User
import domain.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("List users use case")
object ListUsersTest {

    @Test
    fun `GIVEN a list of users, WHEN requesting it, THEN it returns it`() {
        val repository = mockk<UserRepository> {
            every { findAll() } returns listOf(
                User(
                    id = "abc1",
                    email = EmailAddress("email@test.com"),
                    name = "Luís Soares",
                    hashedPassword = "hashed"
                )
            )
        }

        val users = ListUsers(repository).execute()

        verify(exactly = 1) { repository.findAll() }
        assertEquals(
            listOf(
                User(
                    id = "abc1",
                    email = EmailAddress("email@test.com"),
                    name = "Luís Soares",
                    hashedPassword = "hashed"
                )
            ),
            users
        )
    }
}