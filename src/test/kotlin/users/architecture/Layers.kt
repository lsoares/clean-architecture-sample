package users.architecture

import com.tngtech.archunit.base.DescribedPredicate.alwaysTrue
import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameMatching
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.lang.conditions.ArchPredicates.are
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import users.web.WebApp

@Disabled
object Layers {
    private val classes = ClassFileImporter()
        .withImportOption(DoNotIncludeTests())
        .importPackages("users")

    @Test
    fun `layered architecture`() {
        layeredArchitecture()
            .layer("web handlers").definedBy("..web")
            .layer("use cases").definedBy("..usecases")
            .layer("persistence").definedBy("..persistence")
            .whereLayer("web handlers").mayNotBeAccessedByAnyLayer()
            .whereLayer("use cases").mayOnlyBeAccessedByLayers("web handlers")
            .whereLayer("persistence").mayOnlyBeAccessedByLayers("use cases")
            .ignoreDependency(
                nameMatching(".*\\.AppMainKt").or(
                    nameMatching(".*\\.WebAppConfig\\$.*")
                ), alwaysTrue()
            )
            .check(classes)
    }

    @Test
    fun `Javalin is only used by web layer`() {
        noClasses()
            .that().resideOutsideOfPackage("..web.handlers")
            .should().accessClassesThat().resideInAnyPackage("io.javalin..")
            .because("Javalin is to be used by the web adapter")
            .check(classes.that(are(not(belongToAnyOf(WebApp::class.java)))))
    }
}