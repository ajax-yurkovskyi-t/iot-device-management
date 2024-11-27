package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.getDeviceByIdRequest
import com.example.core.exception.EntityNotFoundException
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.GetDeviceByIdMapper
import com.example.iotmanagementdevice.repository.AbstractMongoTest
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class GetDeviceByIdNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var getDeviceByIdMapper: GetDeviceByIdMapper

    @Autowired
    private lateinit var deviceMapper: DeviceMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should return existing device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice()).block()!!
        val deviceDto = deviceMapper.toDto(device)

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.GET_BY_ID,
            getDeviceByIdRequest(device.id.toString()),
            GetDeviceByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(getDeviceByIdMapper.toGetDeviceByIdResponse(deviceDto))
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
