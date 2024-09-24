package com.example.iotmanagementdevice.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@TypeAlias("User")
@Document(collection = MongoUser.COLLECTION_NAME)
data class MongoUser(
    @Id
    val id: ObjectId?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val userPassword: String?,
    val roles: MutableSet<MongoRole>?,
    val devices: MutableList<ObjectId>?,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return roles
    }

    override fun getPassword(): String? {
        return userPassword
    }

    override fun getUsername(): String? {
        return name
    }

    override fun toString(): String {
        return "User(id=$id, name=$name, email=$email, phoneNumber=$phoneNumber)"
    }

    companion object {
        const val COLLECTION_NAME = "user"
    }
}
