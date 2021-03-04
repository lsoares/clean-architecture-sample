import adapters.MongoDBUserRepository
import adapters.MySqlUserRepository
import domain.ports.UserRepository
import domain.usecases.CreateUser
import domain.usecases.DeleteUser
import domain.usecases.ListUsers
import org.jetbrains.exposed.sql.Database

abstract class Config {
    open val listUsers by lazy { ListUsers(repo) }
    open val createUser by lazy { CreateUser(repo) }
    open val deleteUser by lazy { DeleteUser(repo) }
    abstract val repo: UserRepository
}

object ConfigWithMongoDb : Config() {
    override val repo by lazy {
        MongoDBUserRepository(
            System.getenv("MONGODB_HOST"),
            System.getenv("MONGODB_PORT").toInt(),
            "clean_demo"
        )
    }
}

object ConfigWithMySql : Config() {
    override val repo by lazy {
        MySqlUserRepository(
            Database.connect(url = System.getenv("MYSQL_URL"), driver = "com.mysql.cj.jdbc.Driver")
        )
    }
}

