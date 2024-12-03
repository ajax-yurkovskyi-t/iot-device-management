package com.example.iotmanagementdevice.device.infrastructure.mongo

import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.infrastructure.mongo.repository.MongoDeviceRepository
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class MongoDeviceRepositoryTest : AbstractMongoTest {

    @Autowired
    private lateinit var deviceRepository: MongoDeviceRepository

    @Test
    fun `should find device by id when saved`() {
        // Given
        val device = createDevice()
        deviceRepository.save(device).block()

        // When
        val foundDevice = deviceRepository.findById(device.id!!.toString())

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
        val device1 = createDevice()
            .copy(name = "Device1", description = "First test device", type = "TypeB")
        val device2 = createDevice()
            .copy(name = "Device2", description = "Second test device", type = "TypeC")
        deviceRepository.save(device1).block()
        deviceRepository.save(device2).block()

        // When
        val devices = deviceRepository.findAll().collectList()

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
        val device = createDevice()
        deviceRepository.save(device).block()

        // When
        deviceRepository.deleteById(device.id!!.toString()).block()

        // Then
        deviceRepository.findById(device.id!!.toString())
            .test()
            .verifyComplete()
    }
}
