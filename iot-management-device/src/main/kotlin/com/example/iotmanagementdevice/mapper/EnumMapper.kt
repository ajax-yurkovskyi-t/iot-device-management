package com.example.iotmanagementdevice.mapper

import com.example.commonmodels.device.Device
import com.example.core.dto.DeviceStatusType
import com.example.iotmanagementdevice.model.MongoDevice
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
abstract class EnumMapper {

    fun mapStatusType(statusType: DeviceStatusType): Device.StatusType {
        return when (statusType) {
            DeviceStatusType.ONLINE -> Device.StatusType.STATUS_TYPE_ONLINE
            DeviceStatusType.OFFLINE -> Device.StatusType.STATUS_TYPE_OFFLINE
        }
    }

    fun mapStatusType(statusType: Device.StatusType): DeviceStatusType {
        return when (statusType) {
            Device.StatusType.STATUS_TYPE_ONLINE -> DeviceStatusType.ONLINE
            Device.StatusType.STATUS_TYPE_UNSPECIFIED, Device.StatusType.STATUS_TYPE_OFFLINE,
            Device.StatusType.UNRECOGNIZED -> DeviceStatusType.OFFLINE
        }
    }

    fun mapStatusType(statusType: MongoDevice.DeviceStatusType): Device.StatusType {
        return when (statusType) {
            MongoDevice.DeviceStatusType.ONLINE -> Device.StatusType.STATUS_TYPE_ONLINE
            MongoDevice.DeviceStatusType.OFFLINE -> Device.StatusType.STATUS_TYPE_OFFLINE
        }
    }
}
