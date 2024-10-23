package com.example.gateway.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.failureUpdateDeviceResponse
import DeviceProtoFixture.successfulUpdateResponse
import com.example.core.exception.EntityNotFoundException
import com.example.gateway.mapper.impl.CreateDeviceMapperImpl
import com.example.gateway.mapper.impl.UpdateDeviceMapperImpl
import com.example.internal.commonmodels.Error
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

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


    @Test
    fun `should throw RuntimeException when no response case is set`() {
        // GIVEN
        val updateDeviceResponse = UpdateDeviceResponse.getDefaultInstance()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            updateDeviceMapper.toDto(updateDeviceResponse)
        }
        assertEquals("No response case set", exception.message)
    }


    @Test
    fun `should throw error for FAILURE response case`() {
        // GIVEN
        val failureMessage = "Failed to update device by id"
        val updateDeviceResponse = failureUpdateDeviceResponse(failureMessage)

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            updateDeviceMapper.toDto(updateDeviceResponse)
        }
        assertEquals(failureMessage, exception.message)
    }

    @Test
    fun `should throw EntityNotFoundException when DEVICE_NOT_FOUND is the error case`() {
        // GIVEN
        val failureMessage = "Device not found"
        val updateDeviceResponse = UpdateDeviceResponse.newBuilder().apply {
            failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
            failureBuilder.message = failureMessage
        }.build()


        // WHEN & THEN
        val exception = assertThrows<EntityNotFoundException> {
            updateDeviceMapper.toDto(updateDeviceResponse)
        }

        assertEquals(failureMessage, exception.message)
    }
}
