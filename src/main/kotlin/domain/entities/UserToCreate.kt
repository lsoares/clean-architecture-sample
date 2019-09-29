package domain.entities

import javax.validation.constraints.Email
import javax.validation.constraints.Size

data class UserToCreate(
    @field:Email val email: String,
    @field:Size(min = 2) val name: String,
    @field:Size(min = 5) val password: String
)
