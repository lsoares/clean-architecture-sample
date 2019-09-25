package listusers

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.classPathScript
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version.v5_7_latest
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("List users repository")
object RepositoryTest {

    private lateinit var dbServer: EmbeddedMysql
    private lateinit var dbClient: Database

    @BeforeAll
    @JvmStatic
    fun setup() {
        val config = aMysqldConfig(v5_7_latest).withPort(3306).withUser("user", "pass").build()
        dbServer = anEmbeddedMysql(config).addSchema("test_schema").start()
        dbClient = Database.connect("jdbc:mysql://user:pass@localhost:3306/test_schema", "com.mysql.cj.jdbc.Driver")

        transaction(dbClient) {
            SchemaUtils.create(object : IntIdTable("users") {
                val email = varchar("email", 50)
                val name = varchar("name", 50)
                val password = varchar("password", 50)
            })
        }
        dbServer.executeScripts("test_schema", classPathScript("add_users.sql"))
    }

    @Test
    fun `GIVEN a list of users in the database, WHEN requesting it, THEN it returns it`() {
        val result = Repository(dbClient).list()

        assertEquals(setOf(
                User(id = 1, email = "lsoares@gmail.com", name = "Luís Soares"),
                User(id = 2, email = "ms123@gmail.com", name = "Miguel Soares")
        ), result.toSet())
    }

    @AfterAll
    @JvmStatic
    fun tearDown() = dbServer.stop()
}