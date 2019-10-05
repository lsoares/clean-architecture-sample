package repository.mysql

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import domain.EmailAddress
import domain.UserEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import repository.mysql.UserRepository.UserSchema

@DisplayName("Create user repository")
object CreateUserRepositoryTest {

    private lateinit var dbServer: EmbeddedMysql
    private lateinit var userRepository: UserRepository
    private lateinit var dbClient: Database

    @BeforeAll
    @JvmStatic
    fun beforeAll() {
        val config = aMysqldConfig(Version.v5_7_latest).withPort(3310).withUser("user", "pass").build()
        dbServer = anEmbeddedMysql(config).addSchema("test_schema").start()
        dbClient = Database.connect("jdbc:mysql://user:pass@localhost:3310/test_schema", "com.mysql.cj.jdbc.Driver")
        userRepository = UserRepository(dbClient)
        userRepository.createSchema()
    }

    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll()
    }

    @Test
    fun `GIVEN a user, WHEN storing it, THEN it's persisted`() {
        val user = UserEntity(
            id = "123",
            email = EmailAddress("lsoares@gmail.com"),
            name = "Luís Soares",
            hashedPassword = "hashed"
        )

        userRepository.save(user)

        val row = transaction(dbClient) {
            UserSchema.select { UserSchema.email eq user.email.value }.first()
        }
        assertEquals(
            user,
            UserEntity(
                id = row[UserSchema.id],
                email = EmailAddress(row[UserSchema.email]),
                hashedPassword = row[UserSchema.hashedPassword],
                name = row[UserSchema.name]
            )
        )
    }

    @Test
    fun `GIVEN an repeated user, WHEN storing it, THEN it's not persisted and an exception is thrown`() {
        val user = UserEntity(
            email = EmailAddress("lsoares@gmail.com"),
            name = "Luís Soares", hashedPassword = "hashed"
        )

        userRepository.save(user)
        assertThrows<UserEntity.UserAlreadyExists> {
            UserRepository(dbClient).save(user.copy(id = null))
        }

        transaction(dbClient) {
            UserSchema.slice(UserSchema.email.count()).select { UserSchema.email eq user.email.value }.first().run {
                assertEquals(1, this[UserSchema.email.count()])
            }
        }
    }

    @AfterAll
    @JvmStatic
    fun afterAll() = dbServer.stop()
}