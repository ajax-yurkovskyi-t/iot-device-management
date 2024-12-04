package com.example.iotmanagementdevice.role.domain

data class Role(
    val id: String,
    val roleName: RoleName,
) {
    enum class RoleName {
        USER,
        ADMIN
    }
}
