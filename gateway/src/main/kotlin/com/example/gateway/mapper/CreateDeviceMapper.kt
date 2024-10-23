package com.example.gateway.mapper

import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.commonmodels.Device
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = [EnumMapper::class]
)
abstract class CreateDeviceMapper {
    abstract fun toCreateRequestProto(dto: DeviceCreateRequestDto): CreateDeviceRequest

    abstract fun toDeviceResponseDto(device: Device): DeviceResponseDto

    fun toDto(response: CreateDeviceResponse): DeviceResponseDto {
        val message = response.failure.message.orEmpty()
        return when (response.responseCase!!) {
            CreateDeviceResponse.ResponseCase.SUCCESS -> toDeviceResponseDto(response.success.device)
            CreateDeviceResponse.ResponseCase.FAILURE -> error(message)
            CreateDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }
}
