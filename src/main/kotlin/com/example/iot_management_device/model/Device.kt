package com.example.iot_management_device.model

import jakarta.persistence.*

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
    val type: String?,) {
}