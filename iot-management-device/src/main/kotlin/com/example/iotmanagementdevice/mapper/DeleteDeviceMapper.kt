package com.example.iotmanagementdevice.mapper

import com.example.internal.input.reqreply.device.delete.proto.DeleteDeviceResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
abstract class DeleteDeviceMapper {
    fun toSuccessDeleteResponse(): DeleteDeviceResponse {
        return DeleteDeviceResponse.newBuilder().apply {
            successBuilder
        }.build()
    }

    fun toFailureDeleteDeviceResponse(throwable: Throwable): DeleteDeviceResponse {
        val message = throwable.message.orEmpty()
        return DeleteDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(message)
        }.build()
    }
}
