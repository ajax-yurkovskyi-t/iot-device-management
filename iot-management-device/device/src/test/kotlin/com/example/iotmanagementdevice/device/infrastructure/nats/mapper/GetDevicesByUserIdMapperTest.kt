package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.DeviceFixture.getDevicesByUserIdResponse
import com.example.iotmanagementdevice.device.infrastructure.nats.mapper.impl.GetDevicesByUserIdMapperImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetDevicesByUserIdMapperTest {
    private val enumMapper = EnumMapperImpl()
    private val instantMapper =
        InstantToTimestampMapperImpl()
    private val getDevicesByUserIdMapper = GetDevicesByUserIdMapperImpl(instantMapper, enumMapper)

    @Test
    fun `should map list of DeviceResponseDto to GetDevicesByUserIdResponse`() {
        // GIVEN
        val device = createDevice()
        val deviceList = listOf(device)

        // WHEN
        val actualResponse = getDevicesByUserIdMapper.toGetDevicesByUserIdResponse(deviceList)

        // THEN
        assertEquals(actualResponse, getDevicesByUserIdResponse(deviceList))
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
