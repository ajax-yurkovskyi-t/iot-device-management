package com.example.iotManagementDevice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.security.core.GrantedAuthority

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    @Column(nullable = false, unique = true)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val roleName: RoleName?
) : GrantedAuthority {

    override fun getAuthority(): String {
        return "ROLE_" + roleName?.name;
    }

    enum class RoleName {
        USER,
        ADMIN
    }
}
