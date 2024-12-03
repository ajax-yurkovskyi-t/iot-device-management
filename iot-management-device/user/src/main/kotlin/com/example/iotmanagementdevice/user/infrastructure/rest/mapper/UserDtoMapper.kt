package com.example.iotmanagementdevice.user.infrastructure.rest.mapper

import com.example.iotmanagementdevice.user.domain.User
import com.example.iotmanagementdevice.user.infrastructure.rest.dto.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.user.infrastructure.rest.dto.response.UserResponseDto
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl"
)
interface UserDtoMapper {
    fun toDto(user: User): UserResponseDto

    fun toDomain(updateRequestDto: UserUpdateRequestDto): User
}
