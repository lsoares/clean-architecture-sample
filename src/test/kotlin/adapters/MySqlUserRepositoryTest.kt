package adapters

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
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

class MySqlUserRepositoryTest {

    private lateinit var dbServer: EmbeddedMysql
    private lateinit var userRepository: UserRepository
    private val database = Database.connect(
        url = "jdbc:mysql://user:pass@localhost:3301/test_schema",
        driver = "com.mysql.cj.jdbc.Driver"
    )

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        val config = MysqldConfig.aMysqldConfig(Version.v5_7_latest)
            .withPort(3301)
            .withUser("user", "pass")
        dbServer = EmbeddedMysql.anEmbeddedMysql(config.build()).addSchema("test_schema").start()
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