package com.example.iotmanagementdevice.kafka

import DeviceFixture.createDevice
import DeviceFixture.createDeviceUpdateRequestDto
import com.example.commonmodels.device.DeviceUpdateNotification
import com.example.internal.KafkaTopic.KafkaDeviceUpdateEvents.NOTIFY
import com.example.iotmanagementdevice.repository.AbstractMongoTest
import com.example.iotmanagementdevice.service.device.DeviceService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import systems.ajax.kafka.mock.KafkaMockExtension

@Import(KafkaTestConfiguration::class)
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

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
    }
}
