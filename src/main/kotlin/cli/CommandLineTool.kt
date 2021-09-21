package cli

import Config
import domain.model.User
import domain.model.toEmail
import domain.model.toPassword
import domain.model.toUserId
import java.util.*
import kotlin.math.absoluteValue
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong
import kotlin.system.exitProcess

fun main() {
    ConfigWithMySql.repl()
}

private tailrec fun Config.repl() {
    print("> ")
    runCatching {
        when (readLine()?.firstOrNull()?.uppercase() ?: '?') {
            'R' -> this@repl.createUser(generateRandomUser())
            'I' -> this@repl.createUser(generateRandomUser().copy(email = "invalid".toEmail()))
            'L' -> this@repl.listUsers().forEach(::println)
            'Q' -> exitProcess(0)
            else -> println("please type R, I, L or Q")
        }
    }.onFailure(::println)

    repl()
}

private fun generateRandomUser() = User(
    id = UUID.randomUUID().toString().toUserId(),
    email = "random+${nextInt().absoluteValue}@email.com".toEmail(),
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
