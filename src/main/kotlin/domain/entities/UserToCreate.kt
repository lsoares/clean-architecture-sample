package domain.entities

data class UserToCreate(val email: String, val name: String, val password: String)
// TODO: validation rules on creation