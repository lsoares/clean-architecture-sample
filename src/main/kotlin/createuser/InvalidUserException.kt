package createuser

import domain.entities.UserToCreate
import javax.validation.ConstraintViolation

class InvalidUserException(private val violations: MutableSet<ConstraintViolation<UserToCreate>>) : Exception()