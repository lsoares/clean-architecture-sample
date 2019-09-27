package repository.mysql

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class Schema(private val dbClient: Database) {

    fun create() {
        transaction(dbClient) {
            SchemaUtils.createMissingTablesAndColumns(UserRepository.Users)
        }
    }
}
