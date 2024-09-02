package com.example.iotManagementDevice.mapper

import com.example.iotManagementDevice.dto.device.request.DeviceCreateRequestDto
import com.example.iotManagementDevice.dto.device.response.DeviceResponseDto
import com.example.iotManagementDevice.dto.device.request.DeviceUpdateRequestDto
import com.example.iotManagementDevice.model.Device
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl")
interface DeviceMapper {
    fun toDto(device: Device): DeviceResponseDto

    fun toEntity(dto: DeviceCreateRequestDto): Device

    fun toEntity(dto: DeviceUpdateRequestDto): Device
}
