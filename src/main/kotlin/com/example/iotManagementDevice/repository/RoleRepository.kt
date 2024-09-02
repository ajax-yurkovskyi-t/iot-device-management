package com.example.iotManagementDevice.repository

import com.example.iotManagementDevice.model.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByRoleName(roleName: Role.RoleName): Role
}
