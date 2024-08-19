package com.example.iot_management_device.repository

import com.example.iot_management_device.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
    fun findByEmail(email: String): User?
}
