package com.example.iotmanagementdevice.device.infrastructure.nats

import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.device.DeviceFixture.createDeviceRequest
import com.example.iotmanagementdevice.device.DeviceFixture.createDeviceWithoutId
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.CreateDeviceMapper
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class CreateDeviceNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var createDeviceMapper: CreateDeviceMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should return saved device`() {
        // GIVEN
        val device = createDeviceWithoutId().copy(name = "ProtoDevice", userId = null)

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.CREATE,
            createDeviceRequest(),
            CreateDeviceResponse.parser()
        )

        // THEN
        actual.test()
            .assertNext { response ->
                val expected = createDeviceMapper.toCreateDeviceResponse(
                    device.copy(id = response.success.device.id)
                )
                assertThat(expected).isEqualTo(response)
            }
            .verifyComplete()
    }
}
