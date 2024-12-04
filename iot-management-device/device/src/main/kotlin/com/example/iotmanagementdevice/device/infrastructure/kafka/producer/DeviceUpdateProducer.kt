package com.example.iotmanagementdevice.device.infrastructure.kafka.producer

import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.UPDATE
import com.example.iotmanagementdevice.device.application.port.output.UpdateDeviceMessageProducerOutPort
import com.example.iotmanagementdevice.device.domain.Device
import com.example.iotmanagementdevice.device.infrastructure.kafka.mapper.DeviceUpdateEventMapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class DeviceUpdateProducer(
    private val kafkaPublisher: KafkaPublisher,
    private val deviceUpdateEventMapper: DeviceUpdateEventMapper,
) : UpdateDeviceMessageProducerOutPort {

    override fun sendUpdateDeviceMessage(device: Device): Mono<Unit> {
        val event = deviceUpdateEventMapper.toDeviceUpdatedEvent(device)
        return kafkaPublisher.publish(
            UPDATE,
            event.device.userId,
            event.toByteArray()
        ).then(Unit.toMono())
    }
}
