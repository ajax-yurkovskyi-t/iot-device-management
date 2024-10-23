package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.model.MongoUser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserRepository {
    fun findById(id: String): Mono<MongoUser>

    fun findAll(): Flux<MongoUser>

    fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean>

    fun save(user: MongoUser): Mono<MongoUser>

    fun deleteById(id: String): Mono<Unit>

    fun findByUserName(username: String): Mono<MongoUser>

    fun findByUserEmail(email: String): Mono<MongoUser>

    fun findDevicesByUserId(userId: String): Flux<MongoDevice>
}
