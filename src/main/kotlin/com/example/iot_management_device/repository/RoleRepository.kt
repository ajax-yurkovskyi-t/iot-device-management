package com.example.iot_management_device.repository

import com.example.iot_management_device.model.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByRoleName(roleName: Role.RoleName): Role
}
