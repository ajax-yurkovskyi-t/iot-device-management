package com.example.iotmanagementdevice.security

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JwtAuthenticationFilterTest {

    @MockK
    private lateinit var jwtUtil: JwtUtil

    @MockK
    private lateinit var userDetailsService: CustomUserDetailsService

    @MockK
    private lateinit var request: HttpServletRequest

    @MockK
    private lateinit var response: HttpServletResponse

    @MockK
    private lateinit var filterChain: FilterChain

    @MockK
    private lateinit var userDetails: UserDetails

    @InjectMockKs
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should not authenticate when Authorization header is null`() {
        // GIVEN
        every { request.getHeader("Authorization") } returns null
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        invokeDoFilterInternal()

        // THEN
        verify { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `should not authenticate when Authorization header does not start with Bearer`() {
        // GIVEN
        every { request.getHeader(AUTHORIZATION) } returns "InvalidToken"
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        invokeDoFilterInternal()

        // THEN
        assertNull(SecurityContextHolder.getContext().authentication)
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should authenticate when valid token is provided`() {
        // GIVEN
        every { request.getHeader("Authorization") } returns VALID_TOKEN
        every { jwtUtil.isValidToken(VALID_TOKEN.substring(7)) } returns true
        every { jwtUtil.getEmail(VALID_TOKEN.substring(7)) } returns USERNAME
        every { userDetailsService.loadUserByUsername(USERNAME) } returns userDetails
        every { userDetails.authorities } returns listOf()
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        invokeDoFilterInternal()

        // THEN
        assertNotNull(SecurityContextHolder.getContext().authentication)
        verify { filterChain.doFilter(request, response) }
    }

    @Test
    fun `should not authenticate when invalid token is provided`() {
        // GIVEN
        every { request.getHeader(AUTHORIZATION) } returns INVALID_TOKEN
        every { jwtUtil.isValidToken(INVALID_TOKEN.substring(7)) } returns false
        every { filterChain.doFilter(request, response) } just Runs

        // WHEN
        invokeDoFilterInternal()

        // THEN
        assertNull(SecurityContextHolder.getContext().authentication)
        verify { filterChain.doFilter(request, response) }
    }

    private fun invokeDoFilterInternal() {
        val method = JwtAuthenticationFilter::class.declaredFunctions.find { it.name == "doFilterInternal" }
        method?.isAccessible = true
        method?.call(jwtAuthenticationFilter, request, response, filterChain)
    }

    companion object {
        private const val VALID_TOKEN = "Bearer valid.token"
        private const val INVALID_TOKEN = "Bearer invalid.token"
        private const val USERNAME = "test@example.com"
        private const val AUTHORIZATION = "Authorization"
    }
}
