package api

import Config
import adapters.persistence.MySqlUserRepository
import api.HttpDsl.`create user`
import api.HttpDsl.`delete user`
import api.HttpDsl.`list users`
import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import domain.ports.UserRepository
import domain.usecases.CreateUser
import domain.usecases.DeleteUser
import domain.usecases.ListUsers
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

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
        webApp = WebApp(object : Config() {
            override val repo get() = userRepository
        }, 8081).start()
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
        `create user`("luis.s@gmail.com", "Luís Soares", "password")
        `create user`("miguel.s@gmail.com", "Miguel Soares", "f47!3#$5g%")

        val userList = `list users`()

        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.s@gmail.com" },
                            { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            userList.body(),
            false
        )
    }

    @Test
    fun `do not create a repeated user when posting twice`() {
        val creation1Response = `create user`("luis.1@gmail.com", "Luís Soares", "password")
        val creation2Response = `create user`("luis.1@gmail.com", "Luís Soares", "password")

        val listResponse = `list users`()

        assertEquals(HttpStatus.CREATED_201, creation1Response.statusCode())
        assertEquals(HttpStatus.CONFLICT_409, creation2Response.statusCode())
        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.1@gmail.com" }] """,
            listResponse.body(), false
        )
    }

    @Test
    fun `delete a user after creation`() {
        `create user`("luis.s@gmail.com", "Luís Soares", "password")
        `create user`("miguel.s@gmail.com", "Miguel Soares", "f47!3#\$5g%")

        val deleteResponse = `delete user`("luis.s@gmail.com")

        assertEquals(HttpStatus.NO_CONTENT_204, deleteResponse.statusCode())
        val usersAfter = `list users`()
        JSONAssert.assertEquals(
            """ [ { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            usersAfter.body(),
            false
        )
    }
}