package com.example.iot_management_device.security

import com.example.iot_management_device.dto.user.request.UserLoginRequestDto
import com.example.iot_management_device.dto.user.response.UserLoginResponseDto

interface AuthenticationService {
    fun authenticate(requestDto: UserLoginRequestDto): UserLoginResponseDto
}
