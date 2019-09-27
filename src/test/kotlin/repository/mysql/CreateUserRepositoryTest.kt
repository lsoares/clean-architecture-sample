package repository.mysql

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import createuser.UserAlreadyExists
import domain.entities.UserToCreate
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import repository.mysql.UserRepository.Users

@DisplayName("Create user repository")
object CreateUserRepositoryTest {

    private lateinit var dbServer: EmbeddedMysql
    private lateinit var dbClient: Database

    @BeforeAll
    @JvmStatic
    fun beforeAll() {
        val config = aMysqldConfig(Version.v5_7_latest).withPort(3306).withUser("user", "pass").build()
        dbServer = anEmbeddedMysql(config).addSchema("test_schema").start()
        dbClient = Database.connect("jdbc:mysql://user:pass@localhost:3306/test_schema", "com.mysql.cj.jdbc.Driver")
        Schema(dbClient).create()
    }

    @BeforeEach
    fun beforeEach() {
        transaction(dbClient) { Users.deleteAll() }
    }

    @Test
    fun `GIVEN a user, WHEN storing it, THEN it's persisted and gets an id`() {
        val user = UserToCreate("abc123", "lsoares@gmail.com", "Luís Soares")

        UserRepository(dbClient).create(user)

        val row = transaction(dbClient) {
            Users.select { Users.email eq user.email }.first()
        }
        assertEquals(user, UserToCreate(email = row[Users.email], password = row[Users.password], name = row[Users.name]))
        assertTrue(row[Users.id].value > 0)
    }

    @Test
    fun `GIVEN an existing user, WHEN storing it, THEN it's not persisted and an exception is thrown`() {
        val user = UserToCreate("abc123", "lsoares@gmail.com", "Luís Soares")

        UserRepository(dbClient).create(user)
        assertThrows<UserAlreadyExists> {
            UserRepository(dbClient).create(user)
        }

        transaction(dbClient) {
            Users.slice(Users.email.count()).select { Users.email eq user.email }.first().run {
                assertEquals(1, this[Users.email.count()])
            }
        }
    }

    @AfterAll
    @JvmStatic
    fun afterAll() = dbServer.stop()
}