package createuser

import domain.UserEntity
import javax.validation.ConstraintViolation

class InvalidUserException(private val violations: MutableSet<ConstraintViolation<UserEntity>>) : Exception()