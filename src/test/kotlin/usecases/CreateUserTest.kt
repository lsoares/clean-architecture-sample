package usecases

import domain.EmailAddress
import domain.User
import domain.UserRepository
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Create user use case")
class CreateUserTest {

    @Test
    fun `GIVEN a valid user, WHEN running the use case, THEN it calls the repo`() {
        val user = User(email = EmailAddress("lsoares@gmail.com"), name = "Luís Soares", password = "toEncode")
        val repository = mockk<UserRepository> {
            every { save(user.copy(hashedPassword = "encoded")) } just Runs
        }

        CreateUser(repository).execute(user)

        verify(exactly = 1) { repository.save(user.copy(hashedPassword = "encoded")) }
    }
}