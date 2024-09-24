package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole
import org.bson.types.ObjectId

interface RoleRepository {
    fun findRoleById(roleId: ObjectId): MongoRole?
    fun findAll(): List<MongoRole>
    fun save(role: MongoRole): MongoRole?
    fun deleteById(roleId: ObjectId)
    fun findByRoleName(mongoRoleName: MongoRole.RoleName): MongoRole?
}
