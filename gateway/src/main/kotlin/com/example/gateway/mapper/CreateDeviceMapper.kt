package com.example.gateway.mapper

import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ValueMapping

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = [EnumMapper::class]
)
abstract class CreateDeviceMapper {
    abstract fun toCreateRequestProto(dto: DeviceCreateRequestDto): CreateDeviceRequest

    @Mapping(target = "name", source = "success.device.name")
    @Mapping(target = "description", source = "success.device.description")
    @Mapping(target = "type", source = "success.device.type")
    @Mapping(target = "statusType", source = "success.device.statusType")
    abstract fun toSuccess(createDeviceResponse: CreateDeviceResponse): DeviceResponseDto

    fun toDto(response: CreateDeviceResponse): DeviceResponseDto {
        val message = response.failure.message.orEmpty()
        return when (response.responseCase!!) {
            CreateDeviceResponse.ResponseCase.SUCCESS -> toSuccess(response)
            CreateDeviceResponse.ResponseCase.FAILURE -> error(message)
            CreateDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }
}
