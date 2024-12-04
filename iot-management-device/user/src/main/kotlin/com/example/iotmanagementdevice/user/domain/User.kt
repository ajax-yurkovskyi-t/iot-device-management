package com.example.iotmanagementdevice.user.domain

import com.example.iotmanagementdevice.role.domain.Role

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val userPassword: String,
    val roles: Set<Role>,
    val devices: List<String>,
)
