package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.model.MongoUser
import org.bson.types.ObjectId

interface UserRepository {
    fun findById(id: ObjectId): MongoUser?

    fun findAll(): List<MongoUser>

    fun assignDeviceToUser(userId: ObjectId, deviceId: ObjectId): Boolean

    fun save(user: MongoUser): MongoUser?

    fun deleteById(id: ObjectId)

    fun findByUserName(username: String): MongoUser?

    fun findByUserEmail(email: String): MongoUser?

    fun findDevicesByUserId(userId: ObjectId): List<MongoDevice>?
}
