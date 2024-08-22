package com.example.iot_management_device.dto.user.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserLoginRequestDto(
    @field:NotBlank(message = "Please enter your email address.")
    @field:Email(message = "Please enter a valid email format.")
    val email: String,

    @field:NotBlank(message = "Please enter your password.")
    val userPassword: String
)
