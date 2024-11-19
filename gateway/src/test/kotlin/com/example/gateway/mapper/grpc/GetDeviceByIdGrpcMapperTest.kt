package com.example.gateway.mapper.grpc

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.failureGetDeviceByIdResponse
import DeviceProtoFixture.getDeviceByIdRequest
import DeviceProtoFixture.grpcGetDeviceByIdRequest
import DeviceProtoFixture.successfulGetDeviceByIdResponse
import DeviceProtoFixture.successfulGrpcGetDeviceByIdResponse
import com.example.commonmodels.Error
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GetDeviceByIdGrpcMapperTest {
    private val getDeviceByIdGrpcMapper = GetDeviceByIdGrpcMapper()

    @Test
    fun `should map grpc get device request to internal request`() {
        // GIVEN
        val deviceId = ObjectId().toString()
        val grpcGetDeviceByIdRequest = grpcGetDeviceByIdRequest(deviceId)
        val getDeviceByIdRequest = getDeviceByIdRequest(deviceId)

        // WHEN
        val result = getDeviceByIdGrpcMapper.toInternal(grpcGetDeviceByIdRequest)

        // THEN
        assertEquals(result, getDeviceByIdRequest)
    }

    @Test
    fun `should map successful get device by id response to grpc response`() {
        // GIVEN
        val grpcGetDeviceByIdResponse = successfulGrpcGetDeviceByIdResponse(deviceProto)
        val getDeviceResponse = successfulGetDeviceByIdResponse(deviceProto)

        // WHEN
        val result = getDeviceByIdGrpcMapper.toGrpc(getDeviceResponse)

        // THEN
        assertEquals(result, grpcGetDeviceByIdResponse)
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response cases`(
        response: GetDeviceByIdResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getDeviceByIdGrpcMapper.toGrpc(response)
        }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun provideFailureResponseCases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    GetDeviceByIdResponse.getDefaultInstance(),
                    "No response case set"
                ),
                Arguments.of(
                    failureGetDeviceByIdResponse("Failed to find device by id"),
                    "Failed to find device by id"
                ),
                Arguments.of(
                    GetDeviceByIdResponse.newBuilder().apply {
                        failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
                        failureBuilder.message = "Device not found"
                    }.build(),
                    "Device not found"
                )
            )
        }
    }
}
