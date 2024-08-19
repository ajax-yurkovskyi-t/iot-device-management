package com.example.iot_management_device.service.user

import com.example.iot_management_device.dto.user.request.UserRegistrationRequestDto
import com.example.iot_management_device.dto.user.response.UserResponseDto
import com.example.iot_management_device.dto.user.request.UserUpdateRequestDto
import com.example.iot_management_device.exception.EntityNotFoundException
import com.example.iot_management_device.mapper.UserMapper
import com.example.iot_management_device.model.Role
import com.example.iot_management_device.repository.DeviceRepository
import com.example.iot_management_device.repository.RoleRepository
import com.example.iot_management_device.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val deviceRepository: DeviceRepository
) : UserService {

    override fun register(requestDto: UserRegistrationRequestDto): UserResponseDto {
        requestDto.userPassword = passwordEncoder.encode(requestDto.userPassword)
        val userRole = roleRepository.findByRoleName(Role.RoleName.USER)
        val user = userMapper.toEntity(requestDto)
        user.roles?.add(userRole)
        return userMapper.toDto(userRepository.save(user))
    }

    @Transactional
    override fun assignDeviceToUser(userId: Long, deviceId: Long): UserResponseDto {
        val device = deviceRepository.findById(deviceId)
            .orElseThrow { IllegalArgumentException("Device with id $deviceId not found") }

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User with id $userId not found") }

        val updatedDevice = device.copy(user = user)
        user.devices?.add(device)
        deviceRepository.save(updatedDevice)
        return userMapper.toDto(userRepository.save(user))
    }

    override fun getUserById(id: Long): UserResponseDto =
        userMapper.toDto(userRepository.findById(id).orElseThrow { EntityNotFoundException("User with id $id not found") })


    override fun getAll(): List<UserResponseDto> =
        userRepository.findAll().map { userMapper.toDto(it) }

    override fun getUserByUsername(username: String): UserResponseDto =
        userMapper.toDto(userRepository.findByName(username))


    override fun update(id: Long, requestDto: UserUpdateRequestDto): UserResponseDto {
        val existingUser = userRepository.findById(id).orElseThrow {
            EntityNotFoundException("User with id $id not found")
        }

        val updatedUserEntity = existingUser.copy(
            name = requestDto.name,
            email = requestDto.email,
            phoneNumber = requestDto.phoneNumber,
            userPassword = passwordEncoder.encode(requestDto.userPassword),
        )

        return userMapper.toDto(userRepository.save(updatedUserEntity))
    }
}
