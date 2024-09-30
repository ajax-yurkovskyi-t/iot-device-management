package com.example.iotmanagementdevice.service.user

import com.example.iotmanagementdevice.dto.device.response.DeviceResponseDto
import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import com.example.iotmanagementdevice.exception.EntityNotFoundException
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.UserMapper
import com.example.iotmanagementdevice.model.MongoRole
import com.example.iotmanagementdevice.repository.RoleRepository
import com.example.iotmanagementdevice.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val deviceMapper: DeviceMapper,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
) : UserService {

    override fun register(requestDto: UserRegistrationRequestDto): UserResponseDto {
        requestDto.userPassword = passwordEncoder.encode(requestDto.userPassword)
        val userMongoRole = roleRepository.findByRoleName(MongoRole.RoleName.USER)
        val user = userMapper.toEntity(requestDto)
        val updatedRoles = userMongoRole?.let { setOf(it) } ?: emptySet()
        val updatedUser = user.copy(roles = updatedRoles)
        return userMapper.toDto(userRepository.save(updatedUser))
    }

    override fun assignDeviceToUser(userId: String, deviceId: String): Boolean {
        return userRepository.assignDeviceToUser(userId, deviceId)
    }

    override fun getUserById(id: String): UserResponseDto {
        return userMapper.toDto(
            userRepository.findById(id)
                ?: throw EntityNotFoundException("User with id $id not found")
        )
    }

    override fun getDevicesByUserId(userId: String): List<DeviceResponseDto> {
        return userRepository.findDevicesByUserId(userId)
            .map { deviceMapper.toDto(it) }
    }

    override fun getAll(): List<UserResponseDto> {
        return userRepository.findAll().map { userMapper.toDto(it) }
    }

    override fun getUserByUsername(username: String): UserResponseDto {
        return userMapper.toDto(userRepository.findByUserName(username))
    }

    override fun update(id: String, requestDto: UserUpdateRequestDto): UserResponseDto {
        val existingUser = userRepository.findById(id)
            ?: throw EntityNotFoundException("User with id $id not found")

        val updatedUserEntity = existingUser.copy(
            name = requestDto.name,
            email = requestDto.email,
            phoneNumber = requestDto.phoneNumber,
            userPassword = passwordEncoder.encode(requestDto.userPassword),
        )

        return userMapper.toDto(userRepository.save(updatedUserEntity))
    }
}
