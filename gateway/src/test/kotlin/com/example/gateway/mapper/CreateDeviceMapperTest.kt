package com.example.gateway.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.failureCreateResponse
import DeviceProtoFixture.successfulCreateResponse
import com.example.gateway.mapper.impl.CreateDeviceMapperImpl
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    fun `should throw RuntimeException for no response case set`() {
        // Given
        val createDeviceResponse = CreateDeviceResponse.getDefaultInstance()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            createDeviceMapper.toDto(createDeviceResponse)
        }
        assertEquals("No response case set", exception.message)
    }

    @Test
    fun `should throw error for FAILURE response case`() {
        // Given
        val failureMessage = "Device creation failed"
        val createDeviceResponse = failureCreateResponse(failureMessage)

        // When & Then
        val exception = assertThrows<RuntimeException> {
            createDeviceMapper.toDto(createDeviceResponse)
        }
        assertEquals(failureMessage, exception.message)
    }
}
