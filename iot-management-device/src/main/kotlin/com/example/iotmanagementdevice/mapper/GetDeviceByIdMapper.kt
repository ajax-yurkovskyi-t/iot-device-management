package com.example.iotmanagementdevice.mapper

import com.example.core.dto.response.DeviceResponseDto
import com.example.core.exception.EntityNotFoundException
import com.example.internal.commonmodels.Error
import com.example.internal.input.reqreply.device.get_by_id.proto.GetDeviceByIdResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
abstract class GetDeviceByIdMapper {
    @Mapping(target = "success.device", source = "deviceResponseDto")
    abstract fun toGetDeviceByIdResponse(deviceResponseDto: DeviceResponseDto): GetDeviceByIdResponse

    fun toFailureGetDeviceByIdResponse(throwable: Throwable): GetDeviceByIdResponse {
        return GetDeviceByIdResponse.newBuilder().apply {
            failureBuilder.setMessage(throwable.message.orEmpty())
            when (throwable) {
                is EntityNotFoundException -> failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
            }
        }.build()
    }
}
