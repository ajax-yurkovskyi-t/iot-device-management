package com.example.gateway.infrastructure.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.Test

class GatewayModuleArchitectureTest {

    @Test
    fun `module should be following valid onion architecture`() {
        onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("grpc", "..infrastructure.grpc..")
            .adapter("rest", "..infrastructure.rest..")
            .adapter("nats", "..infrastructure.nats..")
            .check(importedClasses)
    }

    companion object {
        private val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .withImportOption(ImportOption.DoNotIncludeGradleTestFixtures())
            .importPackages("com.example.gateway")
    }
}
