package com.example.iotmanagementdevice.kafka.consumer

import com.example.internal.commonmodels.DeviceUpdateNotification
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.kafka.producer.DeviceUpdateNotificationProducer
import com.example.iotmanagementdevice.mapper.DeviceNotificationMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver

@Component
class DeviceUpdateProcessor(
    private val updateDeviceKafkaReceiver: KafkaReceiver<String, ByteArray>,
    private val notificationProducer: DeviceUpdateNotificationProducer,
    private val deviceNotificationMapper: DeviceNotificationMapper,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun consumeMessages() {
        updateDeviceKafkaReceiver.receive()
            .flatMap { record ->
                val updatedDevice = UpdateDeviceResponse.parser().parseFrom(record.value())
                val notification: DeviceUpdateNotification =
                    deviceNotificationMapper.toDeviceUpdateNotification(updatedDevice, record.timestamp())
                sendNotification(notification)
                    .doFinally { record.receiverOffset().acknowledge() }
            }
            .subscribe()
    }

    private fun sendNotification(notification: DeviceUpdateNotification): Mono<Unit> =
        notificationProducer.sendMessage(notification)
            .doOnError { error ->
                log.error(
                    "Failed to send device update notification {}",
                    notification,
                    error
                )
            }
            .thenReturn(Unit)

    companion object {
        private val log = LoggerFactory.getLogger(DeviceUpdateProcessor::class.java)
    }
}