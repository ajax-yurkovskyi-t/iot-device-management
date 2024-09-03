package com.example.iotmanagementdevice.service.device

import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto

interface DeviceService {
    fun create(device: DeviceCreateRequestDto): DeviceResponseDto

    fun getById(deviceId: Long): DeviceResponseDto

    fun getAll(): List<DeviceResponseDto>

    fun update(id:Long, device: DeviceUpdateRequestDto): DeviceResponseDto

    fun deleteById(id: Long)
}
