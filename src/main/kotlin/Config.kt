import org.jetbrains.exposed.sql.Database
import persistence.MongoDBUserRepository
import persistence.MySqlUserRepository

object Config {
    private val database = Database.connect(url = System.getProperty("DB_URL"), driver = "com.mysql.cj.jdbc.Driver")
    val userRepoMySql = MySqlUserRepository(database).apply { createSchema() }
    val userRepoMongoDb = MongoDBUserRepository("localhost", 12345, "db123")
}