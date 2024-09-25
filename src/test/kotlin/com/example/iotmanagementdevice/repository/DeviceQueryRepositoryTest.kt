package com.example.iotmanagementdevice.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DeviceQueryRepositoryTest : AbstractMongoTest {

    @Autowired
    private lateinit var deviceQueryRepository: DeviceQueryRepository

    @Test
    fun `should find device by id when saved`() {
        // Given
        val device = DeviceFixture.createDevice()
        deviceQueryRepository.save(device)

        // When
        val foundDevice = deviceQueryRepository.findById(device.id!!)

        // Then
        assertEquals(device, foundDevice)
    }

    @Test
    fun `should find all devices when multiple devices are saved`() {
        // Given
        val device1 = DeviceFixture.createDevice()
            .copy(name = "Device1", description = "First test device", type = "TypeB")
        val device2 = DeviceFixture.createDevice()
            .copy(name = "Device2", description = "Second test device", type = "TypeC")
        deviceQueryRepository.save(device1)
        deviceQueryRepository.save(device2)

        // When
        val devices = deviceQueryRepository.findAll()

        // Then
        val expectedDevices = listOf(device1, device2)
        assertTrue(devices.containsAll(expectedDevices), "Expected devices not found in the repository")
    }

    @Test
    fun `should not find device when deleted`() {
        // Given
        val device = DeviceFixture.createDevice()
        deviceQueryRepository.save(device)

        // When
        deviceQueryRepository.deleteById(device.id!!)
        val foundDevice = deviceQueryRepository.findById(device.id!!)

        // Then
        Assertions.assertNull(foundDevice)
    }
}
