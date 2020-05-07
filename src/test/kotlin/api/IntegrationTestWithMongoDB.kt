package api

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import domain.UserRepository
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import persistence.MongoDBUserRepository

class IntegrationTestWithMongoDB {

    private lateinit var webApp: WebApp
    private lateinit var mongodExe: MongodExecutable
    private lateinit var mongod: MongodProcess
    private lateinit var userRepository: UserRepository

    @BeforeAll
    fun setup() {
        mongodExe = MongodStarter.getDefaultInstance().prepare(
            MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net("localhost", 12345, Network.localhostIsIPv6()))
                .build()
        )
        mongod = mongodExe.start()
        userRepository = MongoDBUserRepository("localhost", 12345, "db123").apply { createSchema() }
        webApp = WebApp(userRepository, 8081).apply { start() }
    }

    @BeforeEach
    fun `before each`() {
        (userRepository as MongoDBUserRepository).deleteAll()
    }

    @Test
    fun `it creates two users when posting two different requests`() {
        IntegrationTest.`it creates two users when posting two different requests`()
    }

    @Test
    fun `it does not create a repeated user when posting twice`() {
        IntegrationTest.`it does not create a repeated user when posting twice`()
    }

    @Test
    fun `it deletes a user after creation`() {
        IntegrationTest.`it deletes a user after creation`()
    }

    @AfterAll
    fun `after all`() {
        webApp.close()
        mongod.stop()
        mongodExe.stop()
    }
}