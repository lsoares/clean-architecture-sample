package features

import domain.UserEntity
import domain.UserRepository
import io.mockk.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Create user use case")
object CreateUserTest {

    @Test
    fun `GIVEN a valid user, WHEN running the use case, THEN it calls the repo`() {
        val user = UserEntity(email = "lsoares@gmail.com", name = "Luís Soares", password = "toEncode")
        val repository = mockk<UserRepository> {
            every { save(user.copy(hashedPassword = "encoded")) } just Runs
        }

        CreateUser(repository).execute(user)

        verify(exactly = 1) { repository.save(user.copy(hashedPassword = "encoded")) }
    }

    @Test
    fun `GIVEN an invalid user, WHEN running the use case, THEN it throws exception and does not call the repo`() {
        val repository = mockk<UserRepository>()

        assertThrows<UserEntity.InvalidUser> {
            CreateUser(repository)
                .execute(UserEntity(email = "lsoares", name = "L", password = "1"))
        }

        verify { repository wasNot Called }
    }
}