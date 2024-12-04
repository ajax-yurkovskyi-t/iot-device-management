package com.example.gateway.infrastructure.rest.mapper

import com.example.commonmodels.device.Device
import com.example.gateway.infrastructure.rest.dto.request.DeviceCreateRequestDto
import com.example.gateway.infrastructure.rest.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ValueMapping
import org.mapstruct.ValueMappings

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = [EnumMapper::class]
)
abstract class CreateDeviceMapper {
    @ValueMappings(
        ValueMapping(source = "ONLINE", target = "STATUS_TYPE_ONLINE"),
        ValueMapping(source = "OFFLINE", target = "STATUS_TYPE_OFFLINE"),
    )
    abstract fun toCreateRequestProto(dto: DeviceCreateRequestDto): CreateDeviceRequest

    abstract fun toDeviceResponseDto(device: Device): DeviceResponseDto

    @Suppress("TooGenericExceptionThrown")
    fun toDto(response: CreateDeviceResponse): DeviceResponseDto {
        val message = response.failure.message.orEmpty()
        return when (response.responseCase!!) {
            CreateDeviceResponse.ResponseCase.SUCCESS -> toDeviceResponseDto(response.success.device)
            CreateDeviceResponse.ResponseCase.FAILURE -> error(message)
            CreateDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }
}
