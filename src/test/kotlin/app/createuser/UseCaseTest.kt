package app.createuser

import io.mockk.*
import org.junit.jupiter.api.Test

object UseCaseTest {

    @Test
    fun `GIVEN a user to create, WHEN running the use case, THEN it calls the repo`() {
        val user = User(email = "lsoares@gmail.com", name = "Luís Soares", password = "toEncode")
        val repository = mockk<Repository> {
            every { createUser(user.copy(password = "encoded")) } just Runs
        }
        val passwordEncoder = mockk<PasswordEncoder> {
            every { encode("toEncode") } returns "encoded"
        }

        UseCase(repository, passwordEncoder).createUser(user)

        verify(exactly = 1) { passwordEncoder.encode("toEncode") }
        verify(exactly = 1) { repository.createUser(user.copy(password = "encoded")) }
    }

    // TODO: deal with user exists
}