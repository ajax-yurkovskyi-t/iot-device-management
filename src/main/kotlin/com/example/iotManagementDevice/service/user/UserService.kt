package com.example.iotManagementDevice.service.user

import com.example.iotManagementDevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotManagementDevice.dto.user.response.UserResponseDto
import com.example.iotManagementDevice.dto.user.request.UserUpdateRequestDto


interface UserService {
    fun register(requestDto: UserRegistrationRequestDto): UserResponseDto

    fun assignDeviceToUser(userId: Long, deviceId: Long): UserResponseDto

    fun getUserById(id: Long): UserResponseDto

    fun getAll(): List<UserResponseDto>

    fun getUserByUsername(username:String): UserResponseDto

    fun update(id: Long, requestDto: UserUpdateRequestDto): UserResponseDto

}
