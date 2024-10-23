package com.example.gateway.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.failureCreateResponse
import DeviceProtoFixture.successfulCreateResponse
import com.example.core.dto.DeviceStatusType
import com.example.gateway.mapper.impl.CreateDeviceMapperImpl
import com.example.internal.commonmodels.Device
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired

class CreateDeviceMapperTest {
    private val enumMapper = EnumMapperImpl()

    private val createDeviceMapper = CreateDeviceMapperImpl(enumMapper)

    @Test
    fun `should map SUCCESS response to DeviceResponseDto`() {
        // Arrange
        val createDeviceResponse = successfulCreateResponse(deviceProto)

        // Act
        val response = createDeviceMapper.toDto(createDeviceResponse)

        // Assert
        assertEquals(response, deviceResponseDto)
    }

    @Test
    fun `should throw RuntimeException for no response case set`() {
        // Arrange
        val createDeviceResponse = CreateDeviceResponse.getDefaultInstance()

        // Act & Assert
        val exception = assertThrows<RuntimeException> {
            createDeviceMapper.toDto(createDeviceResponse)
        }
        assertEquals("No response case set", exception.message)
    }

    @Test
    fun `should throw error for FAILURE response case`() {
        // Arrange
        val failureMessage = "Device creation failed"
        val createDeviceResponse = failureCreateResponse(failureMessage)

        // Act & Assert
        val exception = assertThrows<RuntimeException> {
            createDeviceMapper.toDto(createDeviceResponse)
        }
        assertEquals("Device creation failed", exception.message)
    }
}
