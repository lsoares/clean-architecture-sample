package domain

import javax.validation.ConstraintViolation

class InvalidUser(private val violations: MutableSet<ConstraintViolation<UserEntity>>) : Exception()