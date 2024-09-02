package com.example.iotManagementDevice.repository

import com.example.iotManagementDevice.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
    fun findByEmail(email: String): User?
}
