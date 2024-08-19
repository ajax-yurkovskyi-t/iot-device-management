package com.example.iot_management_device.mapper

import com.example.iot_management_device.dto.device.request.DeviceRequestDto
import com.example.iot_management_device.dto.device.response.DeviceResponseDto
import com.example.iot_management_device.dto.device.request.DeviceUpdateRequestDto
import com.example.iot_management_device.model.Device
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl")
interface DeviceMapper {
    fun toDto(device: Device): DeviceResponseDto

    fun toEntity(dto: DeviceRequestDto): Device

    fun toEntity(dto: DeviceUpdateRequestDto): Device
}
