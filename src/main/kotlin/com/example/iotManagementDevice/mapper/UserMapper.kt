package com.example.iotManagementDevice.mapper

import com.example.iotManagementDevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotManagementDevice.dto.user.response.UserResponseDto
import com.example.iotManagementDevice.model.User
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.NullValueCheckStrategy

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
interface UserMapper {
    fun toDto(user: User?): UserResponseDto

    @Mapping(target = "roles", expression = "java(new java.util.HashSet<>())")
    @Mapping(target = "devices", expression = "java(new java.util.ArrayList<>())")
    fun toEntity(requestDto: UserRegistrationRequestDto): User
}
