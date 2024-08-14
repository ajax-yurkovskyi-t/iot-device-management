package com.example.iot_management_device.dto.user

import jakarta.validation.constraints.NotEmpty

data class UserRegistrationResponseDto
    (val id: String?,
    @field:NotEmpty(message = "Username cannot be empty.")
    val username: String,
    @field:NotEmpty(message = "Email cannot be empty.")
    val email: String,
    @field:NotEmpty(message = "Mobile number cannot be empty.")
    val phoneNumber: String,
    val password: String,)
{
}