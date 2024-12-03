package com.example.gateway.infrastructure.grpc.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.failureGetDevicesByUserIdResponse
import DeviceProtoFixture.successfulDeviceUpdatedEvent
import DeviceProtoFixture.successfulGetUpdatedDevicesResponse
import com.example.grpcapi.reqrep.device.StreamUpdatedDeviceResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
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
        val successResponse = successfulDeviceUpdatedEvent(deviceProto)
        val successUpdateDeviceResponse = successfulGetUpdatedDevicesResponse(deviceProto)

        // WHEN
        val updateDeviceResponse = getUpdatedDeviceGrpcMapper.toUpdatedDeviceResponse(successResponse)

        // THEN
        assertEquals(updateDeviceResponse, successUpdateDeviceResponse)
    }

    @Test
    fun `should throw RuntimeException when response not set`() {
        val response = GetDevicesByUserIdResponse.getDefaultInstance()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getUpdatedDeviceGrpcMapper.toUpdateDeviceResponseList(response)
        }

        assertEquals("No response case set", exception.message)
    }

    @ParameterizedTest
    @MethodSource("provideGetDevicesByUserIdResponseFailure")
    fun `should throw exception for GetDevicesByUserIdResponse failure response cases`(
        response: GetDevicesByUserIdResponse,
        expectedResponse: List<StreamUpdatedDeviceResponse>
    ) {
        // WHEN & THEN
        val result = getUpdatedDeviceGrpcMapper.toUpdateDeviceResponseList(response)

        assertEquals(expectedResponse, result)
    }

    companion object {
        @JvmStatic
        fun provideGetDevicesByUserIdResponseFailure(): List<Arguments> {
            return listOf(
                Arguments.of(
                    failureGetDevicesByUserIdResponse("Failed to find device by id"),
                    listOf(
                        StreamUpdatedDeviceResponse.newBuilder().apply {
                            failureBuilder.message = "Failed to find device by id"
                        }.build()
                    )
                ),
                Arguments.of(
                    GetDevicesByUserIdResponse.newBuilder().apply {
                        failureBuilder.userNotFoundBuilder
                        failureBuilder.message = "Device not found"
                    }.build(),
                    listOf(
                        StreamUpdatedDeviceResponse.newBuilder().apply {
                            failureBuilder.userNotFoundBuilder
                            failureBuilder.message = "Device not found"
                        }.build()
                    )
                )
            )
        }
    }
}
