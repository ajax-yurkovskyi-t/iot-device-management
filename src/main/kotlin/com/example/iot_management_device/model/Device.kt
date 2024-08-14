package com.example.iot_management_device.model

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
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
    val statusType: DeviceStatusType,) {

}

enum class DeviceStatusType {
    ONLINE,
    OFFLINE
}