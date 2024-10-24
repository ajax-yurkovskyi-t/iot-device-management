package com.example.iotmanagementdevice.config

import com.example.iotmanagementdevice.security.CustomUserDetailsService
import com.example.iotmanagementdevice.security.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val userDetailsService: CustomUserDetailsService,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    @Value("\${spring.security.public.endpoints}") private val publicEndpoints: Array<String>
) {
    @Bean
    fun getPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors { it.disable() }
            .csrf { it.disable() }
            .authorizeExchange { auth ->
                @Suppress("SpreadOperator")
                auth.pathMatchers(*publicEndpoints)
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
            .httpBasic { it.disable() }
            .authenticationManager(reactiveAuthenticationManager())
            .addFilterAt(
                jwtAuthenticationFilter,
                SecurityWebFiltersOrder.AUTHORIZATION
            )
            .build()
    }

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
        authenticationManager.setPasswordEncoder(getPasswordEncoder())
        return authenticationManager
    }
}
