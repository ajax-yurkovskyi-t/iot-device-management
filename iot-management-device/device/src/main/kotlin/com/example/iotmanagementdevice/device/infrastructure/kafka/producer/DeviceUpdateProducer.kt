package com.example.iotmanagementdevice.device.infrastructure.kafka.producer

import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.UPDATE
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.example.iotmanagementdevice.device.application.port.output.UpdateDeviceMessageProducerOutPort
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class DeviceUpdateProducer(
    private val kafkaPublisher: KafkaPublisher,
) : UpdateDeviceMessageProducerOutPort {

    override fun sendUpdateDeviceMessage(event: DeviceUpdatedEvent): Mono<Unit> {
        return kafkaPublisher.publish(
            UPDATE,
            event.device.userId,
            event.toByteArray()
        ).then(Unit.toMono())
    }
}
