package com.example.iotmanagementdevice.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DeviceRepositoryImplTest : AbstractMongoTest {

    @Autowired
    private lateinit var deviceRepositoryImpl: DeviceRepositoryImpl

    @Test
    fun `should find device by id when saved`() {
        // Given
        val device = DeviceFixture.createDevice()
        deviceRepositoryImpl.save(device)

        // When
        val foundDevice = deviceRepositoryImpl.findById(device.id!!.toString())

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
        deviceRepositoryImpl.save(device1)
        deviceRepositoryImpl.save(device2)

        // When
        val devices = deviceRepositoryImpl.findAll()

        // Then
        val expectedDevices = listOf(device1, device2)
        assertTrue(devices.containsAll(expectedDevices), "Expected devices not found in the repository")
    }

    @Test
    fun `should not find device when deleted`() {
        // Given
        val device = DeviceFixture.createDevice()
        deviceRepositoryImpl.save(device)

        // When
        deviceRepositoryImpl.deleteById(device.id!!.toString())
        val foundDevice = deviceRepositoryImpl.findById(device.id!!.toString())

        // Then
        Assertions.assertNull(foundDevice)
    }
}
