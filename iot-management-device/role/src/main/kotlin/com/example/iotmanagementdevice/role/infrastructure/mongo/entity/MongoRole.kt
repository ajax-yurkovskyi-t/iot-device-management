package com.example.iotmanagementdevice.role.infrastructure.mongo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Role")
@Document(collection = MongoRole.COLLECTION_NAME)
data class MongoRole(
    @Id
    val id: ObjectId?,
    val roleName: RoleName?,
) {
    enum class RoleName {
        USER,
        ADMIN
    }

    companion object {
        const val COLLECTION_NAME = "role"
    }
}
