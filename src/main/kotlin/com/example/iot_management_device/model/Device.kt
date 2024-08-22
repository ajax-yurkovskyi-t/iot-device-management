package com.example.iot_management_device.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "devices")
data class Device(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    @Column(nullable = false, unique = true)
    val name: String?,
    @Column(nullable = false)
    val description: String?,
    @Column(nullable = false)
    val type: String?,
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val statusType: DeviceStatusType,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User?
) {
    override fun toString(): String {
        return "Device(id=$id, name=$name, description=$description, type=$type, statusType=$statusType, userId=${user?.id})"
    }
}


enum class DeviceStatusType {
    ONLINE,
    OFFLINE
}
