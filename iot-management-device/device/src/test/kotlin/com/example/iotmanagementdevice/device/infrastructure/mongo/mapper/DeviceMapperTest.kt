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
        val device = createDevice()
        val mongoDevice = createMongoDevice(device)

        val result = deviceMapper.toDomain(mongoDevice)

        assertEquals(device, result)
    }

    @Test
    fun `should map device to mongo device`() {
        val device = createDevice()
        val mongoDevice = createMongoDevice(device)

        val result = deviceMapper.toEntity(device)

        assertEquals(mongoDevice, result)
    }
}
