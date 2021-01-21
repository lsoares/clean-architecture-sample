package api

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import domain.ports.UserRepository
import org.junit.jupiter.api.*
import adapters.persistence.MongoDBUserRepository
import api.HttpDsl.`create user`
import api.HttpDsl.`delete user`
import api.HttpDsl.`list users`
import domain.usecases.CreateUser
import domain.usecases.ListUsers
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.skyscreamer.jsonassert.JSONAssert

class IntegrationTestWithMongoDB {

    private lateinit var webApp: WebApp
    private lateinit var mongodExe: MongodExecutable
    private lateinit var mongod: MongodProcess
    private lateinit var userRepository: UserRepository

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        mongodExe = MongodStarter.getDefaultInstance().prepare(
            MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net("localhost", 12345, Network.localhostIsIPv6()))
                .build()
        )
        mongod = mongodExe.start()
        userRepository = MongoDBUserRepository("localhost", 12345, "db123").apply { createSchema() }
        webApp = WebApp(ListUsers(userRepository), CreateUser(userRepository), 8081)()
    }

    @BeforeEach
    fun `before each`() {
        (userRepository as MongoDBUserRepository).deleteAll()
    }

    @AfterAll
    @Suppress("unused")
    fun `after all`() {
        webApp.close()
        mongod.stop()
        mongodExe.stop()
    }

    @Test
    fun `create two users when posting two different requests`() {
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

        val listResponse = `list users`().body()

        assertEquals(HttpStatus.CREATED_201, creation1Response.statusCode())
        assertEquals(HttpStatus.CONFLICT_409, creation2Response.statusCode())
        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.1@gmail.com" }] """,
            listResponse, false
        )
    }

    @Disabled("please fix me")
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