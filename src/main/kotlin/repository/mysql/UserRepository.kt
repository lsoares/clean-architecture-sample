package repository.mysql

import domain.UserEntity
import domain.UserRepository
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository(private val database: Database) : UserRepository {

    object Users : IntIdTable() {
        val email = varchar("email", 50).uniqueIndex()
        val name = varchar("name", 50)
        val hashedPassword = varchar("hashedPassword", 50)
    }

    override fun findAll(): List<UserEntity> {
        return transaction(database) {
            Users.selectAll().map {
                UserEntity(
                    id = it[Users.id].value,
                    email = it[Users.email],
                    name = it[Users.name],
                    hashedPassword = it[Users.hashedPassword]
                )
            }
        }
    }

    override fun save(user: UserEntity) {
        transaction(database) {
            try {
                Users.insert {
                    it[email] = user.email
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

    fun createSchema() {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Users)
        }
    }
}