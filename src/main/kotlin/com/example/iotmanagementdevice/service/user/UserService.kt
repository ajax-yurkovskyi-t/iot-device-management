package com.example.iotmanagementdevice.service.user

import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto

interface UserService {
    fun register(requestDto: UserRegistrationRequestDto): UserResponseDto

    fun assignDeviceToUser(userId: String, deviceId: String): Boolean

    fun getUserById(id: String): UserResponseDto

    fun getDevicesByUserId(userId: String): List<DeviceResponseDto>

    fun getAll(): List<UserResponseDto>

    fun getUserByUsername(username: String): UserResponseDto

    fun update(id: String, requestDto: UserUpdateRequestDto): UserResponseDto
}
