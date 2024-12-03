package com.example.iotmanagementdevice.device.application.port.output

import com.example.commonmodels.device.DeviceUpdateNotification
import reactor.core.publisher.Mono

interface UpdateDeviceNotificationMessageOutPort {
    fun sendUpdateDeviceNotificationMessage(notification: DeviceUpdateNotification): Mono<Unit>
}
