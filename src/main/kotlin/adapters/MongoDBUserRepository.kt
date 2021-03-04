package adapters

import com.mongodb.MongoWriteException
import com.mongodb.client.model.IndexOptions
import domain.model.*
import domain.ports.UserRepository
import domain.ports.UserRepository.SaveResult.NewUser
import domain.ports.UserRepository.SaveResult.UserAlreadyExists
import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

class MongoDBUserRepository(host: String, port: Int, database: String) : UserRepository {

    private val usersColection = KMongo
        .createClient(host, port)
        .getDatabase(database)
        .getCollection<UserSchema>("users")

    init {
        usersColection.createIndex(Document("email", 1), IndexOptions().unique(true))
    }

    private data class UserSchema(
        var id: String,
        val email: String,
        val name: String,
        var hashedPassword: String,
    )

    override fun findAll() =
        usersColection.find().toList().map {
            User(
                id = it.id.toUserId(),
                email = it.email.toEmail(),
                name = it.name,
                password = Password(it.hashedPassword)
            )
        }

    override fun save(user: User) = try {
        usersColection.save(
            UserSchema(
                id = user.id.value,
                email = user.email.value,
                name = user.name,
                hashedPassword = user.password.hashed
            )
        )
        NewUser
    } catch (ex: MongoWriteException) {
        if (ex.message!!.contains("email_1 dup key")) UserAlreadyExists
        else throw ex
    }

    override fun delete(email: Email) {
        usersColection.deleteOne(Document("email", email.value))
    }
}