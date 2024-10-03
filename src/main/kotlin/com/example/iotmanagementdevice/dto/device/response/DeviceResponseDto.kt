package com.example.iotmanagementdevice.dto.device.response

import com.example.iotmanagementdevice.model.MongoDevice

data class DeviceResponseDto(
    val name: String?,

    val description: String?,

    val type: String?,

    val statusType: MongoDevice.DeviceStatusType?,
)
