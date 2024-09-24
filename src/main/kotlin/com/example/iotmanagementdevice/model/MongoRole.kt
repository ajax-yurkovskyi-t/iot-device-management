package com.example.iotmanagementdevice.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority

@TypeAlias("Role")
@Document(collection = MongoRole.COLLECTION_NAME)
data class MongoRole(
    @Id
    val id: ObjectId?,
    val roleName: RoleName?,
) : GrantedAuthority {

    override fun getAuthority(): String {
        return "ROLE_" + roleName?.name
    }

    enum class RoleName {
        USER,
        ADMIN
    }

    companion object {
        const val COLLECTION_NAME = "role"
    }
}
