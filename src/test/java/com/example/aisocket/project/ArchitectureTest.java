package com.example.aisocket.project;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.example.aisocket.project",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

    private static final String DOMAIN = "..project.domain..";
    private static final String APPLICATION = "..project.application..";
    private static final String ADAPTER = "..project.adapter..";
    private static final String ADAPTER_IN = "..project.adapter.in..";
    private static final String ADAPTER_OUT = "..project.adapter.out..";

    @ArchTest
    static final ArchRule domain_should_not_depend_on_application_or_adapter =
            noClasses()
                    .that().resideInAPackage(DOMAIN)
                    .should().dependOnClassesThat().resideInAnyPackage(APPLICATION, ADAPTER);

    @ArchTest
    static final ArchRule application_should_not_depend_on_adapters =
            noClasses()
                    .that().resideInAPackage(APPLICATION)
                    .should().dependOnClassesThat().resideInAnyPackage(ADAPTER);

    @ArchTest
    static final ArchRule inbound_adapters_should_not_depend_on_outbound_adapters =
            noClasses()
                    .that().resideInAPackage(ADAPTER_IN)
                    .should().dependOnClassesThat().resideInAnyPackage(ADAPTER_OUT);

    @ArchTest
    static final ArchRule outbound_adapters_should_not_depend_on_inbound_adapters =
            noClasses()
                    .that().resideInAPackage(ADAPTER_OUT)
                    .should().dependOnClassesThat().resideInAnyPackage(ADAPTER_IN);

    @ArchTest
    static void project_layers_follow_hexagonal_dependency_direction(JavaClasses classes) {
        com.tngtech.archunit.library.Architectures.layeredArchitecture()
                .consideringAllDependencies()
                .layer("Domain").definedBy(DOMAIN)
                .layer("Application").definedBy(APPLICATION)
                .layer("InboundAdapter").definedBy(ADAPTER_IN)
                .layer("OutboundAdapter").definedBy(ADAPTER_OUT)
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "InboundAdapter", "OutboundAdapter")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("InboundAdapter", "OutboundAdapter")
                .whereLayer("InboundAdapter").mayNotBeAccessedByAnyLayer()
                .whereLayer("OutboundAdapter").mayNotBeAccessedByAnyLayer()
                .check(classes);
    }
}
