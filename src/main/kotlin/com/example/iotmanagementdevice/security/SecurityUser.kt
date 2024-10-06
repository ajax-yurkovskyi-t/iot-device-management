package com.example.iotmanagementdevice.security

import com.example.iotmanagementdevice.model.MongoRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class SecurityUser(
    val id: String?,
    private val email: String?,
    private val userPassword: String?,
    private val roles: Set<MongoRole>?,
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority>? =
        roles?.map { SimpleGrantedAuthority("ROLE_${it.roleName}") }

    override fun getPassword(): String? =
        userPassword

    override fun getUsername(): String? =
        email
}
