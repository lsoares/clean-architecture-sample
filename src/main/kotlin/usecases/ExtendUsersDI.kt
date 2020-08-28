package usecases

import domain.UserRepository
import java.time.LocalDate.now

class ExtendUsersDI(private val userRepo: UserRepository) {

    operator fun invoke() =
        userRepo
            .findAll()
            .filter { it.validUntil < now() }
            .map { it.copy(validUntil = now().plusDays(1)) }
            .forEach(userRepo::save)
}
