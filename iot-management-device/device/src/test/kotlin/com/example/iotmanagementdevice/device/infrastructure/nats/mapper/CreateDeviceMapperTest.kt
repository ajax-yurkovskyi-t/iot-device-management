package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.impl.CreateDeviceMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CreateDeviceMapperTest {
    private val enumMapper = EnumMapperImpl()
    private val instantMapper =
        InstantToTimestampMapperImpl()
    private val createDeviceMapper = CreateDeviceMapperImpl(instantMapper, enumMapper)

    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Device creation failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = createDeviceMapper.toFailureCreateDeviceResponse(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(CreateDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = RuntimeException()

        // WHEN
        val actualResponse = createDeviceMapper.toFailureCreateDeviceResponse(exception)

        // THEN
        assertTrue(actualResponse.failure.message.isBlank())
        assertEquals(CreateDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}
