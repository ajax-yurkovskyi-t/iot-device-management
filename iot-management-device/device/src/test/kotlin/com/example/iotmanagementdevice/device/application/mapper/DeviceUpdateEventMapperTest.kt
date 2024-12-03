package com.example.iotmanagementdevice.device.application.mapper

import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.deviceUpdatedEvent
import com.example.iotmanagementdevice.device.application.mapper.impl.DeviceUpdateEventMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class DeviceUpdateEventMapperTest {
    private val deviceUpdateEventMapper = DeviceUpdateEventMapperImpl()

    @Test
    fun `should map device to device update event`() {
        val device = createDevice().copy(updatedAt = Instant.now())

        val result = deviceUpdateEventMapper.toDeviceUpdatedEvent(device)

        val deviceUpdatedEvent = deviceUpdatedEvent(device)

        assertEquals(deviceUpdatedEvent, result)
    }
}
