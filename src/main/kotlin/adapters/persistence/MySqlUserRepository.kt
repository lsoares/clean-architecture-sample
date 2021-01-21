package adapters.persistence

import domain.model.*
import domain.ports.UserRepository
import domain.ports.UserRepository.UserAlreadyExists
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLIntegrityConstraintViolationException

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
                id = it[UserSchema.id].toUserId(),
                email = it[UserSchema.email].toEmail(),
                name = it[UserSchema.name],
                password = Password(it[UserSchema.hashedPassword])
            )
        }
    }

    override fun save(user: User) {
        transaction(database) {
            try {
                UserSchema.insert {
                    it[id] = user.id.value
                    it[email] = user.email.value
                    it[name] = user.name
                    it[hashedPassword] = user.password.hashed
                }
            } catch (ex: ExposedSQLException) {
                ex.cause
                    ?.takeIf { it is SQLIntegrityConstraintViolationException }
                    ?.takeIf { it.message?.contains("users_email_unique") == true }
                    ?.let { throw UserAlreadyExists() }
                throw ex
            }
        }
    }

    override fun delete(email: Email) {
        transaction {
            UserSchema.deleteWhere {
                UserSchema.email eq email.value
            }
        }
    }

    fun deleteAll() {
        transaction(database) { UserSchema.deleteAll() }
    }

    fun updateSchema() {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(UserSchema)
        }
    }
}
