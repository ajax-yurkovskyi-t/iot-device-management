package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.deleteDeviceRequest
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.iotmanagementdevice.mapper.DeleteDeviceMapper
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class DeleteDeviceNatsControllerTest : AbstractNatsControllerTest() {
    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var deleteDeviceMapper: DeleteDeviceMapper

    @Test
    fun `should delete device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice()).block()!!

        // WHEN
        val actual = doRequest(
            NatsSubject.Device.DELETE,
            deleteDeviceRequest(device.id.toString()),
            DeleteDeviceResponse.parser()
        )

        // THEN
        assertEquals(deleteDeviceMapper.toSuccessDeleteResponse(), actual)
    }
}
