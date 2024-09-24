package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import org.bson.types.ObjectId

interface DeviceRepository {
    fun findById(deviceId: ObjectId): MongoDevice?

    fun findAll(): List<MongoDevice>

    fun save(device: MongoDevice): MongoDevice?

    fun deleteById(deviceId: ObjectId)
}
