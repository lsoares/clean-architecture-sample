import org.jetbrains.exposed.dao.IntIdTable

object Users : IntIdTable() {
    val email = varchar("email", 50).uniqueIndex()
    val name = varchar("name", 50)
    val password = varchar("password", 50)
}