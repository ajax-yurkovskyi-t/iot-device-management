package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.updateDeviceRequest
import com.example.core.exception.EntityNotFoundException
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.UpdateDeviceMapper
import com.example.iotmanagementdevice.repository.AbstractMongoTest
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class UpdateDeviceNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var updateDeviceMapper: UpdateDeviceMapper

    @Autowired
    private lateinit var deviceMapper: DeviceMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should return updated device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice().copy(name = "ProtoDevice")).block()!!
        val deviceDto = deviceMapper.toDto(device)

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.UPDATE,
            updateDeviceRequest(device.id.toString()),
            UpdateDeviceResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(updateDeviceMapper.toUpdateDeviceResponse(deviceDto))
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
