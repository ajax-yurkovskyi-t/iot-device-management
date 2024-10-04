package com.example.iotmanagementdevice.dto.device.request

import jakarta.validation.constraints.NotBlank

data class DeviceUpdateRequestDto(
    @field:NotBlank(message = "Device name must not be blank.")
    val name: String,

    @field:NotBlank(message = "Device description must not be blank.")
    val description: String,

    @field:NotBlank(message = "Device type must not be blank.")
    val type: String,
    val statusType: DeviceStatusTypeUpdateRequest,
)

enum class DeviceStatusTypeUpdateRequest {
    ONLINE,
    OFFLINE
}
