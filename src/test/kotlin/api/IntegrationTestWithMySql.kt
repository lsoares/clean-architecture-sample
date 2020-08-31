package api

import adapters.persistence.MySqlUserRepository
import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import domain.ports.UserRepository
import domain.usecases.CreateUser
import domain.usecases.ListUsers
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IntegrationTestWithMySql {

    private lateinit var webApp: WebApp
    private lateinit var dbServer: EmbeddedMysql
    private lateinit var userRepository: UserRepository

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        val config = aMysqldConfig(Version.v5_7_latest)
            .withPort(3301)
            .withUser("user", "pass")
        dbServer = anEmbeddedMysql(config.build()).addSchema("test_schema").start()
        userRepository = MySqlUserRepository(
            Database.connect(
                url = "jdbc:mysql://user:pass@localhost:3301/test_schema",
                driver = "com.mysql.cj.jdbc.Driver"
            )
        ).also { it.updateSchema() }
        webApp = WebApp(ListUsers(userRepository), CreateUser(userRepository), 8081)()
    }

    @BeforeEach
    fun `before each`() {
        (userRepository as MySqlUserRepository).deleteAll()
    }

    @AfterAll
    @Suppress("unused")
    fun `tear down`() {
        webApp.close()
        dbServer.stop()
    }

    @Test
    fun `create a user when posting a user json`() {
        IntegrationTest.`create two users when posting two different requests`()
    }

    @Test
    fun `do not create a repeated user when posting twice`() {
        IntegrationTest.`do not create a repeated user when posting twice`()
    }
}