package adapters.web

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

object IntegrationTestWithMongoDB {

    private lateinit var webAppConfig: WebAppConfig
    private lateinit var mongodExe: MongodExecutable
    private lateinit var mongod: MongodProcess
    private lateinit var userRepository: UserRepository

    @BeforeAll
    @JvmStatic
    fun setup() {
        mongodExe = MongodStarter.getDefaultInstance().prepare(
            MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net("localhost", 12345, Network.localhostIsIPv6()))
                .build()
        )
        mongod = mongodExe.start()
        userRepository = MongoDBUserRepository("localhost", 12345, "db123").apply { createSchema() }
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
        mongod.stop()
        mongodExe.stop()
    }
}