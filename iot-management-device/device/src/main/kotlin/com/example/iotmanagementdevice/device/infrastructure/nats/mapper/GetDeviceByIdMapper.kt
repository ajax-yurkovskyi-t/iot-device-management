package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.core.exception.EntityNotFoundException
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import com.example.iotmanagementdevice.device.domain.Device
import com.google.protobuf.Timestamp
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import java.time.Instant

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = [EnumMapper::class]
)
abstract class GetDeviceByIdMapper {

    @Mapping(target = "success.device", source = "device")
    abstract fun toGetDeviceByIdResponse(device: Device): GetDeviceByIdResponse

    fun toFailureGetDeviceByIdResponse(throwable: Throwable): GetDeviceByIdResponse {
        return GetDeviceByIdResponse.newBuilder().apply {
            failureBuilder.setMessage(throwable.message.orEmpty())
            when (throwable) {
                is EntityNotFoundException -> failureBuilder.deviceNotFoundBuilder
            }
        }.build()
    }

    fun mapInstantToTimestamp(instant: Instant): Timestamp {
        return Timestamp.newBuilder().apply {
            seconds = instant.epochSecond
            nanos = instant.nano
        }.build()
    }
}
