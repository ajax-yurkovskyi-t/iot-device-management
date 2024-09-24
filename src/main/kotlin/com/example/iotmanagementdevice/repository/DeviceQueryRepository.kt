package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DeviceQueryRepository(private val mongoTemplate: MongoTemplate) : DeviceRepository {
    override fun findById(deviceId: ObjectId): MongoDevice? {
        val query = Query(Criteria.where("_id").isEqualTo(deviceId))
        return mongoTemplate.findOne(query, MongoDevice::class.java)
    }

    override fun findAll(): List<MongoDevice> =
        mongoTemplate.findAll(MongoDevice::class.java)

    override fun save(device: MongoDevice): MongoDevice =
        mongoTemplate.save(device)

    override fun deleteById(deviceId: ObjectId) {
        val query = Query(Criteria.where("_id").isEqualTo(deviceId))
        mongoTemplate.remove(query, MongoDevice::class.java)
    }
}
