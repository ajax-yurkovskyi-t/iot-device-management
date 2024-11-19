package com.example.iotmanagementdevice.kafka

import DeviceFixture.createDevice
import DeviceFixture.createDeviceUpdateRequestDto
import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.NOTIFY
import com.example.internal.commonmodels.DeviceUpdateNotification
import com.example.iotmanagementdevice.repository.AbstractMongoTest
import com.example.iotmanagementdevice.service.device.DeviceService
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Scope
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import java.util.*
import java.util.concurrent.TimeUnit

@Import(DeviceUpdateKafkaIT.KafkaTestConfiguration::class)
class DeviceUpdateKafkaIT : AbstractMongoTest {
    @Autowired
    private lateinit var deviceService: DeviceService

    @Autowired
    private lateinit var testConsumer: KafkaReceiver<String, ByteArray>

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun `should produce message on entity update`() {
        // GIVEN
        val updateRequestDto = createDeviceUpdateRequestDto()
        val savedDevice = reactiveMongoTemplate.insert(createDevice()).block()!!
        val receivedMessages = mutableListOf<DeviceUpdateNotification>()

        testConsumer.receive()
            .map { DeviceUpdateNotification.parseFrom(it.value()) }
            .doOnNext { receivedMessages.add(it) }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // WHEN
        deviceService.update(savedDevice.id.toString(), updateRequestDto).block()!!

        // THEN
        await()
            .atMost(15, TimeUnit.SECONDS)
            .untilAsserted {
                assertTrue(
                    receivedMessages.any { it.deviceId == savedDevice.id.toString() },
                    "Expected deviceId ${savedDevice.id} wasn't found in receivedMessages"
                )
            }
    }

    class KafkaTestConfiguration {
        @Bean
        @Scope("prototype")
        fun testConsumer(kafkaProperties: KafkaProperties): KafkaReceiver<String, ByteArray>? {
            val properties = kafkaProperties.consumer.buildProperties(null).apply {
                putAll(
                    mapOf(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
                        ConsumerConfig.GROUP_ID_CONFIG to "$CONSUMER_GROUP${UUID.randomUUID()}"
                    )
                )
            }
            val receiverOptions = ReceiverOptions.create<String, ByteArray>(properties)
                .subscription(setOf(NOTIFY))
            return KafkaReceiver.create(receiverOptions)
        }

        companion object {
            private const val CONSUMER_GROUP = "group-test"
        }
    }
}
