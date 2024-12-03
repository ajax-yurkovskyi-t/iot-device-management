package com.example.iotmanagementdevice.device.infrastructure.kafka.producer

import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.NOTIFY
import com.example.iotmanagementdevice.device.application.port.output.UpdateDeviceNotificationMessageOutPort
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class DeviceUpdateNotificationProducer(
    private val kafkaPublisher: KafkaPublisher,
) : UpdateDeviceNotificationMessageOutPort {

    override fun sendUpdateDeviceNotificationMessage(notification: DeviceUpdateNotification): Mono<Unit> {
        return kafkaPublisher.publish(
            NOTIFY,
            notification.userId,
            notification.toByteArray()
        ).then(Unit.toMono())
    }
}
