package com.example.gateway.mapper

import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.core.exception.EntityNotFoundException
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
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
abstract class UpdateDeviceMapper {
    abstract fun toUpdateRequestProto(deviceUpdateRequestDto: DeviceUpdateRequestDto, id: String): UpdateDeviceRequest

    @Mapping(target = "name", source = "success.device.name")
    @Mapping(target = "description", source = "success.device.description")
    @Mapping(target = "type", source = "success.device.type")
    @Mapping(target = "statusType", source = "success.device.statusType")
    abstract fun toSuccess(response: UpdateDeviceResponse): DeviceResponseDto

    fun toDto(response: UpdateDeviceResponse): DeviceResponseDto {
        return when (response.responseCase!!) {
            UpdateDeviceResponse.ResponseCase.SUCCESS -> toSuccess(response)
            UpdateDeviceResponse.ResponseCase.FAILURE -> toFailure(response)
            UpdateDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    private fun toFailure(response: UpdateDeviceResponse): Nothing {
        val message = response.failure.message.orEmpty()
        when (response.failure.errorCase!!) {
            UpdateDeviceResponse.Failure.ErrorCase.DEVICE_NOT_FOUND -> throw EntityNotFoundException(message)
            UpdateDeviceResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }
}

