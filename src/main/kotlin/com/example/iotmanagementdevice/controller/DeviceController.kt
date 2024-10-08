package com.example.iotmanagementdevice.controller

import com.example.iotmanagementdevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotmanagementdevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.service.device.DeviceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/devices")
class DeviceController(private val deviceService: DeviceService) {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    fun getDeviceById(@PathVariable(name = "id") id: String): Mono<DeviceResponseDto> =
        deviceService.getById(id)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody requestDto: DeviceCreateRequestDto): Mono<DeviceResponseDto> =
        deviceService.create(requestDto)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody requestDto: DeviceUpdateRequestDto
    ): Mono<DeviceResponseDto> =
        deviceService.update(id, requestDto)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    fun getAll(): Flux<DeviceResponseDto> =
        deviceService.getAll()
}
