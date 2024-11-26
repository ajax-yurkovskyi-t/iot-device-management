package com.example.iotmanagementdevice.kafka.config

import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.UPDATE
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

@Configuration
class KafkaConsumerConfiguration(
    private val kafkaProperties: KafkaProperties,
) {

    @Bean
    fun updateDeviceKafkaReceiver(): KafkaReceiver<String, ByteArray> {
        return KafkaReceiver.create(
            createKafkaReceiverProperties(UPDATE, DEVICE_UPDATE_GROUP)
        )
    }

    @Bean
    fun updateDeviceForNatsKafkaReceiver(): KafkaReceiver<String, ByteArray> {
        return KafkaReceiver.create(
            createKafkaReceiverProperties(UPDATE, DEVICE_UPDATE_FOR_NATS_GROUP)
        )
    }

    private fun createKafkaReceiverProperties(
        topic: String,
        consumerGroup: String
    ): ReceiverOptions<String, ByteArray> {
        val properties = kafkaProperties.consumer.buildProperties(null).apply {
            putAll(
                mapOf(
                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
                    ConsumerConfig.GROUP_ID_CONFIG to consumerGroup,
                )
            )
        }
        return ReceiverOptions.create<String, ByteArray>(properties)
            .subscription(setOf(topic))
    }

    companion object {
        const val DEVICE_UPDATE_GROUP = "deviceUpdateGroup"
        const val DEVICE_UPDATE_FOR_NATS_GROUP = "deviceUpdateForNatsGroup"
    }
}
