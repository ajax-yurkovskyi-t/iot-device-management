package com.example.iotManagementDevice.controller

import com.example.iotManagementDevice.dto.user.request.UserLoginRequestDto
import com.example.iotManagementDevice.dto.user.response.UserLoginResponseDto
import com.example.iotManagementDevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotManagementDevice.dto.user.response.UserResponseDto
import com.example.iotManagementDevice.security.AuthenticationService
import com.example.iotManagementDevice.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService,
) {
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody @Valid requestDto: UserRegistrationRequestDto): UserResponseDto =
        userService.register(requestDto)

    @PostMapping("/login")
    fun login(@RequestBody @Valid requestDto: UserLoginRequestDto): UserLoginResponseDto =
        authenticationService.authenticate(requestDto)
}
