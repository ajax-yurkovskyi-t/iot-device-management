package com.example.iotmanagementdevice.mapper

import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.core.exception.EntityNotFoundException
import com.example.internal.commonmodels.Error
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ValueMapping

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
    uses = [EnumMapper::class]
)
abstract class UpdateDeviceMapper {

    @ValueMapping(source = "STATUS_TYPE_ONLINE", target = "ONLINE")
    @ValueMapping(source = "STATUS_TYPE_OFFLINE", target = "OFFLINE")
    @ValueMapping(source = "STATUS_TYPE_UNSPECIFIED", target = "OFFLINE")
    @ValueMapping(source = "UNRECOGNIZED", target = "OFFLINE")
    abstract fun toDeviceUpdateRequestDto(updateDeviceRequest: UpdateDeviceRequest): DeviceUpdateRequestDto

    @Mapping(target = "success.device", source = "deviceResponseDto")
    abstract fun toUpdateDeviceResponse(deviceResponseDto: DeviceResponseDto): UpdateDeviceResponse

    fun toFailureUpdateDeviceResponse(throwable: Throwable): UpdateDeviceResponse {
        return UpdateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(throwable.message.orEmpty())
            when (throwable) {
                is EntityNotFoundException -> failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
            }
        }.build()
    }
}
