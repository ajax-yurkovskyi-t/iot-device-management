package com.example.iotmanagementdevice.mapper

import com.example.core.dto.DeviceStatusType
import com.example.core.dto.request.DeviceCreateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceRequest
import com.example.internal.input.reqreply.device.create.proto.CreateDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ValueMapping

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
abstract class CreateDeviceMapper {
    abstract fun toDeviceCreateRequestDto(request: CreateDeviceRequest) : DeviceCreateRequestDto

    @Mapping(target = "success", source = "deviceResponseDto")
    abstract fun toCreateDeviceResponse(deviceResponseDto: DeviceResponseDto): CreateDeviceResponse

    @Mapping(target = "device", source = "deviceResponseDto")
    abstract fun toSuccess(deviceResponseDto: DeviceResponseDto): CreateDeviceResponse.Success

    @ValueMapping(source = "ONLINE", target = "ONLINE")
    @ValueMapping(source = "OFFLINE", target = "OFFLINE")
    @ValueMapping(source = "UNRECOGNIZED", target = "OFFLINE")
    @ValueMapping(source = "UNSPECIFIED", target = "OFFLINE")
    abstract fun mapStatusType(statusType: CreateDeviceRequest.StatusType): DeviceStatusType

    fun toErrorResponse(exception: Throwable): CreateDeviceResponse {
        return CreateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(exception.message.orEmpty())
        }.build()
    }
}
