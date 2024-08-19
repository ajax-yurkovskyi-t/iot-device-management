package com.example.iot_management_device.dto.device.request

import com.example.iot_management_device.model.DeviceStatusType
import jakarta.validation.constraints.NotBlank

data class DeviceRequestDto(
    @field:NotBlank(message = "Device name must not be blank.")
    val name: String,

    @field:NotBlank(message = "Device description must not be blank.")
    val description: String,

    @field:NotBlank(message = "Device type must not be blank.")
    val type: String,
    val statusType: DeviceStatusType
)
