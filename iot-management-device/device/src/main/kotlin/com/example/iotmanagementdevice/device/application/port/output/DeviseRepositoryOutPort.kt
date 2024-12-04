package com.example.iotmanagementdevice.device.application.port.output

import com.example.iotmanagementdevice.device.domain.CreateDevice
import com.example.iotmanagementdevice.device.domain.Device
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviseRepositoryOutPort {
    fun findById(deviceId: String): Mono<Device>

    fun findAll(): Flux<Device>

    fun save(device: Device): Mono<Device>

    fun save(device: CreateDevice): Mono<Device>

    fun deleteById(deviceId: String): Mono<Unit>

    fun findDevicesByUserId(userId: String): Flux<Device>
}
