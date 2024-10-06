package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DeviceRepositoryImpl(private val mongoTemplate: MongoTemplate) : DeviceRepository {
    override fun findById(deviceId: String): MongoDevice? {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(deviceId))
        return mongoTemplate.findOne(query, MongoDevice::class.java)
    }

    override fun findAll(): List<MongoDevice> =
        mongoTemplate.findAll(MongoDevice::class.java)

    override fun save(device: MongoDevice): MongoDevice =
        mongoTemplate.save(device)

    override fun deleteById(deviceId: String) {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(deviceId))
        mongoTemplate.remove(query, MongoDevice::class.java)
    }
}
