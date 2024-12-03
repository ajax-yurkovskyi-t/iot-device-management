package com.example.iotmanagementdevice.user.application.port.input

import com.example.iotmanagementdevice.user.domain.CreateUser
import com.example.iotmanagementdevice.user.domain.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserServiceInPort {
    fun register(user: CreateUser): Mono<User>

    fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean>

    fun getUserById(id: String): Mono<User>

    fun getAll(): Flux<User>

    fun getUserByUsername(username: String): Mono<User>

    fun update(id: String, user: User): Mono<User>
}
