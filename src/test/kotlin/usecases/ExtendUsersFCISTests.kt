package usecases

import domain.Email
import domain.User
import domain.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate.now

class ExtendUsersFCTests {

    @Test
    fun `extends validity of expired users until tomorrow`() {
        val expiredUser = exampleUser.copy(validUntil = yesterday)

        val result = ExtendUsersFC(listOf(expiredUser))

        assertEquals(listOf(expiredUser.copy(validUntil = tomorrow)), result)
    }

    @Test
    fun `skip and ignore valid users`() {
        val validUser = exampleUser.copy(name = "valid", validUntil = nextWeek)
        val expiredUser = exampleUser.copy(name = "expired", validUntil = yesterday)

        val result = ExtendUsersFC(listOf(validUser, expiredUser))

        assertEquals(listOf(expiredUser.copy(validUntil = tomorrow)), result)
    }

    private val exampleUser = User(email = Email("mail@example.com"), name = "Example Name")
    private val yesterday = now().minusDays(1)
    private val tomorrow = now().plusDays(1)
    private val nextWeek = now().plusWeeks(1)
}

class ExtendUsersISTests {

    @Test
    fun `repository is queried and users are saved`() { // HF: Happy path!
        val expiredUser = exampleUser.copy(name = "expired", validUntil = yesterday)
        val extendedUser = expiredUser.copy(name = "extended", validUntil = tomorrow)
        val repository = mockk<UserRepository>(relaxed = true) {
            every { findAll() } returns listOf(expiredUser)
            justRun { save(extendedUser) }
        }

        ExtendUsersIS(repository)()

        verifySequence {
            repository.findAll()
            repository.save(extendedUser)
        }
    }

    private val exampleUser = User(email = Email("mail@example.com"), name = "Example Name")
    private val yesterday = now().minusDays(1)
    private val tomorrow = now().plusDays(1)
}
