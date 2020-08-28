package usecases

import domain.Email
import domain.User
import domain.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate.now

class ExtendUsersDITests {

    @Test
    fun `extend validity of expired users until tomorrow`() {
        val expiredUser = exampleUser.copy(validUntil = yesterday)
        val arg = slot<User>()
        val repository = mockk<UserRepository> {
            every { findAll() } returns listOf(expiredUser)
            justRun { save(capture(arg)) }
        }

        ExtendUsersDI(repository)()

        assertEquals(expiredUser.copy(validUntil = tomorrow), arg.captured)
    }

    @Test
    fun `skip and ignore valid users`() {
        val validUser = exampleUser.copy(name = "valid", validUntil = nextWeek)
        val expiredUser = exampleUser.copy(name = "expired", validUntil = yesterday)
        val repository = mockk<UserRepository> {
            every { findAll() } returns listOf(validUser, expiredUser)
            justRun { save(any()) }
        }

        ExtendUsersDI(repository)()

        verifySequence {
            repository.findAll()
            repository.save(expiredUser.copy(validUntil = tomorrow))
        }
    }

    private val exampleUser = User(email = Email("mail@example.com"), name = "Example Name")
    private val yesterday = now().minusDays(1)
    private val tomorrow = now().plusDays(1)
    private val nextWeek = now().plusWeeks(1)
}
