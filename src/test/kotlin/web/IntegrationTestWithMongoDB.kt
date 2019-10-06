package web

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import domain.IdGenerator
import domain.UserRepository
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import persistence.MongoDBUserRepository
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.net.http.HttpResponse.BodyHandlers.ofString


object IntegrationTestWithMongoDB {

    private lateinit var webAppConfig: WebAppConfig
    private lateinit var userRepository: UserRepository
    private val httpClient = newHttpClient()

    @BeforeAll
    @JvmStatic
    fun setup() {
        val _mongodExe = MongodStarter.getDefaultInstance().prepare(
            MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net("localhost", 12345, Network.localhostIsIPv6()))
                .build()
        )
        val _mongod = _mongodExe.start()

//        _mongodExe.stop()
//        _mongod.stop()

        userRepository = MongoDBUserRepository("localhost", 12345, "db123").apply { createSchema() }

        webAppConfig = WebAppConfig(userRepository, 8081)
        webAppConfig.start()
    }

    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll()
    }

    @Test
    fun `GIVEN a user's json, WHEN posting it, THEN it creates a user`() {
        mockkObject(IdGenerator)
        every { IdGenerator.generate() } returns "1" andThen "2"
        httpClient.send(
            newBuilder()
                .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )
        httpClient.send(
            newBuilder()
                .POST(ofString(""" { "email": "miguel.s@gmail.com", "name": "Miguel Soares", "password": "f47!3#$5g%"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )

        val userList = httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())

        JSONAssert.assertEquals(
            """ [ { "id": "1", "name": "Luís Soares", "email": "lsoares@gmail.com" },
                            { "id": "2", "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            userList.body(),
            true
        )
        unmockkObject(IdGenerator)
    }

    @Test
    fun `GIVEN an existing user's json, WHEN posting it, THEN it creates only the first`() {
        val creationRequest = newBuilder()
            .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"} """))
            .uri(URI("http://localhost:8081/users")).build()
        val creation1Response = httpClient.send(creationRequest, discarding())
        assertEquals(HttpStatus.CREATED_201, creation1Response.statusCode())

        val creation2Response = httpClient.send(creationRequest, ofString())
        assertEquals(HttpStatus.CONFLICT_409, creation2Response.statusCode())

        val listResponse =
            httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())
        JSONAssert.assertEquals(
            """ [ { "name": "Luís Soares", "email": "lsoares@gmail.com" }] """,
            listResponse.body(), false
        )
    }

    @AfterAll
    @JvmStatic
    fun afterAll() {
        webAppConfig.stop()
    }
}