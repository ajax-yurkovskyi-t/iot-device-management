package com.example.iotmanagementdevice.kafka.producer

import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.UPDATE
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
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

    fun sendMessage(response: UpdateDeviceResponse): Mono<Unit> {
        return kafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    UPDATE,
                    response.success.device.userId,
                    response.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
    }
}
