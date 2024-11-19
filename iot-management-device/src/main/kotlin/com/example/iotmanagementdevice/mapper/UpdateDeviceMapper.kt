package com.example.iotmanagementdevice.mapper

import com.example.core.dto.request.DeviceUpdateRequestDto
import com.example.core.dto.response.DeviceResponseDto
import com.example.core.exception.EntityNotFoundException
import com.example.internal.commonmodels.Error
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceRequest
import com.example.internal.input.reqreply.device.update.proto.UpdateDeviceResponse
import com.example.iotmanagementdevice.model.MongoDevice
import com.google.protobuf.Timestamp
import org.bson.types.ObjectId
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
abstract class UpdateDeviceMapper {

    abstract fun toDeviceUpdateRequestDto(updateDeviceRequest: UpdateDeviceRequest): DeviceUpdateRequestDto

    @Mapping(target = "success.device", source = "deviceResponseDto")
    abstract fun toUpdateDeviceResponse(deviceResponseDto: DeviceResponseDto): UpdateDeviceResponse

    @Mapping(target = "success.device", source = "mongoDevice")
    abstract fun toUpdateDeviceResponse(mongoDevice: MongoDevice): UpdateDeviceResponse

    fun toFailureUpdateDeviceResponse(throwable: Throwable): UpdateDeviceResponse {
        return UpdateDeviceResponse.newBuilder().apply {
            failureBuilder.setMessage(throwable.message.orEmpty())
            when (throwable) {
                is EntityNotFoundException -> failureBuilder.setDeviceNotFound(Error.getDefaultInstance())
            }
        }.build()
    }

    fun mapObjectIdToString(objectId: ObjectId): String {
        return objectId.toString()
    }

    fun mapInstantToTimestamp(instant: Instant): Timestamp {
        return Timestamp.newBuilder().apply {
            seconds = instant.epochSecond
            nanos = instant.nano
        }.build()
    }
}
