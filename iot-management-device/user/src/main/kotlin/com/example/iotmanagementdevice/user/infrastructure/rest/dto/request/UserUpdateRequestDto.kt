package com.example.iotmanagementdevice.user.infrastructure.rest.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserUpdateRequestDto(
    @field:NotBlank(message = "Name must not be blank.")
    val name: String,

    @field:NotBlank(message = "Email must not be blank.")
    @field:Email(message = "Email should be valid.")
    val email: String,

    @field:NotBlank(message = "Phone number must not be blank.")
    val phoneNumber: String,

    @field:NotBlank(message = "Password must not be blank.")
    val userPassword: String
)
