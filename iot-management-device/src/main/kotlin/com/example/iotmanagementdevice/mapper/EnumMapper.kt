package com.example.iotmanagementdevice.mapper

import com.example.core.dto.DeviceStatusType
import com.example.internal.commonmodels.Device
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
            Device.StatusType.STATUS_TYPE_UNSPECIFIED -> DeviceStatusType.OFFLINE
            Device.StatusType.STATUS_TYPE_ONLINE -> DeviceStatusType.ONLINE
            Device.StatusType.STATUS_TYPE_OFFLINE -> DeviceStatusType.OFFLINE
            Device.StatusType.UNRECOGNIZED -> DeviceStatusType.OFFLINE
        }
    }
}
