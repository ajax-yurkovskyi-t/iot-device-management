package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.commonmodels.Error
import com.example.core.exception.EntityNotFoundException
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
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
abstract class UpdateDeviceMapper {

    abstract fun toDomain(updateDeviceRequest: UpdateDeviceRequest): Device

    @Mapping(target = "success.device", source = "device")
    abstract fun toUpdateDeviceResponse(device: Device): UpdateDeviceResponse

    fun toFailureUpdateDeviceResponse(throwable: Throwable): UpdateDeviceResponse {
        return UpdateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(throwable.message.orEmpty())
            when (throwable) {
                is EntityNotFoundException -> failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
            }
        }.build()
    }
}
