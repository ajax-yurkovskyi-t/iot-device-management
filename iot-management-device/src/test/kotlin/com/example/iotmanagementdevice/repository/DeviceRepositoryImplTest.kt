package com.example.iotmanagementdevice.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class DeviceRepositoryImplTest : AbstractMongoTest {

    @Autowired
    private lateinit var deviceRepositoryImpl: DeviceRepositoryImpl

    @Test
    fun `should find device by id when saved`() {
        // Given
        val device = DeviceFixture.createDevice()
        deviceRepositoryImpl.save(device).block()

        // When
        val foundDevice = deviceRepositoryImpl.findById(device.id!!.toString())

        // Then
        foundDevice.test()
            .assertNext { found ->
                assertThat(found).usingRecursiveComparison()
                    .ignoringFields("updatedAt")
                    .isEqualTo(device)
            }
            .verifyComplete()
    }

    @Test
    fun `should find all devices when multiple devices are saved`() {
        // Given
        val device1 = DeviceFixture.createDevice()
            .copy(name = "Device1", description = "First test device", type = "TypeB")
        val device2 = DeviceFixture.createDevice()
            .copy(name = "Device2", description = "Second test device", type = "TypeC")
        deviceRepositoryImpl.save(device1).block()
        deviceRepositoryImpl.save(device2).block()

        // When
        val devices = deviceRepositoryImpl.findAll().collectList()

        // Then
        devices.test()
            .assertNext { foundDevices ->
                assertThat(foundDevices)
                    .usingRecursiveFieldByFieldElementComparatorIgnoringFields("updatedAt")
                    .containsAll(listOf(device1, device2))
            }
            .verifyComplete()
    }

    @Test
    fun `should not find device when deleted`() {
        // Given
        val device = DeviceFixture.createDevice()
        deviceRepositoryImpl.save(device).block()

        // When
        deviceRepositoryImpl.deleteById(device.id!!.toString()).block()

        // Then
        deviceRepositoryImpl.findById(device.id!!.toString())
            .test()
            .verifyComplete()
    }
}
