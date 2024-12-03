package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.impl.GetAllDevicesMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetAllDevicesMapperTest {
    private val enumMapper = EnumMapperImpl()
    private val instantMapper =
        InstantToTimestampMapperImpl()
    private val getAllDevicesMapper = GetAllDevicesMapperImpl(instantMapper, enumMapper)

    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Finding device by id failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = getAllDevicesMapper.toFailureGetAllDevicesResponse(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(GetAllDevicesResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = RuntimeException()

        // WHEN
        val actualResponse = getAllDevicesMapper.toFailureGetAllDevicesResponse(exception)

        // THEN
        assertTrue(actualResponse.failure.message.isBlank())
        assertEquals(GetAllDevicesResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}
