import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import org.eclipse.jetty.http.HttpStatus
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import repository.mysql.Schema
import repository.mysql.UserRepository
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.net.http.HttpResponse.BodyHandlers.ofString

object IntegrationTest {

    private lateinit var webAppConfig: WebAppConfig
    private lateinit var dbServer: EmbeddedMysql
    private lateinit var dbClient: Database
    private val httpClient = newHttpClient()

    @BeforeAll
    @JvmStatic
    fun setup() {
        val config = aMysqldConfig(Version.v5_7_latest).withPort(3301).withUser("user", "pass").build()
        dbServer = anEmbeddedMysql(config).addSchema("test_schema").start()
        val dbUrl = "jdbc:mysql://user:pass@localhost:3301/test_schema"
        dbClient = Database.connect(url = dbUrl, driver = "com.mysql.cj.jdbc.Driver")
        Schema(dbClient).create()

        webAppConfig = WebAppConfig(dbUrl = dbUrl, port = 8081)
        webAppConfig.start()
    }

    @BeforeEach
    fun beforeEach() {
        transaction(dbClient) { UserRepository.Users.deleteAll() }
    }

    @Test
    fun `GIVEN a user's json, WHEN posting it, THEN it creates a user`() {
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

        val response = httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())

        JSONAssert.assertEquals(
            """ [ { "id": 1, "name": "Luís Soares", "email": "lsoares@gmail.com" },
                            { "id": 2, "name": "Miguel Soares", "email": "miguel.s@gmail.com" } ] """,
            response.body(),
            true
        )
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
        dbServer.stop()
    }
}