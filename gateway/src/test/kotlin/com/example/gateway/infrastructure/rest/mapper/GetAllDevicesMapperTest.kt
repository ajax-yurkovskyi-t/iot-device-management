package com.example.gateway.infrastructure.rest.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GetAllDevicesMapperTest {
    private val enumMapper = EnumMapperImpl()

    private val getAllDevicesMapper = GetAllDevicesMapperImpl(enumMapper)

    @Test
    fun `should map successful response to device response dto list`() {
        // GIVEN
        val successResponse = GetAllDevicesResponse.newBuilder()
            .setSuccess(
                GetAllDevicesResponse.Success.newBuilder()
                    .addDevices(deviceProto)
            )
            .build()

        // WHEN
        val responseDtoList = getAllDevicesMapper.toDto(successResponse)

        // THEN
        assertTrue(responseDtoList.contains(deviceResponseDto))
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response cases`(
        response: GetAllDevicesResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getAllDevicesMapper.toDto(response)
        }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun provideFailureResponseCases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    GetAllDevicesResponse.newBuilder().apply {
                        failureBuilder.message = ("Failed to get devices")
                    }.build(),
                    "Failed to get devices"
                ),
                Arguments.of(
                    GetAllDevicesResponse.getDefaultInstance(),
                    "No response case set"
                )
            )
        }
    }
}
