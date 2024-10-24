package com.example.gateway.mapper

import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.commonmodels.Device
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    uses = [EnumMapper::class]
)
abstract class GetAllDevicesMapper {
    abstract fun toDeviceResponseDtoList(devicesList: List<Device>): List<DeviceResponseDto>

    fun toDto(response: GetAllDevicesResponse): List<DeviceResponseDto> {
        val message = response.failure.message.orEmpty()
        return when (response.responseCase!!) {
            GetAllDevicesResponse.ResponseCase.SUCCESS -> toDeviceResponseDtoList(response.success.devicesList)
            GetAllDevicesResponse.ResponseCase.FAILURE -> error(message)
            GetAllDevicesResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("No response case set")
        }
    }
}
