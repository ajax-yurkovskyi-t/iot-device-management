package com.example.iotmanagementdevice.device.domain

import java.time.Instant

data class Device(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
    val statusType: DeviceStatusType,
    val userId: String?,
    val updatedAt: Instant?,
) {
    enum class DeviceStatusType {
        ONLINE,
        OFFLINE
    }
}
