package com.example.gateway.mapper.grpc

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.failureGetDevicesByUserIdResponse
import DeviceProtoFixture.failureUpdateDeviceResponse
import DeviceProtoFixture.successfulGetUpdatedDevicesResponse
import DeviceProtoFixture.successfulUpdateResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GetUpdatedDeviceGrpcMapperTest {
    private val getUpdatedDeviceGrpcMapper = GetUpdatedDeviceGrpcMapper()

    @Test
    fun `should map successful GetDevicesByUserIdResponse to UpdateDeviceResponse list`() {
        // GIVEN
        val successResponse = GetDevicesByUserIdResponse.newBuilder().apply {
            successBuilder.addDevices(deviceProto)
        }.build()
        val updateDeviceResponse = successfulGetUpdatedDevicesResponse(deviceProto)

        // WHEN
        val updateDeviceResponses = getUpdatedDeviceGrpcMapper.toUpdateDeviceResponseList(successResponse)

        // THEN
        assertTrue(
            updateDeviceResponses.contains(updateDeviceResponse),
            "updateDeviceResponses does not contain the expected updateDeviceResponse"
        )
    }

    @Test
    fun `should map successful UpdateDeviceResponse to GetUpdatedDeviceResponse`() {
        // GIVEN
        val successResponse = successfulUpdateResponse(deviceProto)
        val successUpdateDeviceResponse = successfulGetUpdatedDevicesResponse(deviceProto)

        // WHEN
        val updateDeviceResponse = getUpdatedDeviceGrpcMapper.toGetUpdatedDeviceResponse(successResponse)

        // THEN
        assertEquals(updateDeviceResponse, successUpdateDeviceResponse)
    }

    @ParameterizedTest
    @MethodSource("provideGetDevicesByUserIdResponseFailure")
    fun `should throw exception for GetDevicesByUserIdResponse failure response cases`(
        response: GetDevicesByUserIdResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getUpdatedDeviceGrpcMapper.toUpdateDeviceResponseList(response)
        }

        assertEquals(expectedMessage, exception.message)
    }

    @ParameterizedTest
    @MethodSource("provideUpdateDeviceResponseFailure")
    fun `should throw exception for UpdateDeviceResponse failure response cases`(
        response: UpdateDeviceResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getUpdatedDeviceGrpcMapper.toGetUpdatedDeviceResponse(response)
        }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun provideGetDevicesByUserIdResponseFailure(): List<Arguments> {
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

        @JvmStatic
        fun provideUpdateDeviceResponseFailure(): List<Arguments> {
            return listOf(
                Arguments.of(
                    UpdateDeviceResponse.getDefaultInstance(),
                    "No response case set"
                ),
                Arguments.of(
                    failureUpdateDeviceResponse("Failed to update device by id"),
                    "Failed to update device by id"
                ),
            )
        }
    }
}
