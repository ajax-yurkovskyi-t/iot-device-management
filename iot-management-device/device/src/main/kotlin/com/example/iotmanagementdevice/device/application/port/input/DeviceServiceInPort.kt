package com.example.iotmanagementdevice.device.application.port.input

import com.example.iotmanagementdevice.device.domain.CreateDevice
import com.example.iotmanagementdevice.device.domain.Device
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceServiceInPort {
    fun create(newDevice: CreateDevice): Mono<Device>

    fun getById(deviceId: String): Mono<Device>

    fun getDevicesByUserId(userId: String): Flux<Device>

    fun getAll(): Flux<Device>

    fun update(id: String, updateDevice: Device): Mono<Device>

    fun deleteById(id: String): Mono<Unit>
}
