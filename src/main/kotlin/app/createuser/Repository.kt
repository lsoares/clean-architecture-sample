package app.createuser

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class Repository(private val database: Database) {

    fun createUser(user: User) {
        // TODO : deal with repeated users
        transaction(database) {
            Users.insert {
                it[email] = user.email
                it[name] = user.name
                it[password] = user.password
            }
        }
    }

    object Users : IntIdTable() {
        val email = varchar("email", 50)
        val name = varchar("name", 50)
        val password = varchar("password", 50)
    }
}