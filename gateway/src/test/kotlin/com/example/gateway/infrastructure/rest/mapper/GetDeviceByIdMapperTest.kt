package com.example.gateway.infrastructure.rest.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.failureGetDeviceByIdResponse
import DeviceProtoFixture.successfulGetDeviceByIdResponse
import com.example.commonmodels.Error
import com.example.gateway.infrastructure.rest.mapper.impl.GetDeviceByIdMapperImpl
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GetDeviceByIdMapperTest {

    private val enumMapper = EnumMapperImpl()

    private val getDeviceByIdMapper = GetDeviceByIdMapperImpl(enumMapper)

    @Test
    fun `should map successful response to device response dto`() {
        // GIVEN
        val getDeviceResponse = successfulGetDeviceByIdResponse(deviceProto)

        // WHEN
        val response = getDeviceByIdMapper.toDto(getDeviceResponse)

        // THEN
        assertEquals(response, deviceResponseDto)
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response cases`(
        response: GetDeviceByIdResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getDeviceByIdMapper.toDto(response)
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
