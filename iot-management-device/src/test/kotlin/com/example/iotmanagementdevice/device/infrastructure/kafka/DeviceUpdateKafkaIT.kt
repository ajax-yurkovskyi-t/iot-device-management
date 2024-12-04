package com.example.iotmanagementdevice.device.infrastructure.kafka

import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.NOTIFY
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.application.port.input.DeviceServiceInPort
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import com.example.iotmanagementdevice.utils.KafkaTestConfiguration
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import systems.ajax.kafka.mock.KafkaMockExtension

@Import(KafkaTestConfiguration::class)
class DeviceUpdateKafkaIT : AbstractMongoTest {
    @Autowired
    private lateinit var deviceService: DeviceServiceInPort

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Test
    fun `should produce message on entity update`() {
        // GIVEN
        val device = createDevice()
        val savedDevice = reactiveMongoTemplate.insert(createDevice()).block()!!

        val result = kafkaMockExtension.listen<DeviceUpdateNotification>(
            NOTIFY,
            DeviceUpdateNotification.parser()
        )

        // WHEN
        deviceService.update(savedDevice.id.toString(), device).block()!!

        // THEN
        val notification = result.awaitFirst({
            it.deviceId == savedDevice.id.toString()
        })
        assertNotNull(
            notification,
            "Expected a DeviceUpdateNotification with deviceId ${savedDevice.id} not to be null"
        )
    }

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
    }
}
