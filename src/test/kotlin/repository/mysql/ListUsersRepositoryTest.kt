package repository.mysql

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.classPathScript
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version.v5_7_latest
import domain.UserEntity
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("List users repository")
object ListUsersRepositoryTest {

    private lateinit var dbServer: EmbeddedMysql
    private lateinit var dbClient: Database

    @BeforeAll
    @JvmStatic
    fun setup() {
        val config = aMysqldConfig(v5_7_latest).withPort(3300).withUser("user", "pass").build()
        dbServer = anEmbeddedMysql(config).addSchema("test_schema").start()
        dbClient = Database.connect("jdbc:mysql://user:pass@localhost:3300/test_schema", "com.mysql.cj.jdbc.Driver")

        UserRepository(dbClient).createSchema()
        // TODO: don't use a file insert from here
        dbServer.executeScripts("test_schema", classPathScript("add_users.sql"))
    }

    @Test
    fun `GIVEN a list of users in the database, WHEN requesting it, THEN it returns it`() {
        val result = UserRepository(dbClient).findAll()

        assertEquals(
            setOf(
                UserEntity(id = "a1", email = "lsoares@gmail.com", name = "Luís Soares", hashedPassword = "hashed1"),
                UserEntity(id = "a2", email = "ms123@gmail.com", name = "Miguel Soares", hashedPassword = "hashed2")
            ), result.toSet()
        )
    }

    @AfterAll
    @JvmStatic
    fun afterAll() = dbServer.stop()
}