package com.example.iotmanagementdevice.security

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class JwtAuthenticationFilterTest {

    @MockK
    private lateinit var jwtUtil: JwtUtil

    @MockK
    private lateinit var userDetailsService: CustomUserDetailsService

    @MockK
    private lateinit var request: ServerHttpRequest

    @MockK
    private lateinit var exchange: ServerWebExchange

    @MockK
    private lateinit var filterChain: WebFilterChain

    @MockK
    private lateinit var userDetails: UserDetails

    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        jwtAuthenticationFilter = JwtAuthenticationFilter(jwtUtil, userDetailsService)

        every { exchange.request } returns request
        every { filterChain.filter(exchange) } returns Mono.empty()
    }

    @Test
    fun `should not authenticate when Authorization header is null`() {
        // GIVEN
        every { request.headers.getFirst(AUTHORIZATION) } returns null

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .verifyComplete()

        verify { filterChain.filter(exchange) }
    }

    @Test
    fun `should not authenticate when Authorization header does not start with Bearer`() {
        // GIVEN
        every { request.headers.getFirst(AUTHORIZATION) } returns "InvalidToken"

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .verifyComplete()

        verify { filterChain.filter(exchange) }
    }

    @Test
    fun `should authenticate when valid token is provided`() {
        // GIVEN
        every { request.headers.getFirst(AUTHORIZATION) } returns VALID_TOKEN
        every { jwtUtil.isValidToken(VALID_TOKEN.substring(7)) } returns true
        every { jwtUtil.getEmail(VALID_TOKEN.substring(7)) } returns USERNAME
        every { userDetailsService.findByUsername(USERNAME) } returns userDetails.toMono()
        every { userDetails.authorities } returns listOf()

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .verifyComplete()

        verify { filterChain.filter(exchange) }
    }

    @Test
    fun `should not authenticate when invalid token is provided`() {
        // GIVEN
        every { request.headers.getFirst(AUTHORIZATION) } returns INVALID_TOKEN
        every { jwtUtil.isValidToken(INVALID_TOKEN.substring(7)) } returns false

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .verifyComplete()

        verify { filterChain.filter(exchange) }
    }

    @Test
    fun `should not authenticate when Authorization header is blank`() {
        // GIVEN
        every { request.headers.getFirst(AUTHORIZATION) } returns BLANK_TOKEN

        // WHEN
        val result = jwtAuthenticationFilter.filter(exchange, filterChain)

        // THEN
        result.test()
            .verifyComplete()

        verify { filterChain.filter(exchange) }
    }

    companion object {
        private const val VALID_TOKEN = "Bearer valid.token"
        private const val INVALID_TOKEN = "Bearer invalid.token"
        private const val BLANK_TOKEN = ""
        private const val USERNAME = "test@example.com"
        private const val AUTHORIZATION = "Authorization"
    }
}
