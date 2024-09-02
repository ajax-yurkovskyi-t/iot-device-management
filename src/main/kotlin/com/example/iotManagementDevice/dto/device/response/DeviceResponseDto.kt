package com.example.iotManagementDevice.dto.device.response

import com.example.iotManagementDevice.model.DeviceStatusType

data class DeviceResponseDto(
    val name: String?,

    val description: String?,

    val type: String?,

    val statusType: DeviceStatusType?,
)
