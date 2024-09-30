package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice

interface DeviceRepository {
    fun findById(deviceId: String): MongoDevice?

    fun findAll(): List<MongoDevice>

    fun save(device: MongoDevice): MongoDevice?

    fun deleteById(deviceId: String)
}
