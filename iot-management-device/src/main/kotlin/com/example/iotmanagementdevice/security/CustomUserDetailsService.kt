package com.example.iotmanagementdevice.security

import com.example.iotmanagementdevice.mapper.UserMapper
import com.example.iotmanagementdevice.repository.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper
) : ReactiveUserDetailsService {

    override fun findByUsername(email: String): Mono<UserDetails> {
        return userRepository.findByUserEmail(email)
            .map { userMapper.toSecurityUser(it) }
    }
}
