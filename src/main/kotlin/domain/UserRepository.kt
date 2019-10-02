package domain

interface UserRepository {
    fun findAll(): List<UserEntity>
    fun save(user: UserEntity)
    fun deleteAll()
}