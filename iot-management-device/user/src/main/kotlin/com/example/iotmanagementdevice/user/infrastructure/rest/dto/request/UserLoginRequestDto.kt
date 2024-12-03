package com.example.iotmanagementdevice.user.infrastructure.rest.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserLoginRequestDto(
    @field:NotBlank(message = "Please enter your email address.")
    @field:Email(message = "Please enter a valid email format.")
    val email: String,

    @field:NotBlank(message = "Please enter your password.")
    val userPassword: String
)
