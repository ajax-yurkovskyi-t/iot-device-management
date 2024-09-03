package com.example.iotmanagementdevice.security

import com.example.iotmanagementdevice.beanpostprocessor.MethodAttemptLimiter
import com.example.iotmanagementdevice.dto.user.request.UserLoginRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserLoginResponseDto
import com.example.iotmanagementdevice.exception.AuthenticationException
import com.example.iotmanagementdevice.model.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceImpl(
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
) : AuthenticationService {

    @MethodAttemptLimiter(maxAttempts = 3, lockoutDurationMillis = 600)
    override fun authenticate(requestDto: UserLoginRequestDto): UserLoginResponseDto {
        return try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    requestDto.email,
                    requestDto.userPassword
                )
            )

            val user = authentication.principal as User
            val token = jwtUtil.generateToken(user.email)
            UserLoginResponseDto(token)
        } catch (ex: BadCredentialsException) {
            throw AuthenticationException(LOGIN_ERROR_MESSAGE, ex)
        }
    }

    companion object {
        private const val LOGIN_ERROR_MESSAGE = "Can't log in: "
    }
}
