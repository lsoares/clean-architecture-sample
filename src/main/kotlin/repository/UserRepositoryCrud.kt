package repository

interface UserRepositoryCrud {
    fun findAll(): List<listusers.User>
    fun create(user: createuser.User)
}