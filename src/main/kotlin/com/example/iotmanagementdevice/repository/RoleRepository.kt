package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole

interface RoleRepository {
    fun findRoleById(roleId: String): MongoRole?
    fun findAll(): List<MongoRole>
    fun save(role: MongoRole): MongoRole?
    fun deleteById(roleId: String)
    fun findByRoleName(mongoRoleName: MongoRole.RoleName): MongoRole?
}
