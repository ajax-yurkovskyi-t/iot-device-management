package com.example.iotmanagementdevice.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Device")
@Document(collection = MongoDevice.COLLECTION_NAME)
data class MongoDevice(
    @Id
    val id: ObjectId?,
    val name: String?,
    val description: String?,
    val type: String?,
    val statusType: DeviceStatusType?,
    val userId: ObjectId?,
) {
    override fun toString(): String {
        return "Device(id=$id, name=$name, description=$description, " +
            "type=$type, statusType=$statusType, userId=$userId)"
    }

    companion object {
        const val COLLECTION_NAME = "device"
    }
}

enum class DeviceStatusType {
    ONLINE,
    OFFLINE
}
