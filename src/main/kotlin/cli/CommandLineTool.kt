package cli

import Config.userRepoMongoDb
import domain.Email
import domain.User
import domain.UserPresenter
import domain.UserRepository
import usecases.*
import kotlin.math.absoluteValue
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextLong
import kotlin.system.exitProcess

fun main() {
    val listUsers = ListUsers(userRepoMongoDb)
    val createUser = CreateUser(userRepoMongoDb)

    repl(createUser, listUsers)
}

private tailrec fun repl(createUser: CreateUser, listUsers: ListUsers) {
    print("> ")
    runCatching {
        when (readLine()?.firstOrNull()?.toUpperCase() ?: '?') {
            'R' -> createUser(generateRandomUser())
            'I' -> createUser(generateRandomUser().copy(email = Email("invalid")))
            'L' -> listUsers().forEach(::println)

            // New UC: extend users
            'A' -> ExtendUsersDI(userRepoMongoDb)()
            'B' -> ExtendUsersIS(userRepoMongoDb)()

            // Revisit UC: list users
            // [DI]<--|... FCIS
            'M' -> ListUsers2(userRepoMongoDb, printlnPresenter)()
            //  DI ...|--> FC[IS]
            'N' -> listUsersIS()

            'Q' -> exitProcess(0)
            else -> println("please type R, I, L or Q")
        }
    }.onFailure(::println)

    repl(createUser, listUsers)
}

// UC: list users
// [DI]<--|... FCIS
private val printlnPresenter = object : UserPresenter {
    override fun show(users: List<User>) = users.forEach(::println)
}

// UC: list users
//  DI ...|--> FC[IS]
private fun listUsersIS() = userRepoMongoDb.findAll().forEach(::println)
private fun listUsersIS(repo: UserRepository, presenter: UserPresenter) = presenter.show(repo.findAll())

/////

private fun generateRandomUser() = User(
    email = Email("random+${nextInt().absoluteValue}@email.com"),
    name = "randomUser ${nextInt().absoluteValue}",
    password = nextLong().absoluteValue.toString()
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

/////

