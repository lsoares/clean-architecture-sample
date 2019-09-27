package repository.mysql

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class Schema(private val dbClient: Database) {

    object Users : IntIdTable() {
        val email = varchar("email", 50).uniqueIndex()
        val name = varchar("name", 50)
        val password = varchar("password", 50)
    }

    fun create() {
        transaction(dbClient) {
            SchemaUtils.createMissingTablesAndColumns(Users)
        }
    }
}
