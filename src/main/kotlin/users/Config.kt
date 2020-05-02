package users

import org.jetbrains.exposed.sql.Database
import users.persistence.MongoDBUserRepository
import users.persistence.MySqlUserRepository

object Config {
    val database = Database.connect(url = System.getProperty("DB_URL"), driver = "com.mysql.cj.jdbc.Driver")
    val userRepoMySql = MySqlUserRepository(database).apply { createSchema() }
    val userRepoMongoDb = MongoDBUserRepository("localhost", 12345, "db123")
}