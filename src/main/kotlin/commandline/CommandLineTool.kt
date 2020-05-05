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
        val input = readLine()?.first()?.toUpperCase() ?: '?'
        when (input) {
            'R' -> createUser(
                User(
                    email = EmailAddress("random+${nextInt().absoluteValue}@email.com"),
                    name = "randomUser ${nextInt()}",
                    password = nextLong().toString()
                )
            )
            'I' -> createUser(
                User(
                    email = EmailAddress("invalid email"),
                    name = "randomUser ${nextInt()}",
                    password = nextLong().toString()
                )
            )
            'L' -> listUsers().forEach(::println)
            else -> println("please type R, I, L or Q")
        }
    } while (input != 'Q')
}
