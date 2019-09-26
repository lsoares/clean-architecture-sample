package createuser

class UseCase(private val repository: Repository, private val passwordEncoder: PasswordEncoder) {

    fun createUser(user: User) {
        repository.createUser(
                user.copy(password = passwordEncoder.encode(user.password))
        )
    }
}
