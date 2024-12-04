package com.example.iotmanagementdevice.device.infrastructure.kafka.mapper

import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import org.springframework.stereotype.Component

@Component
class DeviceNotificationMapper {
    fun toDeviceUpdateNotification(event: DeviceUpdatedEvent): DeviceUpdateNotification {
        return DeviceUpdateNotification.newBuilder().apply {
            deviceId = event.device.id
            userId = event.device.userId
            timestamp = event.device.updatedAt
        }.build()
    }
}
