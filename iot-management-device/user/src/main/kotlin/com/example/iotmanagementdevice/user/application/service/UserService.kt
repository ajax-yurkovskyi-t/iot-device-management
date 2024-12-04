package com.example.iotmanagementdevice.user.application.service

import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.role.application.port.output.RoleRepositoryOutPort
import com.example.iotmanagementdevice.role.domain.Role
import com.example.iotmanagementdevice.user.application.port.input.UserServiceInPort
import com.example.iotmanagementdevice.user.application.port.output.UserRepositoryOutPort
import com.example.iotmanagementdevice.user.domain.CreateUser
import com.example.iotmanagementdevice.user.domain.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    private val userRepositoryOutPort: UserRepositoryOutPort,
    private val roleRepositoryOutPort: RoleRepositoryOutPort,
    private val passwordEncoder: PasswordEncoder,
) : UserServiceInPort {
    override fun register(newUser: CreateUser): Mono<User> {
        return roleRepositoryOutPort.findByRoleName(Role.RoleName.USER)
            .switchIfEmpty { Mono.error(EntityNotFoundException("Role not found")) }
            .flatMap { userMongoRole ->
                val updatedRoles = setOf(userMongoRole)
                val updatedUser = newUser.copy(
                    roles = updatedRoles,
                    userPassword = passwordEncoder.encode(newUser.userPassword)
                )
                userRepositoryOutPort.save(updatedUser)
            }
    }

    override fun assignDeviceToUser(userId: String, deviceId: String): Mono<Boolean> {
        return userRepositoryOutPort.assignDeviceToUser(userId, deviceId)
    }

    override fun getUserById(id: String): Mono<User> {
        return userRepositoryOutPort.findById(id)
            .switchIfEmpty { Mono.error(EntityNotFoundException("User with id $id not found")) }
    }

    override fun getAll(): Flux<User> {
        return userRepositoryOutPort.findAll()
    }

    override fun getUserByUsername(username: String): Mono<User> {
        return userRepositoryOutPort.findByUserName(username)
            .switchIfEmpty { Mono.error(EntityNotFoundException("User with name $username not found")) }
    }

    override fun update(id: String, user: User): Mono<User> {
        return userRepositoryOutPort.findById(id)
            .switchIfEmpty { Mono.error(EntityNotFoundException("User with id $id not found")) }
            .flatMap { existingUser ->
                val updatedUserEntity = existingUser.copy(
                    name = user.name,
                    email = user.email,
                    phoneNumber = user.phoneNumber,
                    userPassword = passwordEncoder.encode(user.userPassword),
                )
                userRepositoryOutPort.save(updatedUserEntity)
            }
    }
}
