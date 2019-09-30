package domain

interface UserRepositoryCrud {
    fun findAll(): List<UserEntity>
    fun save(user: UserEntity)
}