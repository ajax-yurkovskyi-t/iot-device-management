package com.example.iot_management_device.dto.device.response

import com.example.iot_management_device.model.DeviceStatusType

data class DeviceResponseDto(
    val name: String?,

    val description: String?,

    val type: String?,

    val statusType: DeviceStatusType?,
)
