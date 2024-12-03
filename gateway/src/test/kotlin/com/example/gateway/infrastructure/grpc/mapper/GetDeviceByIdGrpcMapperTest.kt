package com.example.gateway.infrastructure.grpc.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.failureGetDeviceByIdResponse
import DeviceProtoFixture.failureGrpcGetDeviceByIdResponse
import DeviceProtoFixture.getDeviceByIdRequest
import DeviceProtoFixture.grpcGetDeviceByIdRequest
import DeviceProtoFixture.successfulGetDeviceByIdResponse
import DeviceProtoFixture.successfulGrpcGetDeviceByIdResponse
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import com.example.grpcapi.reqrep.device.GetDeviceByIdResponse as GrpcGetDeviceByIdResponse

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

    @Test
    fun `should throw RuntimeException when response not set`() {
        val response = GetDeviceByIdResponse.getDefaultInstance()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getDeviceByIdGrpcMapper.toGrpc(response)
        }

        assertEquals("No response case set", exception.message)
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should handle failure response cases correctly`(
        response: GetDeviceByIdResponse,
        expectedResponse: GrpcGetDeviceByIdResponse
    ) {
        // WHEN
        val grpcResponse = getDeviceByIdGrpcMapper.toGrpc(response)

        // THEN
        assertEquals(expectedResponse, grpcResponse)
    }

    companion object {
        @JvmStatic
        fun provideFailureResponseCases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    failureGetDeviceByIdResponse("Failed to find device by id"),
                    failureGrpcGetDeviceByIdResponse("Failed to find device by id")
                ),
                Arguments.of(
                    GetDeviceByIdResponse.newBuilder().apply {
                        failureBuilder.deviceNotFoundBuilder
                        failureBuilder.message = "Device not found"
                    }.build(),
                    GrpcGetDeviceByIdResponse.newBuilder().apply {
                        failureBuilder.deviceNotFoundBuilder
                        failureBuilder.message = "Device not found"
                    }.build(),
                )
            )
        }
    }
}
