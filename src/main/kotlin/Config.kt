import adapters.MySqlUserRepository
import domain.model.toUserId
import domain.ports.UserRepository
import domain.usecases.CreateUser
import domain.usecases.DeleteUser
import domain.usecases.ListUsers
import org.jetbrains.exposed.sql.Database
import java.util.*

abstract class Config {
    open val listUsers by lazy { ListUsers(repo) }
    private val generateUserId = { UUID.randomUUID().toString().toUserId() }
    open val createUser by lazy { CreateUser(repo, generateUserId) }
    open val deleteUser by lazy { DeleteUser(repo) }
    abstract val repo: UserRepository
}

object ConfigWithMySql : Config() {
    override val repo by lazy {
        MySqlUserRepository(
            Database.connect(url = System.getenv("MYSQL_URL"), driver = "com.mysql.cj.jdbc.Driver")
        )
    }
}
