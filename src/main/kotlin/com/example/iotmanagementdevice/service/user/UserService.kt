package com.example.iotmanagementdevice.service.user

import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserService {
    fun register(requestDto: UserRegistrationRequestDto): Mono<UserResponseDto> // Changed to Mono

    fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean> // Changed to Mono

    fun getUserById(id: String): Mono<UserResponseDto> // Changed to Mono

    fun getDevicesByUserId(userId: String): Flux<DeviceResponseDto> // Changed to Flux

    fun getAll(): Flux<UserResponseDto> // Changed to Flux

    fun getUserByUsername(username: String): Mono<UserResponseDto> // Changed to Mono

    fun update(id: String, requestDto: UserUpdateRequestDto): Mono<UserResponseDto> // Changed to Mono
}
