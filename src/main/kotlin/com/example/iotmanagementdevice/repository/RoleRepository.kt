package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role, Long> {
    fun findByRoleName(roleName: Role.RoleName): Role
}
