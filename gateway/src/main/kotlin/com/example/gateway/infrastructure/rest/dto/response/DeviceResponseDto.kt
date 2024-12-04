package com.example.gateway.infrastructure.rest.dto.response

import com.example.gateway.infrastructure.rest.dto.DeviceStatusType

data class DeviceResponseDto(
    val name: String?,

    val description: String?,

    val type: String?,

    val statusType: DeviceStatusType?,
)
