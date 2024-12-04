package com.example.iotmanagementdevice.device.infrastructure.mongo.mapper

import com.example.iotmanagementdevice.device.domain.CreateDevice
import com.example.iotmanagementdevice.device.domain.Device
import com.example.iotmanagementdevice.device.infrastructure.mongo.entity.MongoDevice
import org.bson.types.ObjectId
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl",
)
abstract class DeviceMapper {
    abstract fun toDomain(mongoDevice: MongoDevice): Device

    abstract fun toEntity(device: Device): MongoDevice

    abstract fun toEntity(createDevice: CreateDevice): MongoDevice

    fun mapObjectIdToString(objectId: ObjectId): String {
        return objectId.toString()
    }

    fun mapStringIdToObjectId(stringId: String): ObjectId {
        return ObjectId(stringId)
    }
}
