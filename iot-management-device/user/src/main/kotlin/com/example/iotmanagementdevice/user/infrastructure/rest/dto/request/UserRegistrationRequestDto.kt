package com.example.iotmanagementdevice.user.infrastructure.rest.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserRegistrationRequestDto(
    @field:NotBlank(message = "Please enter a username.")
    val name: String,

    @field:NotBlank(message = "Please enter a valid email address.")
    @field:Email(message = "Please enter a valid email format.")
    val email: String,

    @field:NotBlank(message = "Please provide a mobile number.")
    val phoneNumber: String,

    @field:NotBlank(message = "Please enter a password.")
    var userPassword: String
)
