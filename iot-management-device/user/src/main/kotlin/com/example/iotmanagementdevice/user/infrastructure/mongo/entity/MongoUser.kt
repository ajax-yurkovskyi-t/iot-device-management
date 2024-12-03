package com.example.iotmanagementdevice.user.infrastructure.mongo.entity

import com.example.iotmanagementdevice.role.domain.Role
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("User")
@Document(collection = MongoUser.COLLECTION_NAME)
data class MongoUser(
    @Id
    val id: ObjectId?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val userPassword: String?,
    val roles: Set<Role>?,
    val devices: List<ObjectId>?,
) {

    companion object {
        const val COLLECTION_NAME = "user"
    }
}
