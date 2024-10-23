package com.example.iotmanagementdevice.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import com.example.iotmanagementdevice.mapper.impl.DeleteDeviceMapperImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DeleteDeviceMapperTest {
    private val deleteDeviceMapper = DeleteDeviceMapperImpl()

    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Deleting device by id failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = deleteDeviceMapper.failureDeleteResponse(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(DeleteDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = NullPointerException()

        // WHEN
        val actualResponse = deleteDeviceMapper.failureDeleteResponse(exception)

        // THEN
        assertEquals("", actualResponse.failure.message)
        assertEquals(DeleteDeviceResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}