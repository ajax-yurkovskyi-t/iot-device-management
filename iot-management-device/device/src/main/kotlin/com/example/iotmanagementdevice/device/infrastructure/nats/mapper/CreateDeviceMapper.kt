package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import com.example.iotmanagementdevice.device.domain.CreateDevice
import com.example.iotmanagementdevice.device.domain.Device
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = [InstantToTimestampMapper::class, EnumMapper::class]
)
abstract class CreateDeviceMapper {
    abstract fun toCreateDevice(request: CreateDeviceRequest): CreateDevice

    @Mapping(target = "success.device", source = "device")
    abstract fun toCreateDeviceResponse(device: Device): CreateDeviceResponse

    fun toFailureCreateDeviceResponse(exception: Throwable): CreateDeviceResponse {
        return CreateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(exception.message.orEmpty())
        }.build()
    }
}
