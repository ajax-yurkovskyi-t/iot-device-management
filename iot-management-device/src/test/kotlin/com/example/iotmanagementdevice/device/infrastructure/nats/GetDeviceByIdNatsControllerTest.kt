package com.example.iotmanagementdevice.device.infrastructure.nats

import com.example.core.exception.EntityNotFoundException
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.getDeviceByIdRequest
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.GetDeviceByIdMapper
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class GetDeviceByIdNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var deviceRepository: DeviseRepositoryOutPort

    @Autowired
    private lateinit var getDeviceByIdMapper: GetDeviceByIdMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should return existing device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice()).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.GET_BY_ID,
            getDeviceByIdRequest(device.id.toString()),
            GetDeviceByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(getDeviceByIdMapper.toGetDeviceByIdResponse(device))
            .verifyComplete()
    }

    @Test
    fun `should return message with EntityNotFoundException when device not found`() {
        // GIVEN
        val invalidId = ObjectId().toString()

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.GET_BY_ID,
            getDeviceByIdRequest(invalidId),
            GetDeviceByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(
                getDeviceByIdMapper.toFailureGetDeviceByIdResponse(
                    EntityNotFoundException(
                        "Device with id $invalidId not found"
                    )
                )
            )
            .verifyComplete()
    }
}
