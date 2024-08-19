package com.example.iot_management_device.dto.user.response

import com.example.iot_management_device.dto.device.response.DeviceResponseDto

data class UserResponseDto(
    val id: Long?,
    val username: String?,
    val email: String?,
    val phoneNumber: String?,
    val devices: List<DeviceResponseDto>?
)
