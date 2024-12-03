package com.example.iotmanagementdevice.device.infrastructure.nats

import com.example.core.exception.EntityNotFoundException
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.updateDeviceRequest
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.UpdateDeviceMapper
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class UpdateDeviceNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var deviceRepository: DeviseRepositoryOutPort

    @Autowired
    private lateinit var updateDeviceMapper: UpdateDeviceMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should return updated device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice().copy(name = "ProtoDevice")).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.UPDATE,
            updateDeviceRequest(device.id.toString()),
            UpdateDeviceResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(updateDeviceMapper.toUpdateDeviceResponse(device))
            .verifyComplete()
    }

    @Test
    fun `update should return message with exception when device doesn't exist`() {
        val invalidId = ObjectId().toString()

        // GIVEN // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.UPDATE,
            updateDeviceRequest(invalidId),
            UpdateDeviceResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(
                updateDeviceMapper.toFailureUpdateDeviceResponse(
                    EntityNotFoundException(
                        "Device with id $invalidId not found"
                    )
                )
            )
            .verifyComplete()
    }
}
