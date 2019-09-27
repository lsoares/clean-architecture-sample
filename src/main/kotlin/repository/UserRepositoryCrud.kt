package repository

import domain.entities.UserInList
import domain.entities.UserToCreate

interface UserRepositoryCrud {
    fun findAll(): List<UserInList>
    fun create(user: UserToCreate)
}