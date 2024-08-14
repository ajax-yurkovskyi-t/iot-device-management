package com.example.iot_management_device.mapper

import com.example.iot_management_device.dto.user.UserRegistrationRequestDto
import com.example.iot_management_device.model.User
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface UserMapper {

    fun toDto(user: User): UserRegistrationRequestDto

    fun toEntity(dto: UserRegistrationRequestDto): User
}
