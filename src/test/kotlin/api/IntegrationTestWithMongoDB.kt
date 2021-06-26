package api

import Config
import adapters.MongoDBUserRepository
import api.HttpDsl.`create user`
import api.HttpDsl.`delete user`
import api.HttpDsl.`list users`
import com.mongodb.client.MongoDatabase
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import domain.ports.UserRepository
import org.bson.Document
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.KMongo
import org.skyscreamer.jsonassert.JSONAssert

class IntegrationTestWithMongoDB {

    private lateinit var webApp: WebApp
    private lateinit var dbServer: MongodExecutable
    private lateinit var mongod: MongodProcess
    private lateinit var userRepository: UserRepository
    private lateinit var database: MongoDatabase

    @BeforeAll
    @Suppress("unused")
    fun setup() {
        dbServer = MongodStarter.getDefaultInstance().prepare(
            MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net("localhost", 12345, Network.localhostIsIPv6()))
                .build()
        )
        mongod = dbServer.start()
        database = KMongo
            .createClient("localhost", 12345)
            .getDatabase("db123")
        userRepository = MongoDBUserRepository(database)
        webApp = WebApp(object : Config() {
            override val repo = userRepository
        }, 8081).start()
    }

    @BeforeEach
    fun `before each`() {
        database.getCollection("users")
            .deleteMany(Document())
    }

    @AfterAll
    @Suppress("unused")
    fun `after all`() {
        webApp.close()
        mongod.stop()
        dbServer.stop()
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
        `create user`("luis.1@gmail.com", "Luís Soares", "password")

        val creation2Response = `create user`("luis.1@gmail.com", "Luís Soares", "password")

        assertEquals(HttpStatus.CONFLICT_409, creation2Response.statusCode())
        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "luis.1@gmail.com" }] """,
            `list users`().body(),
            false
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