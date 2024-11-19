package com.example.iotmanagementdevice.mapper

import com.example.commonmodels.device.Device
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.get_all.proto.GetAllDevicesResponse
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
abstract class GetAllDevicesMapper {

    abstract fun toProtoDevice(deviceResponseDto: DeviceResponseDto): Device

    fun toGetAllDevicesResponse(deviceResponseDtoList: List<DeviceResponseDto>): GetAllDevicesResponse {
        val devicesProtoList = deviceResponseDtoList.map { dto -> toProtoDevice(dto) }

        val successBuilder = GetAllDevicesResponse.Success.newBuilder()
            .addAllDevices(devicesProtoList)

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
