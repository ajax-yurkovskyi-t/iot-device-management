package com.example.gateway.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteDeviceMapperTest {
    private val deleteDeviceMapper = DeleteDeviceMapperImpl()

    @Test
    fun `should do nothing for SUCCESS response case`() {
        // GIVEN
        val successResponse = DeleteDeviceResponse.newBuilder()
            .setSuccess(DeleteDeviceResponse.Success.newBuilder().build())
            .build()

        // WHEN & THEN
        assertDoesNotThrow {
            deleteDeviceMapper.toDeleteResponse(successResponse)
        }
    }

    @Test
    fun `should throw RuntimeException for FAILURE response case with a message`() {
        // GIVEN
        val failureMessage = "Failed to delete device"
        val failureResponse = DeleteDeviceResponse.newBuilder()
            .setFailure(
                DeleteDeviceResponse.Failure.newBuilder()
                    .setMessage(failureMessage)
            )
            .build()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            deleteDeviceMapper.toDeleteResponse(failureResponse)
        }

        // Assert that the exception message matches the failure message
        assertEquals(failureMessage, exception.message)
    }

    @Test
    fun `should throw RuntimeException for FAILURE response case with empty message`() {
        // GIVEN
        val failureResponse = DeleteDeviceResponse.newBuilder()
            .setFailure(
                DeleteDeviceResponse.Failure.newBuilder()
                    .clearMessage() // Simulate empty message
            )
            .build()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            deleteDeviceMapper.toDeleteResponse(failureResponse)
        }

        // Assert that the exception message is empty
        assertEquals("", exception.message)
    }

    @Test
    fun `should throw RuntimeException when response case is RESPONSE_NOT_SET`() {
        // GIVEN
        val noResponseCaseSet = DeleteDeviceResponse.getDefaultInstance()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            deleteDeviceMapper.toDeleteResponse(noResponseCaseSet)
        }

        // Assert that the exception type is RuntimeException
        assertEquals("No response case set", exception.message)
    }
}
