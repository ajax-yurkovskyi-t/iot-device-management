package com.example.iotmanagementdevice.kafka

import DeviceFixture.createDevice
import DeviceFixture.createDeviceUpdateRequestDto
import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.NOTIFY
import com.example.iotmanagementdevice.repository.AbstractMongoTest
import com.example.iotmanagementdevice.service.device.DeviceService
import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaAdmin
import systems.ajax.kafka.mock.KafkaMockExtension

@Import(DeviceUpdateKafkaIT.MyKafkaTestConfiguration::class)
class DeviceUpdateKafkaIT : AbstractMongoTest {
    @Autowired
    private lateinit var deviceService: DeviceService

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun `should produce message on entity update`() {
        // GIVEN
        val updateRequestDto = createDeviceUpdateRequestDto()
        val savedDevice = reactiveMongoTemplate.insert(createDevice()).block()!!

        val result = kafkaMockExtension.listen<DeviceUpdateNotification>(
            NOTIFY,
            DeviceUpdateNotification.parser()
        )

        // WHEN
        deviceService.update(savedDevice.id.toString(), updateRequestDto).block()!!

        // THEN
        val notification = result.awaitFirst({
            it.deviceId == savedDevice.id.toString()
        })
        assertThat(notification).isNotNull
    }

    class MyKafkaTestConfiguration {
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

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
    }
}
