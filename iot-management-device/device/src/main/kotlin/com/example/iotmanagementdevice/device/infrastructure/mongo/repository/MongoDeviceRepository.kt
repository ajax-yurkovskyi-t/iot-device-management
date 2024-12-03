package com.example.iotmanagementdevice.device.infrastructure.mongo.repository

import com.example.iotmanagementdevice.device.application.port.output.DeviseRepositoryOutPort
import com.example.iotmanagementdevice.device.domain.CreateDevice
import com.example.iotmanagementdevice.device.domain.Device
import com.example.iotmanagementdevice.device.infrastructure.mongo.entity.MongoDevice
import com.example.iotmanagementdevice.device.infrastructure.mongo.mapper.DeviceMapper
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoDeviceRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val deviceMapper: DeviceMapper,
) : DeviseRepositoryOutPort {
    override fun findById(deviceId: String): Mono<Device> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(deviceId))
        return mongoTemplate.findOne(query, MongoDevice::class.java)
            .map { deviceMapper.toDomain(it) }
    }

    override fun findDevicesByUserId(userId: String): Flux<Device> {
        val query = Query(Criteria.where(MongoDevice::userId.name).isEqualTo(ObjectId(userId)))
        return mongoTemplate.find(query, MongoDevice::class.java)
            .map { deviceMapper.toDomain(it) }
    }

    override fun findAll(): Flux<Device> =
        mongoTemplate.findAll(MongoDevice::class.java)
            .map { deviceMapper.toDomain(it) }

    override fun save(device: Device): Mono<Device> =
        mongoTemplate.save(deviceMapper.toEntity(device))
            .map { deviceMapper.toDomain(it) }

    override fun save(device: CreateDevice): Mono<Device> =
        mongoTemplate.save(deviceMapper.toEntity(device))
            .map { deviceMapper.toDomain(it) }

    override fun deleteById(deviceId: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(deviceId))
        return mongoTemplate.remove(query, MongoDevice::class.java).thenReturn(Unit)
    }
}
