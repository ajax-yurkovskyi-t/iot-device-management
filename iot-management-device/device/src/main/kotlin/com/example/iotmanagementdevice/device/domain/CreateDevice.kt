package com.example.iotmanagementdevice.device.domain

import com.example.iotmanagementdevice.device.domain.Device.DeviceStatusType

data class CreateDevice(
    val name: String,
    val description: String,
    val type: String,
    val statusType: DeviceStatusType,
)
