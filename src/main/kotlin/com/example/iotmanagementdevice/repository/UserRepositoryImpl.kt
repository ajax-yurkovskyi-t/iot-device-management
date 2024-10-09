package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregate
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AggregationPipeline
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.aggregation.LookupOperation
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
class UserRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) :
    UserRepository {
    override fun findById(id: String): Mono<MongoUser> {
        val query = Query(where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findAll(): Flux<MongoUser> =
        mongoTemplate.findAll(MongoUser::class.java)

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
            .map { (userUpdate, deviceUpdate) ->
                userUpdate.modifiedCount > 0 && deviceUpdate.modifiedCount > 0
            }
    }

    override fun findDevicesByUserId(userId: String): Flux<MongoDevice> {
        val pipelineStages = getDevicesLookUpAggregationPipeline(userId).operations
        val aggregationResults =
            mongoTemplate.aggregate<DeviceProjectionResult>(
                newAggregation(pipelineStages),
                MongoUser.COLLECTION_NAME
            )
        return aggregationResults.flatMapIterable { it.devices }
    }

    override fun save(user: MongoUser): Mono<MongoUser> =
        mongoTemplate.save(user)

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(where(Fields.UNDERSCORE_ID).isEqualTo(id))
        return mongoTemplate.remove(query, MongoUser::class.java).thenReturn(Unit)
    }

    override fun findByUserName(username: String): Mono<MongoUser> {
        val query = Query(where(MongoUser::name.name).isEqualTo(username))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findByUserEmail(email: String): Mono<MongoUser> {
        val query = Query(where(MongoUser::email.name).isEqualTo(email))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    private fun getDevicesLookUpAggregationPipeline(userId: String): AggregationPipeline {
        val lookupOperation = LookupOperation.newLookup()
            .from(MongoDevice.COLLECTION_NAME)
            .localField(MongoUser::devices.name)
            .foreignField("_id")
            .`as`("devices")

        return AggregationPipeline.of(
            match(where(Fields.UNDERSCORE_ID).isEqualTo(userId)),
            lookupOperation
        )
    }
}

internal data class DeviceProjectionResult(val devices: List<MongoDevice>)
