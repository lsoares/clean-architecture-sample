package api

import Config
import adapters.MySqlUserRepository
import api.HttpDsl.`create user`
import api.HttpDsl.`delete user`
import api.HttpDsl.`list users`
import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import domain.ports.UserRepository
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
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
    private lateinit var database: Database

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        dbServer = anEmbeddedMysql(aMysqldConfig(Version.v5_7_latest).build())
            .addSchema("test_schema")
            .start()
        database = with(dbServer.config) {  Database.connect(
            url = "jdbc:mysql://$username:$password@localhost:$port/test_schema",
            driver = "com.mysql.cj.jdbc.Driver"
        ) }
        userRepository = MySqlUserRepository(database)
        webApp = WebApp(object : Config() {
            override val repo = userRepository
        }, 8081).start()
    }

    @BeforeEach
    fun `before each`() {
        transaction(database) {
            object : Table("users") {}.deleteAll()
        }
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