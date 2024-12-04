package com.example.iotmanagementdevice.device.application.port.output

import com.example.iotmanagementdevice.device.domain.Device
import reactor.core.publisher.Mono

interface UpdateDeviceMessageProducerOutPort {
    fun sendUpdateDeviceMessage(device: Device): Mono<Unit>
}
