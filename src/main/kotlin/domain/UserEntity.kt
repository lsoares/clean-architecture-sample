package domain

import javax.validation.constraints.Email
import javax.validation.constraints.Size

data class UserEntity(
    val id: Int? = null,
    @field:Email val email: String,
    @field:Size(min = 2) val name: String,
    @field:Size(min = 5) val password: String? = null,
    val hashedPassword: String? = null
)