package repository.mysql

import createuser.UserAlreadyExists
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import repository.UserRepositoryCrud

class UserRepository(private val database: Database) : UserRepositoryCrud {

    object Users : IntIdTable() {
        val email = varchar("email", 50).uniqueIndex()
        val name = varchar("name", 50)
        val password = varchar("password", 50)
    }

    override fun findAll(): List<listusers.User> {
        return transaction(database) {
            Users.selectAll().map {
                listusers.User(id = it[Users.id].value, email = it[Users.email], name = it[Users.name])
            }
        }
    }

    override fun create(user: createuser.User) {
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