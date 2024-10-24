package com.example.gateway.mapper

import com.example.core.dto.DeviceStatusType
import com.example.internal.commonmodels.Device
import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import org.mapstruct.ValueMappings

@Mapper(componentModel = "spring")
interface EnumMapper {

    @ValueMappings(
        ValueMapping(source = "STATUS_TYPE_ONLINE", target = "ONLINE"),
        ValueMapping(source = "STATUS_TYPE_OFFLINE", target = "OFFLINE"),
        ValueMapping(source = "STATUS_TYPE_UNSPECIFIED", target = "OFFLINE"),
        ValueMapping(source = "UNRECOGNIZED", target = "OFFLINE"),
    )
    fun mapStatusType(statusType: Device.StatusType): DeviceStatusType
}