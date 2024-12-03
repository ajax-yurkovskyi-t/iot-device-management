package com.example.gateway.infrastructure.rest.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.failureUpdateDeviceResponse
import DeviceProtoFixture.successfulUpdateResponse
import com.example.commonmodels.Error
import com.example.gateway.infrastructure.rest.mapper.impl.UpdateDeviceMapperImpl
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UpdateDeviceMapperTest {
    private val enumMapper = EnumMapperImpl()

    private val updateDeviceMapper = UpdateDeviceMapperImpl(enumMapper)

    @Test
    fun `should map successful response to device response dto`() {
        // GIVEN
        val getDeviceResponse = successfulUpdateResponse(deviceProto)

        // WHEN
        val response = updateDeviceMapper.toDto(getDeviceResponse)

        // THEN
        assertEquals(response, deviceResponseDto)
    }

    @ParameterizedTest
    @MethodSource("provideFailureResponseCases")
    fun `should throw exception for failure response cases`(
        response: UpdateDeviceResponse,
        expectedMessage: String
    ) {
        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            updateDeviceMapper.toDto(response)
        }

        assertEquals(expectedMessage, exception.message)
    }

    companion object {
        @JvmStatic
        fun provideFailureResponseCases(): List<Arguments> {
            return listOf(
                Arguments.of(
                    UpdateDeviceResponse.getDefaultInstance(),
                    "No response case set"
                ),
                Arguments.of(
                    failureUpdateDeviceResponse("Failed to update device by id"),
                    "Failed to update device by id"
                ),
                Arguments.of(
                    UpdateDeviceResponse.newBuilder().apply {
                        failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
                        failureBuilder.message = "Device not found"
                    }.build(),
                    "Device not found"
                )
            )
        }
    }
}
