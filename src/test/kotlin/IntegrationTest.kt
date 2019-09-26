import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.net.URI
import java.net.http.HttpClient.newHttpClient
import java.net.http.HttpRequest.BodyPublishers.ofString
import java.net.http.HttpRequest.newBuilder
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.net.http.HttpResponse.BodyHandlers.ofString

object IntegrationTest {

    private lateinit var webAppConfig: WebAppConfig
    private lateinit var embeddedMysql: EmbeddedMysql
    private val httpClient = newHttpClient()

    @BeforeAll
    @JvmStatic
    fun setup() {
        val config = aMysqldConfig(Version.v5_7_latest).withPort(3306).withUser("user", "pass").build()
        embeddedMysql = anEmbeddedMysql(config).addSchema("test_schema").start()
        val dbUrl = "jdbc:mysql://user:pass@localhost:3306/test_schema"

        transaction(Database.connect(url = dbUrl, driver = "com.mysql.cj.jdbc.Driver")) {
            SchemaUtils.create(object : IntIdTable("users") {
                val email = varchar("email", 50)
                val name = varchar("name", 50)
                val password = varchar("password", 50)
            })
        }

        webAppConfig = WebAppConfig(dbUrl = dbUrl, port = 8081)
        webAppConfig.start()
    }

    @Test
    fun `GIVEN a json, WHEN posting it, THEN it creates a user`() {
        httpClient.send(newBuilder()
                .POST(ofString(""" { "email": "lsoares@gmail.com", "name": "Luís Soares", "password": "password"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )
        httpClient.send(newBuilder()
                .POST(ofString(""" { "email": "miguel.s@gmail.com", "name": "Miguel Soares", "password": "f47!3#$5g%"} """))
                .uri(URI("http://localhost:8081/users")).build(), discarding()
        )

        val response = httpClient.send(newBuilder().GET().uri(URI("http://localhost:8081/users")).build(), ofString())

        JSONAssert.assertEquals(""" [
            { "id": 1, "name": "Luís Soares", "email": "lsoares@gmail.com" },
            { "id": 2, "name": "Miguel Soares", "email": "miguel.s@gmail.com" }
        ] """, response.body(), true)
    }

    // TODO: test user already exists

    @AfterAll
    @JvmStatic
    fun afterAll() {
        webAppConfig.stop()
        embeddedMysql.stop()
    }
}