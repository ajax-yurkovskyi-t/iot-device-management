package com.example.iotmanagementdevice.user.infrastructure.mongo.repository

import com.example.iotmanagementdevice.device.infrastructure.mongo.entity.MongoDevice
import com.example.iotmanagementdevice.user.application.port.output.UserRepositoryOutPort
import com.example.iotmanagementdevice.user.domain.CreateUser
import com.example.iotmanagementdevice.user.domain.User
import com.example.iotmanagementdevice.user.infrastructure.mongo.entity.MongoUser
import com.example.iotmanagementdevice.user.infrastructure.mongo.mapper.UserMapper
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Repository
class MongoUserRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val userMapper: UserMapper,
) : UserRepositoryOutPort {

    override fun findById(id: String): Mono<User> {
        val query = Query(where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return mongoTemplate.findOne(query, MongoUser::class.java)
            .map { userMapper.toDomain(it) }
    }

    override fun findAll(): Flux<User> =
        mongoTemplate.findAll(MongoUser::class.java)
            .map { userMapper.toDomain(it) }

    @Transactional
    override fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean> {
        val userUpdateResult = mongoTemplate.updateFirst(
            Query(where(Fields.UNDERSCORE_ID).isEqualTo(userId)),
            Update().addToSet(MongoUser::devices.name, ObjectId(deviceId)),
            MongoUser::class.java
        )
        val deviceUpdateResult = mongoTemplate.updateFirst(
            Query(where(Fields.UNDERSCORE_ID).isEqualTo(deviceId)),
            Update().set(MongoDevice::userId.name, ObjectId(userId)),
            MongoDevice::class.java
        )
        return Mono.zip(userUpdateResult, deviceUpdateResult)
            .flatMap { (userUpdate, deviceUpdate) ->
                if (userUpdate.modifiedCount > 0 && deviceUpdate.modifiedCount > 0) {
                    Mono.just(true)
                } else {
                    Mono.error(RuntimeException("Failed to assign device to user"))
                }
            }
    }

    override fun save(user: CreateUser): Mono<User> =
        mongoTemplate.save(userMapper.toEntity(user))
            .map { userMapper.toDomain(it) }

    override fun save(user: User): Mono<User> =
        mongoTemplate.save(userMapper.toEntity(user))
            .map { userMapper.toDomain(it) }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return mongoTemplate.remove(query, MongoUser::class.java).thenReturn(Unit)
    }

    override fun findByUserName(username: String): Mono<User> {
        val query = Query(where(MongoUser::name.name).isEqualTo(username))
        return mongoTemplate.findOne(query, MongoUser::class.java)
            .map { userMapper.toDomain(it) }
    }

    override fun findByUserEmail(email: String): Mono<User> {
        val query = Query(where(MongoUser::email.name).isEqualTo(email))
        return mongoTemplate.findOne(query, MongoUser::class.java)
            .map { userMapper.toDomain(it) }
    }
}
