package com.example.iotmanagementdevice.mapper

import DeviceFixture.updateDeviceNotification
import DeviceFixture.updateDeviceResponse
import com.example.commonmodels.device.DeviceUpdateNotification
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class DeviceNotificationMapperTest {
    private val deviceNotificationMapper = DeviceNotificationMapper()

    @Test
    fun `should map UpdateDeviceResponse to DeviceUpdateNotification`() {
        // Given
        val deviceId = ObjectId().toString()
        val userId = ObjectId().toString()
        val timestamp = Instant.now()
        val updateDeviceResponse = updateDeviceResponse(deviceId, userId, timestamp)
        val updateDeviceNotification = updateDeviceNotification(deviceId, userId, timestamp)

        // When
        val result: DeviceUpdateNotification =
            deviceNotificationMapper.toDeviceUpdateNotification(updateDeviceResponse)

        // Then
        assertEquals(updateDeviceNotification, result)
    }
}
