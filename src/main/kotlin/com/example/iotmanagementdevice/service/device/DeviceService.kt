package com.example.iotmanagementdevice.service.device

import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceService {
    fun create(device: DeviceCreateRequestDto): Mono<DeviceResponseDto>

    fun getById(deviceId: String): Mono<DeviceResponseDto>

    fun getAll(): Flux<DeviceResponseDto>

    fun update(id: String, device: DeviceUpdateRequestDto): Mono<DeviceResponseDto>

    fun deleteById(id: String): Mono<Unit>
}
