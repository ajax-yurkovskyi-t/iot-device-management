package com.example.gateway.infrastructure.rest.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class DeleteDeviceMapperTest {
    private val deleteDeviceMapper = DeleteDeviceMapper()

    @Test
    fun `should do nothing for SUCCESS response case`() {
        // GIVEN
        val successResponse = DeleteDeviceResponse.newBuilder().apply { successBuilder }.build()

        // WHEN & THEN
        assertDoesNotThrow {
            deleteDeviceMapper.toDeleteResponse(successResponse)
        }
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response cases`(
        failureResponse: DeleteDeviceResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            deleteDeviceMapper.toDeleteResponse(failureResponse)
        }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun provideFailureResponseCases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    DeleteDeviceResponse.newBuilder().apply {
                        failureBuilder.setMessage("Failed to delete device")
                    }.build(),
                    "Failed to delete device"
                ),

                Arguments.of(
                    DeleteDeviceResponse.newBuilder().apply { failureBuilder }.build(),
                    "" // empty message
                ),

                Arguments.of(
                    DeleteDeviceResponse.getDefaultInstance(),
                    "No response case set"
                )
            )
        }
    }
}
