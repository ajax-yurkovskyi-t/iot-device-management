package com.example.iotmanagementdevice.device.infrastructure.kafka.mapper

import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.deviceUpdatedEvent
import com.example.iotmanagementdevice.device.DeviceFixture.updateDeviceNotification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class DeviceNotificationMapperTest {
    private val deviceNotificationMapper = DeviceNotificationMapper()

    @Test
    fun `should map UpdateDeviceResponse to DeviceUpdateNotification`() {
        // Given
        val device = createDevice().copy(updatedAt = Instant.now())
        val deviceUpdatedEvent = deviceUpdatedEvent(device)
        val updateDeviceNotification = updateDeviceNotification(device)

        // When
        val result: DeviceUpdateNotification =
            deviceNotificationMapper.toDeviceUpdateNotification(deviceUpdatedEvent)

        // Then
        assertEquals(updateDeviceNotification, result)
    }
}
