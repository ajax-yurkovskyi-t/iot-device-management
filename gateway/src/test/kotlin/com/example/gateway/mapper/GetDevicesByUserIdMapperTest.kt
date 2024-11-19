package com.example.gateway.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.failureGetDevicesByUserIdResponse
import DeviceProtoFixture.successfulUpdateResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GetDevicesByUserIdMapperTest {
    private val getDevicesByUserIdMapper = GetDevicesByUserIdMapper()

    @Test
    fun `should map successful GetDevicesByUserIdResponse to UpdateDeviceResponse list`() {
        // GIVEN
        val successResponse = GetDevicesByUserIdResponse.newBuilder().apply {
            successBuilder.addDevices(deviceProto)
        }.build()
        val updateDeviceResponse = successfulUpdateResponse(deviceProto)

        // WHEN
        val updateDeviceResponses = getDevicesByUserIdMapper.toUpdateDeviceResponseList(successResponse)

        // THEN
        assertTrue(
            updateDeviceResponses.contains(updateDeviceResponse),
            "updateDeviceResponses does not contain the expected updateDeviceResponse"
        )
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response cases`(
        response: GetDevicesByUserIdResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getDevicesByUserIdMapper.toUpdateDeviceResponseList(response)
        }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun provideFailureResponseCases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    GetDevicesByUserIdResponse.getDefaultInstance(),
                    "No response case set"
                ),
                Arguments.of(
                    failureGetDevicesByUserIdResponse("Failed to find device by id"),
                    "Failed to find device by id"
                ),
                Arguments.of(
                    GetDevicesByUserIdResponse.newBuilder().apply {
                        failureBuilder.userNotFoundBuilder
                        failureBuilder.message = "Device not found"
                    }.build(),
                    "Device not found"
                )
            )
        }
    }
}
