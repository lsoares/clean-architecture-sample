package adapters

import domain.model.User
import domain.model.toEmail
import domain.model.toPassword
import domain.model.toUserId
import domain.ports.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer

class MySqlUserRepositoryWithDockerTest {

    private lateinit var dbServer: MySQLContainer<Nothing>
    private lateinit var userRepository: UserRepository
    private lateinit var database: Database

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        dbServer = MySQLContainer<Nothing>("mysql")
        dbServer.start()
        database = Database.connect(
            url = dbServer.jdbcUrl,
            user = dbServer.username,
            password = dbServer.password,
            driver = dbServer.driverClassName,
        )
        userRepository = MySqlUserRepository(database)
    }

    @AfterAll
    @Suppress("unused")
    fun `tear down`() {
        dbServer.stop()
    }

    @BeforeEach
    fun `before each`() {
        transaction(database) {
            object : Table("users") {}.deleteAll()
        }
    }

    @Test
    fun `store a user`() {
        val user = User("123".toUserId(), "l@x.y".toEmail(), "name", "password".toPassword())

        userRepository.save(user)

        assertEquals(listOf(user), userRepository.findAll())
    }

    @Test
    fun `delete a user`() {
        val user = User("123".toUserId(), "l@x.y".toEmail(), "name", "password".toPassword())
        userRepository.save(user)
        assertTrue(userRepository.findAll().isNotEmpty())

        userRepository.delete("l@x.y".toEmail())

        assertTrue(userRepository.findAll().isEmpty())
    }
}