import adapters.MongoDBUserRepository
import adapters.MySqlUserRepository
import domain.model.toUserId
import domain.ports.UserRepository
import domain.usecases.CreateUser
import domain.usecases.DeleteUser
import domain.usecases.ListUsers
import org.jetbrains.exposed.sql.Database
import org.litote.kmongo.KMongo
import java.util.*

abstract class Config {
    open val listUsers by lazy { ListUsers(repo) }
    open val createUser by lazy { CreateUser(repo) }
    open val deleteUser by lazy { DeleteUser(repo) }
    val generateUserId = { UUID.randomUUID().toString().toUserId() }
    abstract val repo: UserRepository
}

object ConfigWithMongoDb : Config() {
    override val repo by lazy {
        MongoDBUserRepository(
            KMongo
                .createClient(System.getenv("MONGODB_HOST"), System.getenv("MONGODB_PORT").toInt())
                .getDatabase("clean_demo")
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

