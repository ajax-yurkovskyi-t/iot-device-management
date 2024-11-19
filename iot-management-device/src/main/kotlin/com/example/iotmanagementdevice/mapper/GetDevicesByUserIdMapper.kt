package com.example.iotmanagementdevice.mapper

import com.example.commonmodels.device.Device
import com.example.core.dto.response.DeviceResponseDto
import com.example.core.exception.EntityNotFoundException
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
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
abstract class GetDevicesByUserIdMapper {
    abstract fun toProtoDevice(deviceResponseDto: DeviceResponseDto): Device

    fun toGetDevicesByUserIdResponse(deviceResponseDtoList: List<DeviceResponseDto>): GetDevicesByUserIdResponse {
        val devicesProtoList = deviceResponseDtoList.map { dto -> toProtoDevice(dto) }

        return GetDevicesByUserIdResponse.newBuilder().apply {
            successBuilder.addAllDevices(devicesProtoList)
        }.build()
    }

    fun toFailure(throwable: Throwable): GetDevicesByUserIdResponse {
        return GetDevicesByUserIdResponse.newBuilder().apply {
            failureBuilder.message = throwable.message.orEmpty()
            when (throwable) {
                is EntityNotFoundException -> failureBuilder.userNotFoundBuilder
            }
        }.build()
    }
}
