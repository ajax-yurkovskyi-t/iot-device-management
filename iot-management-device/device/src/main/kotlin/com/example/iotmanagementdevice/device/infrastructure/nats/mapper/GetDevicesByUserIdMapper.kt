package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.commonmodels.device.Device
import com.example.core.exception.EntityNotFoundException
import com.example.internal.input.reqreply.device.get_by_user_id.proto.GetDevicesByUserIdResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import com.example.iotmanagementdevice.device.domain.Device as DomainDevice

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = [InstantToTimestampMapper::class, EnumMapper::class]
)
abstract class GetDevicesByUserIdMapper {
    abstract fun toProtoDevice(device: DomainDevice): Device

    fun toGetDevicesByUserIdResponse(deviceResponseDtoList: List<DomainDevice>): GetDevicesByUserIdResponse {
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
