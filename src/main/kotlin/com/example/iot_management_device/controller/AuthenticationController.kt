package com.example.iot_management_device.controller

import com.example.iot_management_device.dto.user.request.UserLoginRequestDto
import com.example.iot_management_device.dto.user.response.UserLoginResponseDto
import com.example.iot_management_device.dto.user.request.UserRegistrationRequestDto
import com.example.iot_management_device.dto.user.response.UserResponseDto
import com.example.iot_management_device.security.AuthenticationService
import com.example.iot_management_device.security.AuthenticationServiceImpl
import com.example.iot_management_device.service.user.UserService
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
    private val authenticationService: AuthenticationService
) {
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody @Valid requestDto: UserRegistrationRequestDto): UserResponseDto =
        userService.register(requestDto)

    @PostMapping("/login")
    fun login(@RequestBody @Valid requestDto: UserLoginRequestDto): UserLoginResponseDto =
        authenticationService.authenticate(requestDto)
}
