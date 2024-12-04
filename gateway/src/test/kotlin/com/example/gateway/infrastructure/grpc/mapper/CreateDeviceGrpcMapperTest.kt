package com.example.gateway.infrastructure.grpc.mapper

import DeviceProtoFixture.createDeviceRequest
import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.failureCreateResponse
import DeviceProtoFixture.grpcCreateDeviceRequest
import DeviceProtoFixture.successfulCreateResponse
import DeviceProtoFixture.successfulGrpcCreateResponse
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class CreateDeviceGrpcMapperTest {
    private val createDeviceGrpcMapper = CreateDeviceGrpcMapper()

    @Test
    fun `should map grpc create device request to internal request`() {
        // GIVEN
        val grpcCreateDeviceRequest = grpcCreateDeviceRequest()
        val createDeviceRequest = createDeviceRequest()

        // WHEN
        val result = createDeviceGrpcMapper.toInternal(grpcCreateDeviceRequest)

        // THEN
        assertEquals(createDeviceRequest, result)
    }

    @Test
    fun `should map successful create device response to grpc response`() {
        // GIVEN
        val grpcCreateDeviceResponse = successfulGrpcCreateResponse(deviceProto)
        val createDeviceResponse = successfulCreateResponse(deviceProto)

        // WHEN
        val result = createDeviceGrpcMapper.toGrpc(createDeviceResponse)

        // THEN
        assertEquals(grpcCreateDeviceResponse, result)
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response case`(
        createDeviceResponse: CreateDeviceResponse,
        expectedMessage: String
    ) {
        // When & Then
        val exception = assertThrows<RuntimeException> {
            createDeviceGrpcMapper.toGrpc(createDeviceResponse)
        }
        Assertions.assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun provideFailureResponseCases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    CreateDeviceResponse.getDefaultInstance(),
                    "No response case set"
                ),
                Arguments.of(
                    failureCreateResponse("Device creation failed"),
                    "Device creation failed"
                ),
            )
        }
    }
}
