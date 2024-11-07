package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.updateDeviceRequest
import com.example.core.exception.EntityNotFoundException
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.UpdateDeviceMapper
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UpdateDeviceNatsControllerTest : AbstractNatsControllerTest() {
    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var updateDeviceMapper: UpdateDeviceMapper

    @Autowired
    private lateinit var deviceMapper: DeviceMapper

    @Test
    fun `should return updated device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice().copy(name = "ProtoDevice")).block()!!
        val deviceDto = deviceMapper.toDto(device)

        // WHEN
        val actual = doRequest(
            NatsSubject.Device.UPDATE,
            updateDeviceRequest(device.id.toString()),
            UpdateDeviceResponse.parser()
        )

        // THEN
        assertEquals(updateDeviceMapper.toUpdateDeviceResponse(deviceDto), actual)
    }

    @Test
    fun `update should return message with exception when device doesn't exist`() {
        val invalidId = ObjectId().toString()

        // GIVEN // WHEN
        val actual = doRequest(
            NatsSubject.Device.UPDATE,
            updateDeviceRequest(invalidId),
            UpdateDeviceResponse.parser()
        )

        // THEN
        assertEquals(
            updateDeviceMapper.toFailureUpdateDeviceResponse(
                EntityNotFoundException(
                    "Device with id $invalidId not found"
                )
            ),
            actual
        )
    }
}
