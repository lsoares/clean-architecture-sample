package users.domain

interface UserRepository {
    fun findAll(): List<User>
    fun save(user: User)
}