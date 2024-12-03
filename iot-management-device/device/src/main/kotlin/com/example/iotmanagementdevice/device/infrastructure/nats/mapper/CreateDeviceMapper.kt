package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
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
abstract class CreateDeviceMapper {
    abstract fun toDomain(request: CreateDeviceRequest): Device

    @Mapping(target = "success.device", source = "device")
    abstract fun toCreateDeviceResponse(device: Device): CreateDeviceResponse

    fun toFailureCreateDeviceResponse(exception: Throwable): CreateDeviceResponse {
        return CreateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(exception.message.orEmpty())
        }.build()
    }

    fun mapInstantToTimestamp(instant: Instant): Timestamp {
        return Timestamp.newBuilder().apply {
            seconds = instant.epochSecond
            nanos = instant.nano
        }.build()
    }
}
