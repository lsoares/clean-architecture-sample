package domain.usecases

import domain.model.User
import domain.model.toEmail
import domain.model.toPassword
import domain.ports.UserRepository
import domain.ports.UserRepository.SaveResult.NewUser
import io.mockk.*
import org.junit.jupiter.api.Test

class CreateUserTest {

    @Test
    fun `it calls the repo when saving a user`() {
        val user = User(email = "luis.s@gmail.com".toEmail(), name = "Luís Soares", password = "toEncode".toPassword())
        val repository = mockk<UserRepository> {
            every { save(user) } returns NewUser
        }
        val createUser = CreateUser(repository)

        createUser(user)

        verify(exactly = 1) { repository.save(user) }
    }
}