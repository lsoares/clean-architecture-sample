package adapters.web

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import domain.UserRepository
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import persistence.MySqlUserRepository

object IntegrationTestWithMySql {

    private lateinit var webAppConfig: WebAppConfig
    private lateinit var dbServer: EmbeddedMysql
    private lateinit var userRepository: UserRepository

    @BeforeAll
    @JvmStatic
    fun setup() {
        val config = aMysqldConfig(Version.v5_7_latest).withPort(3301).withUser("user", "pass").build()
        dbServer = anEmbeddedMysql(config).addSchema("test_schema").start()
        userRepository = MySqlUserRepository(
            Database.connect(
                url = "jdbc:mysql://user:pass@localhost:3301/test_schema",
                driver = "com.mysql.cj.jdbc.Driver"
            )
        )
        userRepository.createSchema()
        webAppConfig = WebAppConfig(userRepository, 8081).apply { start() }
    }

    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll()
    }

    @Test
    fun `GIVEN a user's json, WHEN posting it, THEN it creates a user`() {
        IntegrationTest.`GIVEN a user's json, WHEN posting it, THEN it creates a user`()
    }

    @Test
    fun `GIVEN an existing user's json, WHEN posting it, THEN it creates only the first`() {
        IntegrationTest.`GIVEN an existing user's json, WHEN posting it, THEN it creates only the first`()
    }

    @AfterAll
    @JvmStatic
    fun afterAll() {
        webAppConfig.stop()
        dbServer.stop()
    }
}