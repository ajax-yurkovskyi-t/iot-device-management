package com.example.core.dto.request

import com.example.core.dto.DeviceStatusType
import jakarta.validation.constraints.NotBlank

data class DeviceCreateRequestDto(
    @field:NotBlank(message = "Device name must not be blank.")
    val name: String,

    @field:NotBlank(message = "Device description must not be blank.")
    val description: String,

    @field:NotBlank(message = "Device type must not be blank.")
    val type: String,
    val statusType: DeviceStatusType,
)
