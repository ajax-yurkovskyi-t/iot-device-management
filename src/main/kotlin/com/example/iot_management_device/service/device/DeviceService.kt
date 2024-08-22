package com.example.iot_management_device.service.device

import com.example.iot_management_device.dto.device.request.DeviceCreateRequestDto
import com.example.iot_management_device.dto.device.response.DeviceResponseDto
import com.example.iot_management_device.dto.device.request.DeviceUpdateRequestDto

interface DeviceService {
    fun create(device: DeviceCreateRequestDto): DeviceResponseDto

    fun getById(deviceId: Long): DeviceResponseDto

    fun getAll(): List<DeviceResponseDto>

    fun update(id:Long, device: DeviceUpdateRequestDto): DeviceResponseDto

    fun deleteById(id: Long)
}
