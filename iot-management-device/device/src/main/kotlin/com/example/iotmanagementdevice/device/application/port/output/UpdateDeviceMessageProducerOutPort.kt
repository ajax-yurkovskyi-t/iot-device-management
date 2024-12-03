package com.example.iotmanagementdevice.device.application.port.output

import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import reactor.core.publisher.Mono

interface UpdateDeviceMessageProducerOutPort {
    fun sendUpdateDeviceMessage(updatedDevice: DeviceUpdatedEvent): Mono<Unit>
}
