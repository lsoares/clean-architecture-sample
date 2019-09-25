package createuser

class UseCase(private val repository: Repository, val passwordEncoder: PasswordEncoder) {

    fun createUser(user: User) {
        repository.createUser(user.copy(password = passwordEncoder.encode(user.password)))
    }

    class UserAlreadyExists : Exception()
}
