import org.jetbrains.exposed.sql.Database
import persistence.MongoDBUserRepository
import persistence.MySqlUserRepository

object Config {
    private val database by lazy {
        Database.connect(url = System.getenv("MYSQL_URL"), driver = "com.mysql.cj.jdbc.Driver")
    }
    val userRepoMySql by lazy {
        MySqlUserRepository(database).apply { createSchema() }
    }
    val userRepoMongoDb by lazy {
        MongoDBUserRepository(
            System.getenv("MONGODB_HOST"),
            System.getenv("MONGODB_PORT").toInt(),
            "clean_demo"
        )
    }
}

/*
    MYSQL_URL=jdbc:mysql://root:my-secret-pw@localhost:3306/clean_demo
    docker stop mysql_demo && docker rm mysql_demo
    docker run --name mysql_demo -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=clean_demo -p 3306:3306 -d mysql
 */

/*
    MONGODB_HOST=localhost
    MONGODB_PORT=27017
    docker stop mongodb_demo && docker rm mongodb_demo
    docker run --rm --name mongodb_demo -p 27017:27017 mongo
*/
