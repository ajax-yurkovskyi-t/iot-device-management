package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.model.MongoUser

interface UserRepository {
    fun findById(id: String): MongoUser?

    fun findAll(): List<MongoUser>

    fun assignDeviceToUser(userId: String, deviceId: String): Boolean

    fun save(user: MongoUser): MongoUser?

    fun deleteById(id: String)

    fun findByUserName(username: String): MongoUser?

    fun findByUserEmail(email: String): MongoUser?

    fun findDevicesByUserId(userId: String): List<MongoDevice>
}
