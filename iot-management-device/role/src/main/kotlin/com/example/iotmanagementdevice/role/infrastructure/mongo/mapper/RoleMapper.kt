package com.example.iotmanagementdevice.role.infrastructure.mongo.mapper

import com.example.iotmanagementdevice.role.domain.CreateRole
import com.example.iotmanagementdevice.role.domain.Role
import com.example.iotmanagementdevice.role.infrastructure.mongo.entity.MongoRole
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
abstract class RoleMapper {
    abstract fun toDomain(mongoRole: MongoRole): Role
    abstract fun toEntity(role: Role): MongoRole
    abstract fun toEntity(role: CreateRole): MongoRole

    fun mapObjectIdToString(objectId: ObjectId): String {
        return objectId.toString()
    }

    fun mapStringIdToObjectId(stringId: String): ObjectId {
        return ObjectId(stringId)
    }
}
