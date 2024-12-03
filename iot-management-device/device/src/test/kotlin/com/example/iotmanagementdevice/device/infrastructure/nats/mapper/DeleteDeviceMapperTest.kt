package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DeleteDeviceMapperTest {
    private val deleteDeviceMapper = DeleteDeviceMapper()

    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Deleting device by id failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = deleteDeviceMapper.toFailureDeleteDeviceResponse(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(DeleteDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = RuntimeException()

        // WHEN
        val actualResponse = deleteDeviceMapper.toFailureDeleteDeviceResponse(exception)

        // THEN
        assertTrue(actualResponse.failure.message.isBlank())
        assertEquals(DeleteDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}
