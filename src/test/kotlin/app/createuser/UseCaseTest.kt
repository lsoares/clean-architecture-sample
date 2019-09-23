package app.createuser

import app.User
import io.mockk.*
import org.junit.jupiter.api.Test

object UseCaseTest {

    @Test
    fun `GIVEN a user to create, WHEN running the use case, THEN it calls the repo`() {
        val user = User("abc123", "lsoares@gmail.com", "Luís Soares", "hashedpassword")
        val repository = mockk<Repository> {
            every { createUser(user) } just Runs
        }

        UseCase(repository).createUser(user)

        verify(exactly = 1) { repository.createUser(user) }
    }
}