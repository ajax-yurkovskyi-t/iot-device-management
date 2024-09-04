package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByName(name: String): User?
    fun findByEmail(email: String): User?
}
