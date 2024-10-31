package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceRepository {
    fun findById(deviceId: String): Mono<MongoDevice>

    fun findAll(): Flux<MongoDevice>

    fun save(device: MongoDevice): Mono<MongoDevice>

    fun deleteById(deviceId: String): Mono<Unit>
}
