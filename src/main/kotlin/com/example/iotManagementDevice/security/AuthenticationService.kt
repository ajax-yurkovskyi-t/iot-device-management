package com.example.iotManagementDevice.security

import com.example.iotManagementDevice.dto.user.request.UserLoginRequestDto
import com.example.iotManagementDevice.dto.user.response.UserLoginResponseDto

interface AuthenticationService {
    fun authenticate(requestDto: UserLoginRequestDto): UserLoginResponseDto
}
