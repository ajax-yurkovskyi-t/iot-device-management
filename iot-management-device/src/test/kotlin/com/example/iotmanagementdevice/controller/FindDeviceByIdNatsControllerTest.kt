package com.example.iotmanagementdevice.controller

import DeviceFixture.createDevice
import DeviceFixture.getDeviceByIdRequest
import com.example.core.exception.EntityNotFoundException
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.GetDeviceByIdMapper
import com.example.iotmanagementdevice.repository.DeviceRepository
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FindDeviceByIdNatsControllerTest : AbstractNatsControllerTest() {
    @Autowired
    private lateinit var deviceRepository: DeviceRepository

    @Autowired
    private lateinit var getDeviceByIdMapper: GetDeviceByIdMapper

    @Autowired
    private lateinit var deviceMapper : DeviceMapper

    @Test
    fun `should return existing device`() {
        // GIVEN
        val device = deviceRepository.save(createDevice()).block()!!
        val deviceDto = deviceMapper.toDto(device)

        // WHEN
        val actual = doRequest(
            NatsSubject.Device.GET_BY_ID,
            getDeviceByIdRequest(device.id.toString()),
            GetDeviceByIdResponse.parser()
        )

        // THEN
        assertEquals(getDeviceByIdMapper.toGetDeviceByIdResponse(deviceDto), actual)
    }

    @Test
    fun `should return message with EntityNotFoundException when device not found`() {
        // GIVEN
        val invalidId = ObjectId().toString()

        // WHEN
        val actual = doRequest(
            NatsSubject.Device.GET_BY_ID,
            getDeviceByIdRequest(invalidId),
            GetDeviceByIdResponse.parser()
        )

        // THEN
        assertEquals(getDeviceByIdMapper.toErrorResponse(EntityNotFoundException("Device with id $invalidId not found")), actual)
    }
}
