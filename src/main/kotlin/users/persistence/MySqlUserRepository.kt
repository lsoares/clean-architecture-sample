package users.persistence

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import users.domain.EmailAddress
import users.domain.User
import users.domain.UserRepository

class MySqlUserRepository(private val database: Database) : UserRepository {

    private object UserSchema : Table("users") {
        val id = varchar("id", 36).primaryKey()
        val email = varchar("email", 50).uniqueIndex()
        val name = varchar("name", 50)
        val hashedPassword = varchar("hashedPassword", 50)
    }

    override fun findAll() = transaction(database) {
        UserSchema.selectAll().map {
            User(
                id = it[UserSchema.id],
                email = EmailAddress(it[UserSchema.email]),
                name = it[UserSchema.name],
                hashedPassword = it[UserSchema.hashedPassword]
            )
        }
    }

    override fun save(user: User) {
        transaction(database) {
            try {
                UserSchema.insert {
                    it[id] = user.id!!
                    it[email] = user.email.value
                    it[name] = user.name
                    it[hashedPassword] = user.hashedPassword!!
                }
            } catch (ex: ExposedSQLException) {
                if (ex.message != null && ex.message!!.contains("users_email_unique")) {
                    throw User.UserAlreadyExists()
                } else throw ex
            }
        }
    }

    override fun deleteAll() {
        transaction(database) { UserSchema.deleteAll() }
    }

    override fun createSchema() {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(UserSchema)
        }
    }
}
