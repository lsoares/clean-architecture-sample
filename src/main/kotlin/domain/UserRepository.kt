package domain

interface UserRepository {
    fun findAll(): List<User>
    fun save(user: User)
    fun deleteAll()
}