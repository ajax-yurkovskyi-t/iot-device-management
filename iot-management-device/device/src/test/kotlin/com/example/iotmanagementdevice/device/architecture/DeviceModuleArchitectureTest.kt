package com.example.iotmanagementdevice.device.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.Test

class DeviceModuleArchitectureTest {

    @Test
    fun `module should be following valid onion architecture`() {
        onionArchitecture()
            .withOptionalLayers(true)
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("kafka", "..infrastructure.kafka..")
            .adapter("nats", "..infrastructure.nats..")
            .adapter("redis", "..infrastructure.redis..")
            .adapter("mongo", "..infrastructure.mongo..")
            .check(importedClasses)
    }

    companion object {
        private val importedClasses: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .withImportOption(ImportOption.DoNotIncludeGradleTestFixtures())
            .importPackages("com.example.iotmanagementdevice.device")
    }
}
