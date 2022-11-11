package api

import Config
import adapters.MySqlUserRepository
import api.HttpDsl.`create user`
import api.HttpDsl.`delete user`
import api.HttpDsl.`list users`
import com.fasterxml.jackson.databind.ObjectMapper
import domain.ports.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MySQLContainer

class CreateUserTest {

    private lateinit var webApp: WebApp
    private lateinit var userRepository: UserRepository
    private lateinit var dbServer: MySQLContainer<Nothing>
    private lateinit var database: Database

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        dbServer = MySQLContainer<Nothing>("mysql")
        dbServer.start()
        database = Database.connect(
            url = dbServer.jdbcUrl,
            user = dbServer.username,
            password = dbServer.password,
            driver = dbServer.driverClassName,
        )
        userRepository = MySqlUserRepository(database)
        webApp = WebApp(object : Config() {
            override val repo = userRepository
        }, 8081).start()
    }

    @AfterAll
    @Suppress("unused")
    fun `tear down`() {
        webApp.close()
        dbServer.stop()
    }

    @BeforeEach
    fun `before each`() {
        transaction(database) {
            object : Table("users") {}.deleteAll()
        }
    }

    @Test
    fun `create a user`() {
        `create user`("luis.s@gmail.com", "Luís Soares", "password")

        val userList = `list users`()

        assertEquals(
            ObjectMapper().readTree(""" [ { "name": "Luís Soares", "email": "luis.s@gmail.com" } ] """),
            ObjectMapper().readTree(userList.body()),
        )
    }

    @Test
    fun `do not allow creating user with same email`() {
        `create user`("luis.1@gmail.com", "Luís Soares", "password")

        val creation2Response = `create user`("luis.1@gmail.com", "Luís Soares", "password")

        assertEquals(409, creation2Response.statusCode())
        assertEquals(
            ObjectMapper().readTree(""" [ { "name": "Luís Soares", "email": "luis.1@gmail.com" }] """),
            ObjectMapper().readTree(`list users`().body()),
        )
    }

    @Test
    fun `delete a user`() {
        `create user`("luis.s@gmail.com", "Luís Soares", "password")
        `create user`("miguel.s@gmail.com", "Miguel Soares", "f47!3#\$5g%")

        val deleteResponse = `delete user`("luis.s@gmail.com")

        assertEquals(204, deleteResponse.statusCode())
        val usersAfter = `list users`()
        assertEquals(
            ObjectMapper().readTree(""" [ { "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """),
            ObjectMapper().readTree(usersAfter.body()),
        )
    }
}
