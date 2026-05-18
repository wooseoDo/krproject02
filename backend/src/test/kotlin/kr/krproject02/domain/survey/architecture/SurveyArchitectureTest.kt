package kr.krproject02.domain.survey.architecture

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

@AnalyzeClasses(
    packages = ["kr.krproject02.domain.survey"],
    importOptions = [DoNotIncludeTests::class],
)
class SurveyArchitectureTest {
    @ArchTest
    val api_is_swagger_documentation_only: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..domain.survey.api..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..domain.survey.controller..",
                "..domain.survey.service..",
                "..domain.survey.repository..",
                "..domain.survey.entity..",
                "..domain.survey.validation..",
            )

    @ArchTest
    val service_does_not_depend_on_controller_or_api: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..domain.survey.service..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..domain.survey.controller..",
                "..domain.survey.api..",
            )

    @ArchTest
    val validation_does_not_depend_on_repository: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..domain.survey.validation..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..domain.survey.repository..")

    @ArchTest
    val entity_does_not_depend_on_outer_layers: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..domain.survey.entity..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "..domain.survey.controller..",
                "..domain.survey.api..",
                "..domain.survey.service..",
                "..domain.survey.repository..",
                "..domain.survey.validation..",
            )
}
