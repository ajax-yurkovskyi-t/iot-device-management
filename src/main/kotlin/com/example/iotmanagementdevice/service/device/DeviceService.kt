package com.example.iotmanagementdevice.service.device

import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto

interface DeviceService {
    fun create(device: DeviceCreateRequestDto): DeviceResponseDto

    fun getById(deviceId: String): DeviceResponseDto

    fun getAll(): List<DeviceResponseDto>

    fun update(id: String, device: DeviceUpdateRequestDto): DeviceResponseDto

    fun deleteById(id: String)
}
