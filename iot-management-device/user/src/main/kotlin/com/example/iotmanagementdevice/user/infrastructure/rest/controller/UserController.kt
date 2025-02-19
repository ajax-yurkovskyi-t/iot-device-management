package com.example.iotmanagementdevice.user.infrastructure.rest.controller

import com.example.iotmanagementdevice.user.application.port.input.UserServiceInPort
import com.example.iotmanagementdevice.user.infrastructure.rest.dto.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.user.infrastructure.rest.dto.response.UserResponseDto
import com.example.iotmanagementdevice.user.infrastructure.rest.mapper.UserDtoMapper
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(
    private val userServiceInPort: UserServiceInPort,
    private val userDtoMapper: UserDtoMapper,
) {

    @PostMapping("/{userId}/assign/{deviceId}")
    fun assignDeviceToUser(
        @PathVariable userId: String,
        @PathVariable deviceId: String,
    ): Mono<Boolean> {
        return userServiceInPort.assignDeviceToUser(userId, deviceId)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): Mono<UserResponseDto> {
        return userServiceInPort.getUserById(id)
            .map { userDtoMapper.toDto(it) }
    }

    @PutMapping("{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody requestDto: UserUpdateRequestDto
    ): Mono<UserResponseDto> {
        return userServiceInPort.update(id, userDtoMapper.toDomain(requestDto))
            .map { userDtoMapper.toDto(it) }
    }

    @GetMapping
    fun getUserByUsername(@RequestParam username: String): Mono<UserResponseDto> {
        return userServiceInPort.getUserByUsername(username)
            .map { userDtoMapper.toDto(it) }
    }

    @GetMapping("/all")
    fun getAll(): Flux<UserResponseDto> {
        return userServiceInPort.getAll()
            .map { userDtoMapper.toDto(it) }
    }
}
