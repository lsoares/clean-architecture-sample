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
import domain.usecases.CreateUser
import domain.usecases.ListUsers

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
        IntegrationTest.`create two users when posting two different requests`()
    }

    @Test
    fun `do not create a repeated user when posting twice`() {
        IntegrationTest.`do not create a repeated user when posting twice`()
    }

    @Disabled("please fix me")
    // TODO
    @Test
    fun `delete a user after creation`() {
        IntegrationTest.`delete a user after creation`()
    }
}