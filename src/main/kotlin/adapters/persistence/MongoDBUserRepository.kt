package adapters.persistence

import com.mongodb.MongoWriteException
import com.mongodb.client.model.IndexOptions
import domain.model.Password
import domain.model.User
import domain.model.toEmail
import domain.model.toUserId
import domain.ports.UserRepository
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

class MongoDBUserRepository(host: String, port: Int, database: String) : UserRepository {

    private val usersColection = KMongo
        .createClient(host, port)
        .getDatabase(database)
        .getCollection<UserSchema>("users")

    private data class UserSchema(
        var id: String,
        val email: String,
        val name: String,
        var hashedPassword: String,
    )

    fun createSchema() {
        usersColection.createIndex(Document("email", 1), IndexOptions().unique(true))
    }

    override fun findAll() =
        usersColection.find().toList().map {
            User(
                id = it.id.toUserId(),
                email = it.email.toEmail(),
                name = it.name,
                password = Password(it.hashedPassword)
            )
        }

    override fun save(user: User) {
        try {
            usersColection.save(
                UserSchema(
                    id = user.id.value,
                    email = user.email.value,
                    name = user.name,
                    hashedPassword = user.password.hashed
                )
            )
        } catch (ex: MongoWriteException) {
            throw if (ex.message!!.contains("email_1 dup key")) UserRepository.UserAlreadyExists() else ex
        }
    }

    fun deleteAll() {
        usersColection.deleteMany(Document())
    }
}