package com.example.gateway.infrastructure.rest.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.failureCreateResponse
import DeviceProtoFixture.successfulCreateResponse
import com.example.gateway.infrastructure.rest.mapper.impl.CreateDeviceMapperImpl
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CreateDeviceMapperTest {
    private val enumMapper = EnumMapperImpl()
    private val createDeviceMapper = CreateDeviceMapperImpl(enumMapper)

    @Test
    fun `should map SUCCESS response to DeviceResponseDto`() {
        // Given
        val createDeviceResponse = successfulCreateResponse(deviceProto)

        // When
        val response = createDeviceMapper.toDto(createDeviceResponse)

        // Then
        assertEquals(response, deviceResponseDto)
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response case`(
        createDeviceResponse: CreateDeviceResponse,
        expectedMessage: String
    ) {
        // When & Then
        val exception = assertThrows<RuntimeException> {
            createDeviceMapper.toDto(createDeviceResponse)
        }
        assertEquals(expectedMessage, exception.message)
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
