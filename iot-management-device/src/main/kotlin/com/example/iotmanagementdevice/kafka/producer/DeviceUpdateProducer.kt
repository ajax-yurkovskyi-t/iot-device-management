package com.example.iotmanagementdevice.kafka.producer

import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.UPDATE
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class DeviceUpdateProducer(
    private val kafkaSender: KafkaSender<String, ByteArray>,
) {

    fun sendMessage(event: DeviceUpdatedEvent): Mono<Unit> {
        return kafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    UPDATE,
                    event.device.userId,
                    event.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
    }
}
