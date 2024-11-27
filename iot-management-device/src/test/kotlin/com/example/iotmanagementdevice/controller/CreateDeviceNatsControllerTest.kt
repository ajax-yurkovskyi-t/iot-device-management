package com.example.iotmanagementdevice.controller

import DeviceFixture.createDeviceRequest
import DeviceFixture.createDeviceResponseDto
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.mapper.CreateDeviceMapper
import com.example.iotmanagementdevice.repository.AbstractMongoTest
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
        val deviceResponseDto = createDeviceResponseDto().copy(name = "ProtoDevice")

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.CREATE,
            createDeviceRequest(),
            CreateDeviceResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(createDeviceMapper.toCreateDeviceResponse(deviceResponseDto))
            .verifyComplete()
    }
}
