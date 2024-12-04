package com.example.gateway.infrastructure.rest.mapper

import com.example.commonmodels.device.Device
import com.example.gateway.infrastructure.rest.dto.DeviceStatusType
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
abstract class EnumMapper {

    fun mapStatusType(statusType: Device.StatusType): DeviceStatusType {
        return when (statusType) {
            Device.StatusType.STATUS_TYPE_ONLINE -> DeviceStatusType.ONLINE
            Device.StatusType.STATUS_TYPE_OFFLINE, Device.StatusType.UNRECOGNIZED,
            Device.StatusType.STATUS_TYPE_UNSPECIFIED -> DeviceStatusType.OFFLINE
        }
    }
}
