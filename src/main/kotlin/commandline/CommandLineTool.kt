package commandline

import domain.EmailAddress
import domain.User
import usecases.CreateUser
import usecases.ListUsers
import kotlin.math.absoluteValue
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong

fun main() {
//    val listUsers = ListUsers(Config.userRepoMongoDb)
//    val createUser = CreateUser(Config.userRepoMongoDb)
    val listUsers = ListUsers(Config.userRepoMySql)
    val createUser = CreateUser(Config.userRepoMySql)

    do {
        print("> ")
        val input = readLine()?.firstOrNull()?.toUpperCase() ?: '?'
        when (input) {
            'R' -> createUser(generateRandomUser())
            'I' -> createUser(generateRandomUser().copy(email = EmailAddress("invalid")))
            'L' -> listUsers().forEach(::println)
            else -> println("please type R, I, L or Q")
        }
    } while (input != 'Q')
}

private fun generateRandomUser() = User(
    email = EmailAddress("random+${nextInt().absoluteValue}@email.com"),
    name = "randomUser ${nextInt().absoluteValue}",
    password = nextLong().absoluteValue.toString()
)
