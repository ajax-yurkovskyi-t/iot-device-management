package com.example.iotManagementDevice.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        getToken(request)?.let { token ->
            if (jwtUtil.isValidToken(token)) {
                val username = jwtUtil.getEmail(token)
                val userDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(HEADER)
        return bearerToken?.takeIf { StringUtils.hasText(it) && it.startsWith(BEARER) }
            ?.substring(TOKEN_START_INDEX)
    }

    companion object {
        private const val HEADER = "Authorization"
        private const val BEARER = "Bearer "
        private const val TOKEN_START_INDEX = 7
    }
}
