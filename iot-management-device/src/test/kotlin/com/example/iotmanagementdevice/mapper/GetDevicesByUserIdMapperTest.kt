package com.example.iotmanagementdevice.mapper

import DeviceFixture.createDeviceResponseDto
import DeviceFixture.getDevicesByUserIdResponse
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.iotmanagementdevice.mapper.impl.GetDevicesByUserIdMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetDevicesByUserIdMapperTest {
    private val enumMapper = EnumMapperImpl()
    private val getDevicesByUserIdMapper = GetDevicesByUserIdMapperImpl(enumMapper)

    @Test
    fun `should map list of DeviceResponseDto to GetDevicesByUserIdResponse`() {
        // GIVEN
        val deviceResponseDto = createDeviceResponseDto()
        val deviceResponseDtoList = listOf(deviceResponseDto)

        // WHEN
        val actualResponse = getDevicesByUserIdMapper.toGetDevicesByUserIdResponse(deviceResponseDtoList)

        // THEN
        assertEquals(actualResponse, getDevicesByUserIdResponse(deviceResponseDtoList))
    }

    @Test
    fun `should return error response`() {
        // GIVEN
        val exceptionMessage = "Finding devices by user id failed"
        val exception = RuntimeException(exceptionMessage)

        // WHEN
        val actualResponse = getDevicesByUserIdMapper.toFailure(exception)

        // THEN
        assertEquals(exceptionMessage, actualResponse.failure.message)
        assertEquals(GetDevicesByUserIdResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }

    @Test
    fun `should return error response with default message when exception has no message`() {
        // GIVEN
        val exception = RuntimeException()

        // WHEN
        val actualResponse = getDevicesByUserIdMapper.toFailure(exception)

        // THEN
        assertTrue(actualResponse.failure.message.isBlank())
        assertEquals(GetDevicesByUserIdResponse.ResponseCase.FAILURE, actualResponse.responseCase)
    }
}
