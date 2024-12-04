package com.example.iotmanagementdevice.device.infrastructure.nats

import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.deleteDeviceRequest
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.DeleteDeviceMapper
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class DeleteDeviceNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var deviceRepository: DeviseRepositoryOutPort

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
