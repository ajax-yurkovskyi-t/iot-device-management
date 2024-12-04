package com.example.iotmanagementdevice.device.infrastructure.mongo.mapper

import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.createMongoDevice
import com.example.iotmanagementdevice.device.infrastructure.mongo.mapper.impl.DeviceMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeviceMapperTest {
    private val deviceMapper = DeviceMapperImpl()

    @Test
    fun `should map mongo device to device`() {
        // Given
        val device = createDevice()
        val mongoDevice = createMongoDevice(device)

        // When
        val result = deviceMapper.toDomain(mongoDevice)

        // Then
        assertEquals(device, result)
    }

    @Test
    fun `should map device to mongo device`() {
        // Given
        val device = createDevice()
        val mongoDevice = createMongoDevice(device)

        // When
        val result = deviceMapper.toEntity(device)

        // Then
        assertEquals(mongoDevice, result)
    }
}
