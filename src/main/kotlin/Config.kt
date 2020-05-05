import org.jetbrains.exposed.sql.Database
import persistence.MongoDBUserRepository
import persistence.MySqlUserRepository

object Config {
    private val database by lazy {
        Database.connect(url = System.getenv("MYSQL_URL"), driver = "com.mysql.cj.jdbc.Driver")
    }
    val userRepoMySql by lazy { MySqlUserRepository(database).apply { createSchema() } }
    val userRepoMongoDb by lazy {
        MongoDBUserRepository(
            System.getenv("MONGODB_HOST"),
            System.getenv("MONGODB_PORT").toInt(),
            "clean_demo"
        )
    }
}

/*
    export MYSQL_URL=jdbc:mysql://localhost:3306/clean_demo
    docker run --name mysql_demo -e MYSQL_ALLOW_EMPTY_PASSWORD=true -e MYSQL_DATABASE=clean_demo -p 3306:3306 -d mysql
    docker stop mysql_demo
 */
/*
    export MONGODB_HOST=localhost
    export MONGODB_PORT=27017
    docker run --name mongodb_demo -p 27017:27017 mongo
    docker stop mongodb_demo
 */
