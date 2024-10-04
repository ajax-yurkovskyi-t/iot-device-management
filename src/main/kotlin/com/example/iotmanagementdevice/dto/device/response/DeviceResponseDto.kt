package com.example.iotmanagementdevice.dto.device.response

data class DeviceResponseDto(
    val name: String?,

    val description: String?,

    val type: String?,

    val statusType: DeviceStatusTypeResponse,
)

enum class DeviceStatusTypeResponse {
    ONLINE,
    OFFLINE
}
