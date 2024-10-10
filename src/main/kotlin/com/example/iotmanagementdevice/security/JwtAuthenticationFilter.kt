package com.example.iotmanagementdevice.security

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request

        return getToken(request)?.takeIf { jwtUtil.isValidToken(it) }?.let { token ->
            val username = jwtUtil.getEmail(token)

            userDetailsService.findByUsername(username).flatMap { userDetails ->
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
            }.switchIfEmpty(chain.filter(exchange))
        } ?: chain.filter(exchange)
    }

    private fun getToken(request: ServerHttpRequest): String? {
        val bearerToken = request.headers.getFirst(HEADER)
        return bearerToken?.takeIf { it.isNotBlank() && it.startsWith(BEARER) }
            ?.substring(TOKEN_START_INDEX)
    }

    companion object {
        private const val HEADER = "Authorization"
        private const val BEARER = "Bearer "
        private const val TOKEN_START_INDEX = 7
    }
}
