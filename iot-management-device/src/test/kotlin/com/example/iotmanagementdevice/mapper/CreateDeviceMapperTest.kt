package com.example.iotmanagementdevice.mapper

import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.mapper.impl.CreateDeviceMapperImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CreateDeviceMapperTest {

    private val createDeviceMapper = CreateDeviceMapperImpl()


    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Device creation failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = createDeviceMapper.toErrorResponse(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(CreateDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = NullPointerException()

        // WHEN
        val actualResponse = createDeviceMapper.toErrorResponse(exception)

        // THEN
        assertEquals("", actualResponse.failure.message)
        assertEquals(CreateDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}