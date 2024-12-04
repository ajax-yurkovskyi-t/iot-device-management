package com.example.gateway.infrastructure.rest.mapper

import com.example.commonmodels.device.Device
import com.example.core.exception.EntityNotFoundException
import com.example.gateway.infrastructure.rest.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
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
abstract class GetDeviceByIdMapper {
    abstract fun toDeviceResponseDto(device: Device): DeviceResponseDto

    @Suppress("TooGenericExceptionThrown")
    fun toDto(response: GetDeviceByIdResponse): DeviceResponseDto {
        return when (response.responseCase!!) {
            GetDeviceByIdResponse.ResponseCase.SUCCESS -> toDeviceResponseDto(response.success.device)
            GetDeviceByIdResponse.ResponseCase.FAILURE -> toFailure(response)
            GetDeviceByIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }

    private fun toFailure(response: GetDeviceByIdResponse): Nothing {
        val message = response.failure.message.orEmpty()
        throw when (response.failure.errorCase!!) {
            GetDeviceByIdResponse.Failure.ErrorCase.DEVICE_NOT_FOUND -> EntityNotFoundException(message)
            GetDeviceByIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }
}
