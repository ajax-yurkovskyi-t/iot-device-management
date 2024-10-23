package com.example.iotmanagementdevice.controller

import DeviceFixture.createDeviceRequest
import DeviceFixture.createDeviceResponseDto
import com.example.internal.NatsSubject
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.mapper.CreateDeviceMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CreateDeviceNatsControllerTest : AbstractNatsControllerTest() {
    @Autowired
    private lateinit var createDeviceMapper: CreateDeviceMapper

    @Test
    fun `should return saved device`() {
        // GIVEN
        val deviceResponseDto = createDeviceResponseDto().copy(name = "ProtoDevice")

        // WHEN
        val actual = doRequest(
            NatsSubject.Device.CREATE,
            createDeviceRequest(),
            CreateDeviceResponse.parser()
        )

        // THEN
        assertEquals(createDeviceMapper.toCreateDeviceResponse(deviceResponseDto), actual)
    }
}
