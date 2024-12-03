package com.example.iotmanagementdevice.device.infrastructure.nats

import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.getAllDevicesRequest
import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.GetAllDevicesMapper
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class GetAllDevicesNatsControllerTest : AbstractMongoTest {
    @Autowired
    private lateinit var deviceRepository: DeviseRepositoryOutPort

    @Autowired
    private lateinit var getAllDevicesMapper: GetAllDevicesMapper

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `should return all devices`() {
        // GIVEN
        val device1 = deviceRepository.save(createDevice()).block()!!
        val device2 = deviceRepository.save(createDevice().copy(name = "Device2")).block()!!
        val expectedDevices = listOf(device1, device2)
            .map { getAllDevicesMapper.toProtoDevice(it) }

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Device.GET_ALL,
            getAllDevicesRequest(),
            GetAllDevicesResponse.parser()
        )

        // THEN
        actual.test()
            .assertNext { actualResponse ->
                assertTrue(
                    actualResponse.success.devicesList.containsAll(expectedDevices),
                    "Response does not contain all expected devices"
                )
            }
            .verifyComplete()
    }
}
