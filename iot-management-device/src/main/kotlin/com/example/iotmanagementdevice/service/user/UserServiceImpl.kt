package com.example.iotmanagementdevice.service.user

import com.example.core.dto.response.DeviceResponseDto
import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.UserMapper
import com.example.iotmanagementdevice.model.MongoRole
import com.example.iotmanagementdevice.repository.RoleRepository
import com.example.iotmanagementdevice.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val deviceMapper: DeviceMapper,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
) : UserService {

    override fun register(requestDto: UserRegistrationRequestDto): Mono<UserResponseDto> {
        requestDto.userPassword = passwordEncoder.encode(requestDto.userPassword)

        return roleRepository.findByRoleName(MongoRole.RoleName.USER)
            .switchIfEmpty { Mono.error(EntityNotFoundException("Role not found")) }
            .flatMap { userMongoRole ->
                val user = userMapper.toEntity(requestDto)
                val updatedRoles = setOf(userMongoRole)
                val updatedUser = user.copy(roles = updatedRoles)
                userRepository.save(updatedUser)
            }
            .map { savedUser -> userMapper.toDto(savedUser) }
    }

    override fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean> {
        return userRepository.assignDeviceToUser(userId, deviceId)
    }

    override fun getUserById(id: String): Mono<UserResponseDto> {
        return userRepository.findById(id)
            .switchIfEmpty { Mono.error(EntityNotFoundException("User with id $id not found")) }
            .map { userMapper.toDto(it) }
    }

    override fun getDevicesByUserId(userId: String): Flux<DeviceResponseDto> {
        return userRepository.findById(userId)
            .switchIfEmpty { Mono.error(EntityNotFoundException("User with id $userId not found")) }
            .flatMapMany { userRepository.findDevicesByUserId(userId) }
            .map { deviceMapper.toDto(it) }
    }

    override fun getAll(): Flux<UserResponseDto> {
        return userRepository.findAll()
            .map { userMapper.toDto(it) }
    }

    override fun getUserByUsername(username: String): Mono<UserResponseDto> {
        return userRepository.findByUserName(username)
            .switchIfEmpty { Mono.error(EntityNotFoundException("User with name $username not found")) }
            .map { userMapper.toDto(it) }
    }

    override fun update(id: String, requestDto: UserUpdateRequestDto): Mono<UserResponseDto> {
        return userRepository.findById(id)
            .switchIfEmpty { Mono.error(EntityNotFoundException("User with id $id not found")) }
            .flatMap { existingUser ->
                val updatedUserEntity = existingUser.copy(
                    name = requestDto.name,
                    email = requestDto.email,
                    phoneNumber = requestDto.phoneNumber,
                    userPassword = passwordEncoder.encode(requestDto.userPassword),
                )
                userRepository.save(updatedUserEntity)
            }
            .map { userMapper.toDto(it) }
    }
}
