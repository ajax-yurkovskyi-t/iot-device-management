package com.example.iotmanagementdevice.controller

import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import com.example.iotmanagementdevice.security.SecurityUser
import com.example.iotmanagementdevice.service.user.UserService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): UserResponseDto {
        val userId = extractUserId(authentication)
        return userService.getUserById(userId)
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{deviceId}/assign")
    fun assignDeviceToUser(
        authentication: Authentication,
        @PathVariable deviceId: String
    ): Boolean {
        val userId = extractUserId(authentication)
        return userService.assignDeviceToUser(userId, deviceId)
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/devices")
    fun getUserDevices(
        authentication: Authentication
    ): List<DeviceResponseDto> {
        val userId = extractUserId(authentication)
        return userService.getDevicesByUserId(userId)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): UserResponseDto =
        userService.getUserById(id)

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody requestDto: UserUpdateRequestDto
    ): UserResponseDto =
        userService.update(id, requestDto)

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getUserByUsername(@RequestParam username: String): UserResponseDto =
        userService.getUserByUsername(username)

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    fun getAll(): List<UserResponseDto> =
        userService.getAll()

    private fun extractUserId(authentication: Authentication): String {
        val securityUser = authentication.principal as? SecurityUser
            ?: throw IllegalArgumentException("Authentication principal is not of type SecurityUser")

        return securityUser.id ?: throw IllegalArgumentException("User ID cannot be null")
    }
}
