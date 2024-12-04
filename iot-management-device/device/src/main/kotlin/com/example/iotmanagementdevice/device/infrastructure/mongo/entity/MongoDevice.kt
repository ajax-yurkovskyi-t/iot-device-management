package com.example.iotmanagementdevice.device.infrastructure.mongo.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@TypeAlias("Device")
@Document(collection = MongoDevice.COLLECTION_NAME)
data class MongoDevice(
    @Id
    @JsonSerialize(using = ToStringSerializer::class)
    val id: ObjectId?,
    val name: String?,
    val description: String?,
    val type: String?,
    val statusType: DeviceStatusType?,
    @JsonSerialize(using = ToStringSerializer::class)
    val userId: ObjectId?,
    @LastModifiedDate
    val updatedAt: Instant? = null,
) {
    enum class DeviceStatusType {
        ONLINE,
        OFFLINE
    }

    companion object {
        const val COLLECTION_NAME = "device"
    }
}
