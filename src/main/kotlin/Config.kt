import org.jetbrains.exposed.sql.Database
import adapters.persistence.MongoDBUserRepository
import adapters.persistence.MySqlUserRepository
import api.ListUsersHandler
import domain.usecases.CreateUser
import domain.usecases.ListUsers

object Config {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    val listUsers by lazy { ListUsers(repo) }
    val createUser by lazy { CreateUser(repo) }

    private val database by lazy {
        Database.connect(url = System.getenv("MYSQL_URL"), driver = "com.mysql.cj.jdbc.Driver")
    }
    private val userRepoMySql by lazy {
        MySqlUserRepository(database).apply { updateSchema() }
    }
    private val userRepoMongoDb by lazy {
        MongoDBUserRepository(
            System.getenv("MONGODB_HOST"),
            System.getenv("MONGODB_PORT").toInt(),
            "clean_demo"
        )
    }

    private val repo by lazy { userRepoMongoDb }
}
