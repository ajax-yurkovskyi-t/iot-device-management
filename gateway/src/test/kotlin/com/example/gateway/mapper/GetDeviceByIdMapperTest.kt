package com.example.gateway.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import DeviceProtoFixture.failureGetDeviceByIdResponse
import DeviceProtoFixture.successfulGetDeviceByIdResponse
import com.example.core.exception.EntityNotFoundException
import com.example.gateway.mapper.impl.GetDeviceByIdMapperImpl
import com.example.internal.commonmodels.Error
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    fun `should throw RuntimeException when no response case is set`() {
        // GIVEN
        val getDeviceResponse = GetDeviceByIdResponse.getDefaultInstance()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getDeviceByIdMapper.toDto(getDeviceResponse)
        }
        assertEquals("No response case set", exception.message)
    }

    @Test
    fun `should throw error for FAILURE response case`() {
        // GIVEN
        val failureMessage = "Failed to find device by id"
        val getDeviceResponse = failureGetDeviceByIdResponse(failureMessage)

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getDeviceByIdMapper.toDto(getDeviceResponse)
        }
        assertEquals(failureMessage, exception.message)
    }

    @Test
    fun `should throw EntityNotFoundException when DEVICE_NOT_FOUND is the error case`() {
        // GIVEN
        val failureMessage = "Device not found"
        val getDeviceResponse = GetDeviceByIdResponse.newBuilder().apply {
            failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
            failureBuilder.message = failureMessage
        }.build()

        // WHEN & THEN
        val exception = assertThrows<EntityNotFoundException> {
            getDeviceByIdMapper.toDto(getDeviceResponse)
        }

        assertEquals(failureMessage, exception.message)
    }
}
