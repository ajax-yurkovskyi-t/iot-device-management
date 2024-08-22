package com.example.iot_management_device.security

import com.example.iot_management_device.dto.user.request.UserLoginRequestDto
import com.example.iot_management_device.dto.user.response.UserLoginResponseDto
import com.example.iot_management_device.exception.AuthenticationException
import com.example.iot_management_device.model.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager) {

    fun authenticate(requestDto: UserLoginRequestDto): UserLoginResponseDto {
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
