package listusers

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class Repository(private val database: Database) {

    private object Users : IntIdTable() {
        val email = varchar("email", 50)
        val name = varchar("name", 50)
    }

    fun list() = transaction(database) {
        Users.selectAll().map {
            User(id = it[Users.id].value, email = it[Users.email], name = it[Users.name])
        }
    }
}