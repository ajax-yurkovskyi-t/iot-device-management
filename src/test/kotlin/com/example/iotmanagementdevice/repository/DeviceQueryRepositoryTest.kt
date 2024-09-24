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
    fun `given a device when saved then it can be found by id`() {
        // Given
        val device = DeviceFixture.createDevice()
        deviceQueryRepository.save(device)

        // When
        val foundDevice = deviceQueryRepository.findById(device.id!!)

        // Then
        assertEquals(device, foundDevice)
    }

    @Test
    fun `given multiple devices when saved then all can be found`() {
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
        val expectedRoles = listOf(device1, device2)
        assertTrue(devices.containsAll(expectedRoles), "Expected devices not found in the repository")
    }

    @Test
    fun `given a device when deleted then it cannot be found`() {
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
