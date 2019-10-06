package domain

interface UserRepository {
    fun createSchema()
    fun findAll(): List<User>
    fun save(user: User)
    fun deleteAll()
}