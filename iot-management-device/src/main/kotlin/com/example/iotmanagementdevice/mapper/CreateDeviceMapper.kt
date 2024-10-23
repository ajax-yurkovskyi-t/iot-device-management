package com.example.iotmanagementdevice.mapper

import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ValueMapping

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
abstract class CreateDeviceMapper {
    @ValueMapping(source = "UNSPECIFIED", target = "OFFLINE")
    @ValueMapping(source = "UNRECOGNIZED", target = "OFFLINE")
    abstract fun toDeviceCreateRequestDto(request: CreateDeviceRequest): DeviceCreateRequestDto

    @Mapping(target = "success.device", source = "deviceResponseDto")
    abstract fun toCreateDeviceResponse(deviceResponseDto: DeviceResponseDto): CreateDeviceResponse

    fun toFailureCreateDeviceResponse(exception: Throwable): CreateDeviceResponse {
        return CreateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(exception.message.orEmpty())
        }.build()
    }
}
