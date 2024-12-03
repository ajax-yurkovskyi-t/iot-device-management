package com.example.iotmanagementdevice.user.infrastructure.rest.dto.response

data class UserResponseDto(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val devices: List<String>,
)
