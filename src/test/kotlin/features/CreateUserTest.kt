package features

import domain.InvalidUser
import domain.UserEntity
import domain.UserRepositoryCrud
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Create user use case")
object CreateUserTest {

    @Test
    fun `GIVEN a valid user, WHEN running the use case, THEN it calls the repo`() {
        val user = UserEntity(email = "lsoares@gmail.com", name = "Luís Soares", password = "toEncode")
        val repository = mockk<UserRepositoryCrud> {
            every { save(user.copy(hashedPassword = "encoded")) } just Runs
        }
        val passwordEncoder = mockk<PasswordEncoder> {
            every { encode("toEncode") } returns "encoded"
        }

        CreateUser(repository, passwordEncoder).execute(user)

        verify(exactly = 1) { passwordEncoder.encode("toEncode") }
        verify(exactly = 1) { repository.save(user.copy(hashedPassword = "encoded")) }
    }

    @Test
    fun `GIVEN an invalid user, WHEN running the use case, THEN it throws exception and does not call the repo`() {
        val repository = mockk<UserRepositoryCrud>()

        assertThrows<InvalidUser> {
            CreateUser(repository, PasswordEncoder())
                .execute(UserEntity(email = "lsoares", name = "L", hashedPassword = "1"))
        }

        verify { repository wasNot Called }
    }
}