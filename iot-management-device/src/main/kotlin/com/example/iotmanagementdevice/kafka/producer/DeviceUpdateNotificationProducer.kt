package com.example.iotmanagementdevice.kafka.producer

import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.NOTIFY
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class DeviceUpdateNotificationProducer(
    private val kafkaPublisher: KafkaPublisher,
) {

    fun sendMessage(notification: DeviceUpdateNotification): Mono<Unit> {
        return kafkaPublisher.publish(
            NOTIFY,
            notification.userId,
            notification.toByteArray()
        ).then(Unit.toMono())
    }
}
