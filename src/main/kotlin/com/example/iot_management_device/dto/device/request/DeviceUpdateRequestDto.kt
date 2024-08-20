package com.example.iot_management_device.dto.device.request

import com.example.iot_management_device.model.DeviceStatusType

data class DeviceUpdateRequestDto(
    val name: String,
    val description: String,
    val type: String,
    val statusType: DeviceStatusType
)
