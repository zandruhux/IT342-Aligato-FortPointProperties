package edu.cit.aligato.fortpointproperties.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;

import jakarta.persistence.Entity;

/**
 * Enforces the Controller -> Service -> Repository layering described in
 * docs/ENGINEERING_STANDARDS.md.
 *
 * <p>Rules wrapped in {@link FreezingArchRule} have pre-existing violations that are
 * recorded in src/test/resources/archunit_store. They fail only on NEW violations;
 * when you fix an existing one the store shrinks automatically. Commit the store.
 */
@AnalyzeClasses(packages = "edu.cit.aligato.fortpointproperties", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    // ---------- Layering ----------

    @ArchTest
    static final ArchRule controllersMustNotAccessRepositories = FreezingArchRule.freeze(
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..repository..")
                    .because("controllers delegate to services; services own data access"));

    @ArchTest
    static final ArchRule controllersMustNotExposeEntities = FreezingArchRule.freeze(
            noClasses().that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().areAnnotatedWith(Entity.class)
                    .because("API contracts use DTOs, never JPA entities"));

    @ArchTest
    static final ArchRule servicesMustNotDependOnControllers =
            noClasses().that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule repositoriesMustNotDependOnUpperLayers =
            noClasses().that().resideInAPackage("..repository..")
                    .should().dependOnClassesThat().resideInAnyPackage("..service..", "..controller..");

    @ArchTest
    static final ArchRule entitiesMustNotDependOnUpperLayers =
            noClasses().that().resideInAPackage("..entity..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..service..", "..controller..", "..dto..");

    // ---------- Placement & naming ----------

    @ArchTest
    static final ArchRule restControllersLiveInControllerPackages =
            classes().that().areAnnotatedWith(RestController.class)
                    .should().resideInAPackage("..controller..")
                    .andShould().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule servicesLiveInServicePackages =
            classes().that().areAnnotatedWith(Service.class)
                    .should().resideInAPackage("..service..");

    @ArchTest
    static final ArchRule repositoriesLiveInRepositoryPackages =
            classes().that().areAssignableTo(Repository.class)
                    .should().resideInAPackage("..repository..")
                    .andShould().haveSimpleNameEndingWith("Repository");

    @ArchTest
    static final ArchRule entitiesLiveInEntityPackages =
            classes().that().areAnnotatedWith(Entity.class)
                    .should().resideInAPackage("..entity..");

    @ArchTest
    static final ArchRule exceptionHandlingIsCentralised =
            classes().that().areAnnotatedWith(RestControllerAdvice.class)
                    .should().resideInAPackage("..shared.exception..");

    // ---------- General coding rules ----------

    @ArchTest
    static final ArchRule noFieldInjection = FreezingArchRule.freeze(NO_CLASSES_SHOULD_USE_FIELD_INJECTION
            .because("use constructor injection (immutable, testable)"));

    @ArchTest
    static final ArchRule noStandardStreams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static final ArchRule noJavaUtilLogging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    static final ArchRule noGenericExceptions = FreezingArchRule.freeze(NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS
            .because("throw AppException (or a specific subtype) so GlobalExceptionHandler maps it"));
}
