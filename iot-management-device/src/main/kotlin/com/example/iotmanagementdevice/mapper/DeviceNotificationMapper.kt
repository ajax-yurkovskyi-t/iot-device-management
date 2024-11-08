package com.example.iotmanagementdevice.mapper

import com.example.internal.commonmodels.DeviceUpdateNotification
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.google.protobuf.Timestamp
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DeviceNotificationMapper {
    fun toDeviceUpdateNotification(response: UpdateDeviceResponse, timestampMillis: Long): DeviceUpdateNotification {
        val currentTimestamp = Instant.ofEpochMilli(timestampMillis).let {
            Timestamp.newBuilder().apply {
                seconds = it.epochSecond
                nanos = it.nano
            }.build()
        }
        return DeviceUpdateNotification.newBuilder().apply {
            deviceId = response.success.device.id
            userId = response.success.device.userId
            this.timestamp = currentTimestamp
        }.build()
    }
}
