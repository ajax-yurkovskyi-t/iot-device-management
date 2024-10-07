package com.example.iotmanagementdevice.security

import com.example.iotmanagementdevice.beanpostprocessor.MethodAttemptLimiter
import com.example.iotmanagementdevice.dto.user.request.UserLoginRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserLoginResponseDto
import com.example.iotmanagementdevice.exception.AuthenticationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthenticationServiceImpl(
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
) : AuthenticationService {

    @MethodAttemptLimiter(maxAttempts = 3, lockoutDurationMillis = 600)
    override fun authenticate(requestDto: UserLoginRequestDto): Mono<UserLoginResponseDto> {
        return Mono.fromCallable {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    requestDto.email,
                    requestDto.userPassword
                )
            )
        }.map { authentication ->
            val securityUser = authentication.principal as SecurityUser
            val token = jwtUtil.generateToken(securityUser.username)
            UserLoginResponseDto(token)
        }.onErrorMap(BadCredentialsException::class.java) { ex ->
            AuthenticationException(LOGIN_ERROR_MESSAGE, ex)
        }
    }

    companion object {
        private const val LOGIN_ERROR_MESSAGE = "Can't log in: "
    }
}
