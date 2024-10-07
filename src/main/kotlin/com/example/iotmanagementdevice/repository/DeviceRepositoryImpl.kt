package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class DeviceRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : DeviceRepository {
    override fun findById(deviceId: String): Mono<MongoDevice> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(deviceId))
        return mongoTemplate.findOne(query, MongoDevice::class.java)
    }

    override fun findAll(): Flux<MongoDevice> =
        mongoTemplate.findAll(MongoDevice::class.java)

    override fun save(device: MongoDevice): Mono<MongoDevice> =
        mongoTemplate.save(device)

    override fun deleteById(deviceId: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(deviceId))
        return mongoTemplate.remove(query, MongoDevice::class.java).thenReturn(Unit)
    }
}
