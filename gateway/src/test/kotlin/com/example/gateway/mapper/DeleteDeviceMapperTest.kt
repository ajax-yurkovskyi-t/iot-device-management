package com.example.gateway.mapper

import com.example.gateway.mapper.impl.CreateDeviceMapperImpl
import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

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
        assertTrue(exception is RuntimeException)
    }
}
