package com.youngjun.cleanarchitecture;

import org.junit.jupiter.api.Test;

public class DependencyRuleTests {

    @Test
    void validateRegistrationContextArchitecture() {
        HexagonalArchitecture.boundedContext("io.reflectoring.buckpal.account")

                .withDomainLayer("domain")

                .withAdaptersLayer("adapter")
                .incoming("in.web")
                .outgoing("out.persistence")
                .and()

                .withApplicationLayer("application")
                .services("service")
                .incomingPorts("port.in")
                .outgoingPorts("port.out")
                .and()

                .withConfiguration("configuration")
                .check(new ClassFileImporter()
                        .importPackages("io.reflectoring.buckpal.."));
    }

    @Test
    void domainLayerDoesNotDependOnApplicationLayer() {
        noClasses()
            .that()
            .resideInAPackage("com.youngjun.cleanarchitecture.domain")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("com.youngjun.cleanarchitecture.application..")
            .check(new ClassFileImporter()
                .importPackages("com.youngjun.cleanarchitecture.."));
    }
}
