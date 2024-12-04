package com.example.iotmanagementdevice.device.infrastructure.kafka.mapper

import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.deviceUpdatedEvent
import com.example.iotmanagementdevice.device.infrastructure.kafka.mapper.impl.DeviceUpdateEventMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class DeviceUpdateEventMapperTest {
    private val deviceUpdateEventMapper = DeviceUpdateEventMapperImpl()

    @Test
    fun `should map device to device update event`() {
        // Given
        val device = createDevice().copy(updatedAt = Instant.now())
        val deviceUpdatedEvent = deviceUpdatedEvent(device)

        // When
        val result = deviceUpdateEventMapper.toDeviceUpdatedEvent(device)

        // Then
        assertEquals(deviceUpdatedEvent, result)
    }
}
