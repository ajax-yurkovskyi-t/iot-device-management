package com.example.iotmanagementdevice.security

import com.example.iotmanagementdevice.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails? {
        return userRepository.findByUserEmail(email)
    }
}
