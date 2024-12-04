package com.example.gateway.infrastructure.rest.mapper

import com.example.commonmodels.device.Device
import com.example.core.exception.EntityNotFoundException
import com.example.gateway.infrastructure.rest.dto.request.DeviceUpdateRequestDto
import com.example.gateway.infrastructure.rest.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
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
abstract class UpdateDeviceMapper {
    @ValueMappings(
        ValueMapping(source = "ONLINE", target = "STATUS_TYPE_ONLINE"),
        ValueMapping(source = "OFFLINE", target = "STATUS_TYPE_OFFLINE"),
    )
    abstract fun toUpdateRequestProto(deviceUpdateRequestDto: DeviceUpdateRequestDto, id: String): UpdateDeviceRequest

    abstract fun toDeviceResponseDto(device: Device): DeviceResponseDto

    @Suppress("TooGenericExceptionThrown")
    fun toDto(response: UpdateDeviceResponse): DeviceResponseDto {
        return when (response.responseCase!!) {
            UpdateDeviceResponse.ResponseCase.SUCCESS -> toDeviceResponseDto(response.success.device)
            UpdateDeviceResponse.ResponseCase.FAILURE -> toFailure(response)
            UpdateDeviceResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    private fun toFailure(response: UpdateDeviceResponse): Nothing {
        val message = response.failure.message.orEmpty()
        throw when (response.failure.errorCase!!) {
            UpdateDeviceResponse.Failure.ErrorCase.DEVICE_NOT_FOUND -> EntityNotFoundException(message)
            UpdateDeviceResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }
}
