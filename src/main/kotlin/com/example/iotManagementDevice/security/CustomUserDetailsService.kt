package com.example.iotManagementDevice.security

import com.example.iotManagementDevice.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails? {
        return userRepository.findByEmail(email)
    }
}
