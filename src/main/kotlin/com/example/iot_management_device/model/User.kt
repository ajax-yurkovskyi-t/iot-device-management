package com.example.iot_management_device.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false, unique = true)
    val phoneNumber: String,
    @Column(nullable = false)
    val password: String,
)