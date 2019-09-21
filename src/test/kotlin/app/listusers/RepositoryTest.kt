package app.listusers

import app.User
import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.classPathScript
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version.v5_7_latest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@DisplayName("List users repository")
object RepositoryTest {

    private lateinit var dbServer: EmbeddedMysql
    private lateinit var dbClient: Database

    @BeforeAll
    @JvmStatic
    fun setupDb() {
        val config = aMysqldConfig(v5_7_latest).withPort(3306).withUser("user", "pass").build()
        dbServer = anEmbeddedMysql(config).addSchema("test_schema").start()
        dbClient = Database.connect("jdbc:mysql://user:pass@localhost:3306/test_schema", "com.mysql.cj.jdbc.Driver")
    }

    @BeforeEach
    fun setupData() {
        transaction {
            SchemaUtils.create(object : Table("users") {
                val id = varchar("id", 10).primaryKey()
                val email = varchar("email", 50)
                val name = varchar("name", 50)
                val password = varchar("password", 50)
            })
        }
    }

    @Test
    fun `GIVEN a list of users in the database, WHEN requesting it, THEN it returns it`() {
        dbServer.executeScripts("test_schema", classPathScript("add_users.sql"))

        val result = Repository(dbClient).list()

        val expected = setOf(
                User("abc123", "lsoares@gmail.com", "Luís Soares", "hashedpassword"),
                User("bcd123", "ms123@gmail.com", "Miguel Soares", "fdsgerth56ut45")
        )
        assertEquals(expected, result.toSet())
    }

    @AfterAll
    @JvmStatic
    fun cleanUp() {
        dbServer.stop()
    }
}