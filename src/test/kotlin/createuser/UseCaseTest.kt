package createuser

import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import repository.UserRepositoryCrud

@DisplayName("Create user use case")
object UseCaseTest {

    @Test
    fun `GIVEN a user to create, WHEN running the use case, THEN it calls the repo`() {
        val user = User(email = "lsoares@gmail.com", name = "Luís Soares", password = "toEncode")
        val repository = mockk<UserRepositoryCrud> {
            every { create(user.copy(password = "encoded")) } just Runs
        }
        val passwordEncoder = mockk<PasswordEncoder> {
            every { encode("toEncode") } returns "encoded"
        }

        UseCase(repository, passwordEncoder).createUser(user)

        verify(exactly = 1) { passwordEncoder.encode("toEncode") }
        verify(exactly = 1) { repository.create(user.copy(password = "encoded")) }
    }

    // TODO: deal with user exists
}