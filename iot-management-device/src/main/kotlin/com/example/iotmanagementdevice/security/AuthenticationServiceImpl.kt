package com.example.iotmanagementdevice.security

import com.example.core.exception.AuthenticationException
import com.example.iotmanagementdevice.dto.user.request.UserLoginRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserLoginResponseDto
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationServiceImpl(
    private val jwtUtil: JwtUtil,
    private val authenticationManager: ReactiveAuthenticationManager,
) : AuthenticationService {

    override fun authenticate(requestDto: UserLoginRequestDto): Mono<UserLoginResponseDto> {
        val authenticationToken = UsernamePasswordAuthenticationToken(
            requestDto.email,
            requestDto.userPassword
        )

        return authenticationManager.authenticate(authenticationToken)
            .map { authentication ->
                val securityUser = authentication.principal as SecurityUser
                val token = jwtUtil.generateToken(securityUser.username)
                UserLoginResponseDto(token)
            }
            .onErrorMap(BadCredentialsException::class.java) { ex ->
                AuthenticationException(LOGIN_ERROR_MESSAGE, ex)
            }
    }

    companion object {
        private const val LOGIN_ERROR_MESSAGE = "Can't log in: "
    }
}
