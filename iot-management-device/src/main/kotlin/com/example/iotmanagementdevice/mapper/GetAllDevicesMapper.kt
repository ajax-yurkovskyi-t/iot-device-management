package com.example.iotmanagementdevice.mapper

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
    implementationPackage = "<PACKAGE_NAME>.impl"
)
abstract class GetAllDevicesMapper {
    abstract fun toProtoDevice(deviceResponseDto: DeviceResponseDto): Device

    fun toGetAllDevicesResponse(deviceResponseDtoList: List<DeviceResponseDto>): GetAllDevicesResponse {
        val successBuilder = GetAllDevicesResponse.Success.newBuilder()

        deviceResponseDtoList.forEach { dto ->
            successBuilder.addDevices(toProtoDevice(dto))
        }

        return GetAllDevicesResponse.newBuilder()
            .setSuccess(successBuilder.build())
            .build()
    }

    fun toFailureGetAllDevicesResponse(throwable: Throwable): GetAllDevicesResponse {
        return GetAllDevicesResponse.newBuilder().apply {
            failureBuilder.message = throwable.message.orEmpty()
        }.build()
    }
}
