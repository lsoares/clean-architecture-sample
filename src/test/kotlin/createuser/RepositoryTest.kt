package createuser

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
import createuser.RepositoryTest.Users.email
import createuser.RepositoryTest.Users.id
import createuser.RepositoryTest.Users.name
import createuser.RepositoryTest.Users.password
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

@DisplayName("Create user repository")
object RepositoryTest {

    private lateinit var dbServer: EmbeddedMysql
    private lateinit var dbClient: Database

    object Users : IntIdTable() {
        val email = varchar("email", 50)
        val name = varchar("name", 50)
        val password = varchar("password", 50)
    }

    @BeforeAll
    @JvmStatic
    fun setup() {
        val config = MysqldConfig.aMysqldConfig(Version.v5_7_latest).withPort(3306).withUser("user", "pass").build()
        dbServer = EmbeddedMysql.anEmbeddedMysql(config).addSchema("test_schema").start()
        dbClient = Database.connect("jdbc:mysql://user:pass@localhost:3306/test_schema", "com.mysql.cj.jdbc.Driver")
        transaction(dbClient) { SchemaUtils.create(Users) }
    }

    @BeforeEach
    fun cleanup() {
        transaction(dbClient) { Users.deleteAll() }
    }

    @Test
    fun `GIVEN a user, WHEN storing it, THEN it's persisted and gets an id`() {
        val user = User("abc123", "lsoares@gmail.com", "Luís Soares")

        Repository(dbClient).createUser(user)

        val row = transaction(dbClient) {
            Users.select { email eq user.email }.first()
        }

        assertEquals(user, User(email = row[email], password = row[password], name = row[name]))
        assertTrue(row[id].value > 0)
    }

    @AfterAll
    @JvmStatic
    fun tearDown() = dbServer.stop()
}