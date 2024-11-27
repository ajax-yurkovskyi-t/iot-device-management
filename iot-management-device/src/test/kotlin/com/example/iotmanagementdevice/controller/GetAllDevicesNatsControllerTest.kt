package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.getAllDevicesRequest
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.GetAllDevicesMapper
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class GetAllDevicesNatsControllerTest : AbstractNatsControllerTest() {
    @Autowired
    @Qualifier("mongoDeviceRepository")
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var findAllDevicesMapper: GetAllDevicesMapper

    @Autowired
    private lateinit var deviceMapper: DeviceMapper

    @Test
    fun `should return all devices`() {
        // GIVEN
        val device1 = deviceRepository.save(createDevice()).block()!!
        val device2 = deviceRepository.save(createDevice().copy(name = "Device2")).block()!!

        // WHEN
        val actualResponse = doRequest(
            NatsSubject.Device.GET_ALL,
            getAllDevicesRequest(),
            GetAllDevicesResponse.parser()
        )

        // THEN
        val actualDevices = actualResponse.success.devicesList
        val expectedDevices =
            listOf(device1, device2).map { findAllDevicesMapper.toProtoDevice(deviceMapper.toDto(it)) }

        assertTrue(actualDevices.containsAll(expectedDevices), "Response does not contain all expected devices")
    }
}
