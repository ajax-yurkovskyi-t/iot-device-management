package com.example.iot_management_device.service.device

import com.example.iot_management_device.model.Device

interface DeviceService {
    fun create(device: Device): Device

    fun getById(deviceId: Long): Device

    fun getAll(): List<Device>

    fun update(device: Device): Device

    fun deleteById(id: Long)
}