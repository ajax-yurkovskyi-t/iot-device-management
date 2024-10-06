package com.example.iotmanagementdevice.dto.user.response

import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto

data class UserResponseDto(
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val devices: List<DeviceResponseDto>,
)
