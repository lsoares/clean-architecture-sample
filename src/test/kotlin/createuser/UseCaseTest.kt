package createuser

import domain.entities.UserToCreate
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import repository.UserRepositoryCrud

@DisplayName("Create user use case")
object UseCaseTest {

    @Test
    fun `GIVEN a valid user, WHEN running the use case, THEN it calls the repo`() {
        val user = UserToCreate(email = "lsoares@gmail.com", name = "Luís Soares", password = "toEncode")
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

    @Test
    fun `GIVEN an invalid user, WHEN running the use case, THEN it throws exception and does not call the repo`() {
        val repository = mockk<UserRepositoryCrud>()

        assertThrows<InvalidUserException> {
            UseCase(repository, PasswordEncoder())
                .createUser(UserToCreate(email = "lsoares", name = "L", password = "1"))
        }

        verify { repository wasNot Called }
    }
}