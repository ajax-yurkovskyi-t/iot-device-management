package com.example.iotmanagementdevice.device.infrastructure.kafka.consumer

import com.example.internal.KafkaTopic
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.example.iotmanagementdevice.device.application.port.output.UpdateDeviceNotificationMessageOutPort
import com.example.iotmanagementdevice.device.infrastructure.kafka.mapper.DeviceNotificationMapper
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle

@Component
class DeviceUpdateProcessor(
    private val updateDeviceNotificationMessageOutPort: UpdateDeviceNotificationMessageOutPort,
    private val deviceNotificationMapper: DeviceNotificationMapper,
) : KafkaHandler<DeviceUpdatedEvent, TopicSingle> {

    override val groupId: String = DEVICE_UPDATE_GROUP

    override val parser: Parser<DeviceUpdatedEvent> = DeviceUpdatedEvent.parser()

    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.KafkaDeviceUpdateEvents.UPDATE)

    override fun handle(kafkaEvent: KafkaEvent<DeviceUpdatedEvent>): Mono<Unit> {
        val notification = deviceNotificationMapper.toDeviceUpdateNotification(kafkaEvent.data)
        return updateDeviceNotificationMessageOutPort.sendUpdateDeviceNotificationMessage(notification)
            .doFinally { kafkaEvent.ack() }
    }

    companion object {
        const val DEVICE_UPDATE_GROUP = "deviceUpdateGroup"
    }
}
