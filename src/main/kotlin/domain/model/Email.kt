package domain.model

import java.util.regex.Pattern

data class Email(val value: String) {

    private val emailAddressPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    init {
       require(emailAddressPattern.matcher(value).matches()) { throw InvalidEmail() }
    }

    class InvalidEmail : Exception()
}

fun String.toEmail() = Email(this)