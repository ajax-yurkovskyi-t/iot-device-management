package com.example.iotmanagementdevice.mapper

import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import com.example.iotmanagementdevice.model.MongoUser
import com.example.iotmanagementdevice.security.SecurityUser
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
interface UserMapper {
    fun toDto(mongoUser: MongoUser): UserResponseDto

    fun toEntity(requestDto: UserRegistrationRequestDto): MongoUser

    @Mapping(target = "id", expression = "java(mongoUser.getId().toString())")
    fun toSecurityUser(mongoUser: MongoUser): SecurityUser
}
