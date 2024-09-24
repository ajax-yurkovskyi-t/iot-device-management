package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregate
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.AggregationPipeline
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class UserQueryRepository(private val mongoTemplate: MongoTemplate) :
    UserRepository {
    override fun findById(id: ObjectId): MongoUser? {
        val query = Query(where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findAll(): List<MongoUser> =
        mongoTemplate.findAll(MongoUser::class.java)

    override fun assignDeviceToUser(userId: ObjectId, deviceId: ObjectId): Boolean {
        val userUpdateResult = mongoTemplate.updateFirst(
            Query(where("_id").isEqualTo(userId)),
            Update().addToSet("devices", deviceId),
            MongoUser::class.java
        )

        val deviceUpdateResult = mongoTemplate.updateFirst(
            Query(where("_id").isEqualTo(deviceId)),
            Update().set("userId", userId),
            MongoDevice::class.java
        )

        return userUpdateResult.modifiedCount > 0 && deviceUpdateResult.modifiedCount > 0
    }

    override fun findDevicesByUserId(userId: ObjectId): List<MongoDevice> {
        val pipelineStages = getDevicesLookUpAggregationPipeline(userId).operations
        val aggregationResults =
            mongoTemplate.aggregate<DeviceProjectionResult>(
                newAggregation(pipelineStages),
                MongoUser.COLLECTION_NAME
            )
        return aggregationResults.uniqueMappedResult?.devices ?: emptyList()
    }

    override fun save(user: MongoUser): MongoUser =
        mongoTemplate.save(user)

    override fun deleteById(id: ObjectId) {
        val query = Query(where("_id").isEqualTo(id))
        mongoTemplate.remove(query, MongoUser::class.java)
    }

    override fun findByUserName(username: String): MongoUser? {
        val query = Query(where(MongoUser::name.name).isEqualTo(username))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    override fun findByUserEmail(email: String): MongoUser? {
        val query = Query(where(MongoUser::email.name).isEqualTo(email))
        return mongoTemplate.findOne(query, MongoUser::class.java)
    }

    private fun getDevicesLookUpAggregationPipeline(userId: ObjectId): AggregationPipeline {
        val lookupOperation = LookupOperation.newLookup()
            .from("device")
            .localField("devices")
            .foreignField("_id")
            .`as`("devices")

        return AggregationPipeline.of(
            match(where("_id").isEqualTo(userId)),
            lookupOperation
        )
    }
}

internal data class DeviceProjectionResult(val devices: List<MongoDevice>?)
