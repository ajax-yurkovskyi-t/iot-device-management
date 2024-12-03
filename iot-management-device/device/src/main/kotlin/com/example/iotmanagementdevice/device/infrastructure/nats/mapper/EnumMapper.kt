package com.example.iotmanagementdevice.device.infrastructure.nats.mapper

import com.example.commonmodels.device.Device
import org.mapstruct.Mapper
import com.example.iotmanagementdevice.device.domain.Device as DomainDevice

@Mapper(componentModel = "spring")
abstract class EnumMapper {

    fun mapStatusType(statusType: DomainDevice.DeviceStatusType): Device.StatusType {
        return when (statusType) {
            DomainDevice.DeviceStatusType.ONLINE -> Device.StatusType.STATUS_TYPE_ONLINE
            DomainDevice.DeviceStatusType.OFFLINE ->
                Device.StatusType.STATUS_TYPE_OFFLINE
        }
    }

    fun mapStatusType(statusType: Device.StatusType): DomainDevice.DeviceStatusType {
        return when (statusType) {
            Device.StatusType.STATUS_TYPE_ONLINE -> DomainDevice.DeviceStatusType.ONLINE
            Device.StatusType.STATUS_TYPE_UNSPECIFIED, Device.StatusType.STATUS_TYPE_OFFLINE,
            Device.StatusType.UNRECOGNIZED -> DomainDevice.DeviceStatusType.OFFLINE
        }
    }
}
