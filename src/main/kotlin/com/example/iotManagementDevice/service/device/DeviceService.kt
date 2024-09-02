package com.example.iotManagementDevice.service.device

import com.example.iotManagementDevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotManagementDevice.dto.device.response.DeviceResponseDto
import com.example.iotManagementDevice.dto.device.request.DeviceUpdateRequestDto

interface DeviceService {
    fun create(device: DeviceCreateRequestDto): DeviceResponseDto

    fun getById(deviceId: Long): DeviceResponseDto

    fun getAll(): List<DeviceResponseDto>

    fun update(id:Long, device: DeviceUpdateRequestDto): DeviceResponseDto

    fun deleteById(id: Long)
}
