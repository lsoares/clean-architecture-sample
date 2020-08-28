package cli

import domain.model.Email
import domain.model.User
import domain.model.toPassword
import domain.usecases.CreateUser
import domain.usecases.ListUsers
import kotlin.math.absoluteValue
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong
import kotlin.system.exitProcess

fun main() {
    repl(Config.createUser, Config.listUsers)
}

private tailrec fun repl(createUser: CreateUser, listUsers: ListUsers) {
    print("> ")
    runCatching {
        when (readLine()?.firstOrNull()?.toUpperCase() ?: '?') {
            'R' -> createUser(generateRandomUser())
            'I' -> createUser(generateRandomUser().copy(email = Email("invalid")))
            'L' -> listUsers().forEach(::println)
            'Q' -> exitProcess(0)
            else -> println("please type R, I, L or Q")
        }
    }.onFailure(::println)

    repl(createUser, listUsers)
}

private fun generateRandomUser() = User(
    email = Email("random+${nextInt().absoluteValue}@email.com"),
    name = "randomUser ${nextInt().absoluteValue}",
    password = nextLong().absoluteValue.toString().toPassword()
)

/*
    MYSQL_URL=jdbc:mysql://root:my-secret-pw@localhost:3306/clean_demo
    docker stop mysql_demo && docker rm mysql_demo
    docker run --name mysql_demo -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=clean_demo -p 3306:3306 -d mysql
 */

/*
    MONGODB_HOST=localhost
    MONGODB_PORT=27017
    docker stop mongodb_demo && docker rm mongodb_demo
    docker run --rm --name mongodb_demo -p 27017:27017 mongo
*/
