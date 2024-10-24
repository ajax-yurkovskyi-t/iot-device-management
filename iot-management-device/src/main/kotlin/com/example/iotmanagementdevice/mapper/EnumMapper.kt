package com.example.iotmanagementdevice.mapper

import com.example.core.dto.DeviceStatusType
import com.example.internal.commonmodels.Device
import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import org.mapstruct.ValueMappings

@Mapper(componentModel = "spring")
interface EnumMapper {

    @ValueMappings(
        ValueMapping(source = "ONLINE", target = "STATUS_TYPE_ONLINE"),
        ValueMapping(source = "OFFLINE", target = "STATUS_TYPE_OFFLINE"),
    )
    fun mapStatusType(statusType: DeviceStatusType): Device.StatusType
}
