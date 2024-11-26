package com.example.iotmanagementdevice.kafka.consumer

import com.example.internal.NatsSubject.Device.updateByUserId
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import io.nats.client.Connection
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono

@Component
class DeviceUpdateConsumerForNats(
    private val updateDeviceForNatsKafkaReceiver: KafkaReceiver<String, ByteArray>,
    private val natsConnection: Connection,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun consumeMessages() {
        updateDeviceForNatsKafkaReceiver.receive()
            .flatMap { record ->
                Mono.defer {
                    val updatedDevice = DeviceUpdatedEvent.parser().parseFrom(record.value())
                    sendUpdate(updatedDevice)
                }
                    .onErrorResume { error ->
                        log.error("Failed to process a device update message", error)
                        Mono.empty()
                    }
                    .doFinally { record.receiverOffset().acknowledge() }
            }.subscribe()
    }

    private fun sendUpdate(event: DeviceUpdatedEvent): Mono<Unit> {
        return natsConnection.publish(
            updateByUserId(event.device.userId),
            event.toByteArray()
        ).toMono()
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeviceUpdateConsumerForNats::class.java)
    }
}
