package domain

import javax.validation.Validation
import javax.validation.constraints.Email

data class Email(@field:Email val value: String) {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    init {
        validator.validate(this).apply {
            require(isEmpty()) { throw InvalidEmail() }
        }
    }

    class InvalidEmail : Exception()
}

fun String.toEmail() = domain.Email(this)