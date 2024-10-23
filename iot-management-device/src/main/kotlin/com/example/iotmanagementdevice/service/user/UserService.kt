package com.example.iotmanagementdevice.service.user

import com.example.core.dto.response.DeviceResponseDto
import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserService {
    fun register(requestDto: UserRegistrationRequestDto): Mono<UserResponseDto>

    fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean>

    fun getUserById(id: String): Mono<UserResponseDto>

    fun getDevicesByUserId(userId: String): Flux<DeviceResponseDto>

    fun getAll(): Flux<UserResponseDto>

    fun getUserByUsername(username: String): Mono<UserResponseDto>

    fun update(id: String, requestDto: UserUpdateRequestDto): Mono<UserResponseDto>
}
