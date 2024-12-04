package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.impl.UpdateDeviceMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UpdateDeviceMapperTest {
    private val enumMapper = EnumMapperImpl()
    private val instantMapper =
        InstantToTimestampMapperImpl()
    private val updateDeviceMapper = UpdateDeviceMapperImpl(instantMapper, enumMapper)

    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Finding device by id failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = updateDeviceMapper.toFailureUpdateDeviceResponse(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(UpdateDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = RuntimeException()

        // WHEN
        val actualResponse = updateDeviceMapper.toFailureUpdateDeviceResponse(exception)

        // THEN
        assertTrue(actualResponse.failure.message.isBlank())
        assertEquals(UpdateDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}
