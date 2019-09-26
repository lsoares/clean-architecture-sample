package listusers

import Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class Repository(private val database: Database) {

    fun list() = transaction(database) {
        Users.selectAll().map {
            User(id = it[Users.id].value, email = it[Users.email], name = it[Users.name])
        }
    }
}