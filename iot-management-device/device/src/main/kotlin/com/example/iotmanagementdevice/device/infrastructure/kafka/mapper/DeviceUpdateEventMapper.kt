package com.example.iotmanagementdevice.device.infrastructure.kafka.mapper

import com.example.internal.output.pubsub.device.DeviceUpdatedEvent
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
    implementationPackage = "<PACKAGE_NAME>.impl"
)
abstract class DeviceUpdateEventMapper {
    @Mapping(target = "device", source = "domainDevice")
    abstract fun toDeviceUpdatedEvent(domainDevice: Device): DeviceUpdatedEvent

    fun mapStatusType(statusType: Device.DeviceStatusType): com.example.commonmodels.device.Device.StatusType {
        return when (statusType) {
            Device.DeviceStatusType.ONLINE -> com.example.commonmodels.device.Device.StatusType.STATUS_TYPE_ONLINE
            Device.DeviceStatusType.OFFLINE -> com.example.commonmodels.device.Device.StatusType.STATUS_TYPE_OFFLINE
        }
    }

    fun mapInstantToTimestamp(instant: Instant): Timestamp {
        return Timestamp.newBuilder().apply {
            seconds = instant.epochSecond
            nanos = instant.nano
        }.build()
    }
}
