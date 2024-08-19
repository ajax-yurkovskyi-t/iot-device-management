package com.example.iot_management_device.dto.user.request

data class UserUpdateRequestDto(
    val id: Long,
    val name: String,
    val email: String,
    val phoneNumber: String,
    var userPassword: String
)
