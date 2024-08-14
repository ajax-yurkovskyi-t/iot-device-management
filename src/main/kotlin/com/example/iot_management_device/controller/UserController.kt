package com.example.iot_management_device.controller


import com.example.iot_management_device.model.User
import com.example.iot_management_device.service.user.UserServiceImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userServiceImpl: UserServiceImpl) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody user: User): User =
        userServiceImpl.register(user);
}