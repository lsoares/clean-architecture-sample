package repository.mysql

import domain.EmailAddress
import domain.UserEntity
import domain.UserRepository
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository(private val database: Database) : UserRepository {

    object UserSchema : Table("users") {
        val id = varchar("id", 36).primaryKey()
        val email = varchar("email", 50).uniqueIndex()
        val name = varchar("name", 50)
        val hashedPassword = varchar("hashedPassword", 50)
    }

    override fun findAll() = transaction(database) {
        UserSchema.selectAll().map {
            UserEntity(
                id = it[UserSchema.id],
                email = EmailAddress(it[UserSchema.email]),
                name = it[UserSchema.name],
                hashedPassword = it[UserSchema.hashedPassword]
            )
        }
    }

    override fun save(user: UserEntity) {
        transaction(database) {
            try {
                UserSchema.insert {
                    it[id] = user.id!!
                    it[email] = user.email.value
                    it[name] = user.name
                    it[hashedPassword] = user.hashedPassword ?: throw RuntimeException("password must be hashed first")
                }
            } catch (ex: ExposedSQLException) {
                if (ex.message != null && ex.message!!.contains("users_email_unique")) {
                    throw UserEntity.UserAlreadyExists()
                } else throw ex
            }
        }
    }

    override fun deleteAll() {
        transaction(database) { UserSchema.deleteAll() }
    }

    fun createSchema() {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(UserSchema)
        }
    }
}
