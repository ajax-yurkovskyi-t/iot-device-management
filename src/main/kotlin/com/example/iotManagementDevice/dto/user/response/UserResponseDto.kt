package com.example.iotManagementDevice.dto.user.response

import com.example.iotManagementDevice.dto.device.response.DeviceResponseDto

data class UserResponseDto(
    val id: Long?,
    val username: String?,
    val email: String?,
    val phoneNumber: String?,
    val devices: List<DeviceResponseDto>?
)
