package com.example.iotmanagementdevice.mapper

import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.iotmanagementdevice.model.MongoDevice
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
interface DeviceMapper {
    fun toDto(device: MongoDevice): DeviceResponseDto

    fun toEntity(dto: DeviceCreateRequestDto): MongoDevice

    fun toEntity(dto: DeviceUpdateRequestDto): MongoDevice
}
