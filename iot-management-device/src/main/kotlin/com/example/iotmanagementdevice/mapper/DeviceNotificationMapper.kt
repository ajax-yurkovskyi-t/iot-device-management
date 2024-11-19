package com.example.iotmanagementdevice.mapper

import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import org.springframework.stereotype.Component

@Component
class DeviceNotificationMapper {
    fun toDeviceUpdateNotification(response: UpdateDeviceResponse): DeviceUpdateNotification {
        return DeviceUpdateNotification.newBuilder().apply {
            deviceId = response.success.device.id
            userId = response.success.device.userId
            timestamp = response.success.device.updatedAt
        }.build()
    }
}
