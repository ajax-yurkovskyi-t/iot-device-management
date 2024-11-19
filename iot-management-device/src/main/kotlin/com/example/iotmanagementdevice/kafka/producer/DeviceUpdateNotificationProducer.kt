package com.example.iotmanagementdevice.kafka.producer

import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.NOTIFY
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class DeviceUpdateNotificationProducer(
    private val kafkaSender: KafkaSender<String, ByteArray>
) {

    fun sendMessage(notification: DeviceUpdateNotification): Mono<Unit> {
        return kafkaSender.send(
            SenderRecord.create(
                ProducerRecord(
                    NOTIFY,
                    notification.userId,
                    notification.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
    }
}
