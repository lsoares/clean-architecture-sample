package createuser

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import repository.mysql.Schema.Users

class Repository(private val database: Database) {

    fun createUser(user: User) {
        transaction(database) {
            try {
                Users.insert {
                    it[email] = user.email
                    it[name] = user.name
                    it[password] = user.password
                }
            } catch (ex: ExposedSQLException) {
                if (ex.message != null && ex.message!!.contains("users_email_unique")) {
                    throw UserAlreadyExists()
                } else throw ex
            }
        }
    }
}