package listusers

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import repository.UserRepositoryCrud

@DisplayName("List users use case")
object UseCaseTest {

    @Test
    fun `GIVEN a list of users, WHEN requesting it, THEN it returns it`() {
        val repository = mockk<UserRepositoryCrud> {
            every { findAll() } returns listOf(User(1, "email", "Luís Soares"))
        }

        val users = UseCase(repository).list()

        verify(exactly = 1) { repository.findAll() }
        assertEquals(listOf(User(1, "email", "Luís Soares")), users)
    }
}