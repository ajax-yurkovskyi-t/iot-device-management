package com.example.iotmanagementdevice.mapper

import DeviceFixture.updateDeviceNotification
import DeviceFixture.updateDeviceResponse
import com.example.internal.commonmodels.DeviceUpdateNotification
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
        val timestampMillis = Instant.now().toEpochMilli()
        val updateDeviceResponse = updateDeviceResponse(deviceId, userId)
        val updateDeviceNotification = updateDeviceNotification(deviceId, userId, Instant.ofEpochMilli(timestampMillis))

        // When
        val result: DeviceUpdateNotification =
            deviceNotificationMapper.toDeviceUpdateNotification(updateDeviceResponse, timestampMillis)

        // Then
        assertEquals(updateDeviceNotification, result)
    }
}
