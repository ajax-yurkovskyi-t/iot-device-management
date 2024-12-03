package com.example.iotmanagementdevice.user.infrastructure.mongo.mapper

import com.example.iotmanagementdevice.user.domain.User
import com.example.iotmanagementdevice.user.infrastructure.mongo.entity.MongoUser
import org.bson.types.ObjectId
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
abstract class UserMapper {
    abstract fun toDomain(mongoUser: MongoUser): User
    abstract fun toEntity(user: User): MongoUser

    fun mapObjectIdToString(objectId: ObjectId): String {
        return objectId.toString()
    }

    fun mapStringIdToObjectId(stringId: String): ObjectId {
        return ObjectId(stringId)
    }
}
