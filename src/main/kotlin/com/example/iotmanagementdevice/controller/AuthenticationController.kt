package com.example.iotmanagementdevice.controller

import com.example.iotmanagementdevice.dto.user.request.UserLoginRequestDto
import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserLoginResponseDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import com.example.iotmanagementdevice.security.AuthenticationService
import com.example.iotmanagementdevice.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val userService: UserService,
    private val authenticationService: AuthenticationService,
) {

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody @Valid requestDto: UserRegistrationRequestDto): Mono<UserResponseDto> =
        userService.register(requestDto)

    @PostMapping("/login")
    fun login(@RequestBody @Valid requestDto: UserLoginRequestDto): Mono<UserLoginResponseDto> =
        authenticationService.authenticate(requestDto)
}
