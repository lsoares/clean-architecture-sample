package usecases

import domain.Email
import domain.User
import domain.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("List users use case")
class ListUsersTest {

    @Test
    fun `it returns a list of users from the repo`() {
        val repository = mockk<UserRepository> {
            every { findAll() } returns listOf(
                User(
                    id = "abc1",
                    email = Email("email@test.com"),
                    name = "Luís Soares",
                    hashedPassword = "hashed"
                )
            )
        }
        val listUsers = ListUsers(repository)

        val users = listUsers()

        verify(exactly = 1) { repository.findAll() }
        assertEquals(
            listOf(
                User(
                    id = "abc1",
                    email = Email("email@test.com"),
                    name = "Luís Soares",
                    hashedPassword = "hashed"
                )
            ),
            users
        )
    }
}