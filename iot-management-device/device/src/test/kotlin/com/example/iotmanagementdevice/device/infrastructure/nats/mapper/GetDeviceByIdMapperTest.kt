package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.impl.GetDeviceByIdMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetDeviceByIdMapperTest {
    private val enumMapper = EnumMapperImpl()
    private val instantMapper =
        InstantToTimestampMapperImpl()
    private val findDeviceByIdMapper = GetDeviceByIdMapperImpl(instantMapper, enumMapper)

    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Finding device by id failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = findDeviceByIdMapper.toFailureGetDeviceByIdResponse(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(GetDeviceByIdResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = RuntimeException()

        // WHEN
        val actualResponse = findDeviceByIdMapper.toFailureGetDeviceByIdResponse(exception)

        // THEN
        assertTrue(actualResponse.failure.message.isBlank())
        assertEquals(GetDeviceByIdResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}
