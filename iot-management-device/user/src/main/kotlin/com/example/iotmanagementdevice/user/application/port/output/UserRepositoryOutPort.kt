package com.example.iotmanagementdevice.user.application.port.output

import com.example.iotmanagementdevice.user.domain.CreateUser
import com.example.iotmanagementdevice.user.domain.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserRepositoryOutPort {
    fun findById(id: String): Mono<User>

    fun findAll(): Flux<User>

    fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean>

    fun save(user: CreateUser): Mono<User>

    fun save(user: User): Mono<User>

    fun deleteById(id: String): Mono<Unit>

    fun findByUserName(username: String): Mono<User>

    fun findByUserEmail(email: String): Mono<User>
}
