package com.example.iotmanagementdevice.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class MongoDeviceRepositoryTest : AbstractMongoTest {

    @Autowired
    private lateinit var mongoDeviceRepository: MongoDeviceRepository

    @Test
    fun `should find device by id when saved`() {
        // Given
        val device = DeviceFixture.createDevice()
        mongoDeviceRepository.save(device).block()

        // When
        val foundDevice = mongoDeviceRepository.findById(device.id!!.toString())

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
        mongoDeviceRepository.save(device1).block()
        mongoDeviceRepository.save(device2).block()

        // When
        val devices = mongoDeviceRepository.findAll().collectList()

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
        mongoDeviceRepository.save(device).block()

        // When
        mongoDeviceRepository.deleteById(device.id!!.toString()).block()

        // Then
        mongoDeviceRepository.findById(device.id!!.toString())
            .test()
            .verifyComplete()
    }
}
