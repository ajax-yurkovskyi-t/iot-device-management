package com.example.iotmanagementdevice.utils

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaAdmin

class KafkaTestConfiguration {
    @Bean
    fun adminClient(kafkaAdmin: KafkaAdmin): Admin =
        KafkaAdminClient.create(kafkaAdmin.configurationProperties)

    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, ByteArray> =
        DefaultKafkaConsumerFactory(
            kafkaProperties.buildConsumerProperties(null),
            StringDeserializer(),
            ByteArrayDeserializer()
        )
}
