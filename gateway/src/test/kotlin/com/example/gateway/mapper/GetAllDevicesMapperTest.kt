package com.example.gateway.mapper

import DeviceProtoFixture.deviceProto
import DeviceProtoFixture.deviceResponseDto
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

    @Test
    fun `should throw error for failure response case`() {
        // GIVEN
        val failureMessage = "Failed to get devices"
        val failureResponse = GetAllDevicesResponse.newBuilder()
            .setFailure(
                GetAllDevicesResponse.Failure.newBuilder()
                    .setMessage(failureMessage)
            )
            .build()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getAllDevicesMapper.toDto(failureResponse)
        }
        assertEquals(failureMessage, exception.message)
    }

    @Test
    fun `should throw RuntimeException when response case is not set`() {
        // GIVEN
        val noResponseCaseSet = GetAllDevicesResponse.getDefaultInstance()

        // WHEN & THEN
        val exception = assertThrows<RuntimeException> {
            getAllDevicesMapper.toDto(noResponseCaseSet)
        }

        assertEquals("No response case set", exception.message)
    }
}
