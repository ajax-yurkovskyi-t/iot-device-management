package com.example.iotmanagementdevice.mapper

import DeviceFixture.updateDeviceResponse
import com.example.internal.commonmodels.DeviceUpdateNotification
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeviceNotificationMapperTest {
    private val deviceNotificationMapper = DeviceNotificationMapper()

    @Test
    fun `should map UpdateDeviceResponse to DeviceUpdateNotification`() {
        // Given
        val deviceId = ObjectId().toString()
        val userId = ObjectId().toString()
        val updateDeviceResponse = updateDeviceResponse(deviceId, userId)

        // When
        val result: DeviceUpdateNotification = deviceNotificationMapper.toDeviceUpdateNotification(updateDeviceResponse)

        // Then
        assertEquals(deviceId, result.deviceId)
        assertEquals(userId, result.userId)
    }
}
