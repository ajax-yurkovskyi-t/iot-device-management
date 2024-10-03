package com.example.iotmanagementdevice.security

import com.example.iotmanagementdevice.model.MongoUser
import org.bson.types.ObjectId
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class SecurityUser(
    private val mongoUser: MongoUser
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority>? {
        return mongoUser.roles
            ?.map { SimpleGrantedAuthority("ROLE_${it.roleName}") }
            ?.toSet()
    }

    override fun getPassword(): String? =
        mongoUser.userPassword

    override fun getUsername(): String? =
        mongoUser.email

    fun getId(): ObjectId? =
        mongoUser.id
}
