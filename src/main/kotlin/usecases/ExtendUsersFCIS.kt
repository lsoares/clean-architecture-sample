package usecases

import domain.User
import domain.UserRepository
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.Month.JANUARY

/**
 * See for more info on FCIS:
 * - 34 min talk: https://www.destroyallsoftware.com/talks/boundaries
 * - 5 min summary: https://marsbased.com/blog/2020/01/20/functional-core-imperative-shell/
 */

// Functional Core
object ExtendUsersFC {

    // “The ‘value’ is the boundary”
    operator fun invoke(users: List<User>): List<User> =
        users
            .filter { it.validUntil < now() }
            .map { it.copy(validUntil = now().plusDays(1)) }
}

// Imperative Shell
class ExtendUsersIS(private val userRepo: UserRepository) {

    operator fun invoke() {
        val users = userRepo.findAll()
        val extended = ExtendUsersFC(users)
        extended.forEach { userRepo.save(it) }
    }
}

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * Exploring further into using “values” and “functions” as the interface.
 * */

object ExtendUsersFC2 {
    operator fun invoke(users: List<User>, condition: (LocalDate) -> Boolean): List<User> =
        users
            .filter { condition(it.validUntil) }
            .map { it.copy(validUntil = now().plusDays(1)) }
}

fun isExpired(date: LocalDate) = date < now()
fun isJanuary(date: LocalDate) = date.month == JANUARY

val aGoodInvocation = ExtendUsersFC2(emptyList(), ::isExpired)
val aBadInvocation = ExtendUsersFC2(emptyList(), ::isJanuary) // not really what we wanted!

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * Considering the shallower surface of a function definition,
 * we could say that using generic types is akin to overly generic interfaces.
 *
 * For a “VERY CONVOLUTED” example... :D
 */

interface Doer { fun doit(): String }
object Runner : Doer { override fun doit() = "runs"}
object Walker : Doer { override fun doit() = "walks"}
object Swimmer : Doer { override fun doit() = "swims"}

fun move(pedestrian: Doer) = pedestrian.doit()

val bGoodInvocation = move(Runner)
val bBadInvocation = move(Swimmer) // not really what we wanted!

// A better design would communicate intention by creating new types with clearer purpose.

interface Pedestrian { fun move(): String }
object Runner2 : Pedestrian { override fun move() = "runs"}
object Walker2 : Pedestrian { override fun move() = "walks"}

interface Aquatician { fun glide(): String } // don’t think this work exists :D
object Swimmer2 : Aquatician { override fun glide() = "swims"}

fun move2(pedestrian: Pedestrian) = pedestrian.move()

val cGoodInvocation = move2(Runner2)
// val cImpossibleInvocation = move2(Swimmer2)

//////////////////////////////////////////////////////////////////////////////////////////

/**
 * Similarly, we could communicate intention by creating new types with clearer purpose
 * in the function type definition.
 *
 * For a “TAKING THIS TO THE EXTREME” example...
 */

object ExtendUsersFC3 {
    operator fun invoke(users: List<User>, condition: (ExpirationDate) -> IsExpired): List<ExtendedUser> =
        users
            // Not going to refactor User but user.validUntil would be an ExpirationDate
            .filter { condition(ExpirationDate(it.validUntil)).value }
            .map { ExtendedUser(it.copy(validUntil = now().plusDays(1))) }
}

data class ExpirationDate(val value: LocalDate)
data class IsExpired(val value: Boolean)
data class ExtendedUser(val value: User)

fun isExpired2(date: ExpirationDate) = IsExpired(date.value < now())
fun isJanuary2(date: LocalDate) = date.month == JANUARY

val dGoodInvocation = ExtendUsersFC3(emptyList(), ::isExpired2)
// val dImpossibleInvocation = ExtendUsersFC3(emptyList(), ::isJanuary2)

// Right now, all this wrapping and unwrapping is quite exaggerated.
// This would be much nicer if a real `typedef` existed (instead of only `typealias`):
//
//      typedef UserId = String
//      typedef BookId = String
//      val foo: UserId = "abc"
//      val bar: BookId = "abc"
//      foo == bar  /* Error: type mismatch! */
//