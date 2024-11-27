package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.deleteDeviceRequest
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.iotmanagementdevice.mapper.DeleteDeviceMapper
import com.example.iotmanagementdevice.repository.AbstractMongoTest
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class DeleteDeviceNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var deleteDeviceMapper: DeleteDeviceMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should delete device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice()).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.DELETE,
            deleteDeviceRequest(device.id.toString()),
            DeleteDeviceResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(deleteDeviceMapper.toSuccessDeleteResponse())
            .verifyComplete()
    }
}
