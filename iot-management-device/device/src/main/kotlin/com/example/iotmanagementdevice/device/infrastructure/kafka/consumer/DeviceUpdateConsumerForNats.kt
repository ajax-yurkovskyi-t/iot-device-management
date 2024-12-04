package com.example.iotmanagementdevice.device.infrastructure.kafka.consumer

import com.example.internal.KafkaTopic
import com.example.internal.NatsSubject.Device.updateByUserId
import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Component
class DeviceUpdateConsumerForNats(
    private val publisher: NatsMessagePublisher,
) : KafkaHandler<DeviceUpdatedEvent, TopicSingle> {

    override val groupId: String = DEVICE_UPDATE_FOR_NATS_GROUP

    override val parser: Parser<DeviceUpdatedEvent> = DeviceUpdatedEvent.parser()

    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.KafkaDeviceUpdateEvents.UPDATE)

    override fun handle(kafkaEvent: KafkaEvent<DeviceUpdatedEvent>): Mono<Unit> {
        return publisher.publish(
            updateByUserId(kafkaEvent.data.device.userId),
            kafkaEvent.data
        ).doFinally { kafkaEvent.ack() }
    }

    companion object {
        const val DEVICE_UPDATE_FOR_NATS_GROUP = "deviceUpdateForNatsGroup"
    }
}
