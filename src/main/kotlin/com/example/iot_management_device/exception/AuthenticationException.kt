package com.example.iot_management_device.exception

import org.springframework.security.authentication.BadCredentialsException

class AuthenticationException(message: String, ex: BadCredentialsException) : RuntimeException("$message ${ex.message}")
