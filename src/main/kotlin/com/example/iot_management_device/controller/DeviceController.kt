package com.example.iot_management_device.controller

import com.example.iot_management_device.dto.device.request.DeviceCreateRequestDto
import com.example.iot_management_device.dto.device.response.DeviceResponseDto
import com.example.iot_management_device.dto.device.request.DeviceUpdateRequestDto
import com.example.iot_management_device.service.device.DeviceService
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

@RestController
@RequestMapping("/devices")
class DeviceController(private val deviceService: DeviceService) {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    fun getDeviceById(@PathVariable(name = "id") id:Long): DeviceResponseDto =
        deviceService.getById(id)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody requestDto: DeviceCreateRequestDto): DeviceResponseDto =
        deviceService.create(requestDto)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody requestDto: DeviceUpdateRequestDto
    ): DeviceResponseDto =
        deviceService.update(id, requestDto)

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    fun getAll(): List<DeviceResponseDto> =
        deviceService.getAll()
}
