package com.example.core.dto.response

import com.example.core.dto.DeviceStatusType

data class DeviceResponseDto(
    val name: String?,

    val description: String?,

    val type: String?,

    val statusType: DeviceStatusType?,
)
