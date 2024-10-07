package com.example.iotmanagementdevice.security

import com.example.iotmanagementdevice.dto.user.request.UserLoginRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserLoginResponseDto
import reactor.core.publisher.Mono

interface AuthenticationService {
    fun authenticate(requestDto: UserLoginRequestDto): Mono<UserLoginResponseDto>
}
