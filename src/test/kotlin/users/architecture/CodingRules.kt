package users.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS
import com.tngtech.archunit.library.GeneralCodingRules.THROW_GENERIC_EXCEPTIONS
import org.junit.jupiter.api.Test

object CodingRules {

    private val classes = ClassFileImporter().importPackages("users")

    @Test
    fun `no System out println`() {
        noClasses()
            .should(ACCESS_STANDARD_STREAMS)
            .because("they should use a logger or a monitoring service")
            .check(classes)
    }

    @Test
    fun `no generic exceptions`() {
        noClasses()
            .should(THROW_GENERIC_EXCEPTIONS)
            .because("exceptions should be specific")
            .check(classes)
    }
}