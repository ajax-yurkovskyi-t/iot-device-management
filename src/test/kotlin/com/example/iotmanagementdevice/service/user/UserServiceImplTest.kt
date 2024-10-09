package com.example.iotmanagementdevice.service.user

import com.example.iotmanagementdevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import com.example.iotmanagementdevice.dto.user.response.UserResponseDto
import com.example.iotmanagementdevice.exception.EntityNotFoundException
import com.example.iotmanagementdevice.mapper.DeviceMapper
import com.example.iotmanagementdevice.mapper.UserMapper
import com.example.iotmanagementdevice.model.MongoDevice
import com.example.iotmanagementdevice.model.MongoRole
import com.example.iotmanagementdevice.model.MongoUser
import com.example.iotmanagementdevice.repository.RoleRepository
import com.example.iotmanagementdevice.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
class UserServiceImplTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var userMapper: UserMapper

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var deviceMapper: DeviceMapper

    @MockK
    private lateinit var roleRepository: RoleRepository

    @InjectMockKs
    lateinit var userService: UserServiceImpl

    private lateinit var mongoUser: MongoUser
    private lateinit var mongoRole: MongoRole
    private lateinit var userResponseDto: UserResponseDto
    private lateinit var device: MongoDevice

    @BeforeEach
    fun setUp() {
        mongoRole = MongoRole(id = ObjectId(), roleName = MongoRole.RoleName.USER)
        mongoUser = UserFixture.createUser().copy(devices = mutableListOf())
        device = UserFixture.createDevice()
        userResponseDto = UserFixture.createUserResponseDto(listOf(device)) // Updated to use DeviceResponseDto
    }

    @Test
    fun `should register a new user`() {
        // Given
        val requestDto = UserRegistrationRequestDto("John Doe", "john.doe@example.com", "1234567890", "password123")
        val encodedPassword = "encodedPassword"
        val savedUser = mongoUser.copy(id = ObjectId(), roles = mutableSetOf(mongoRole))

        // Stubbing
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { roleRepository.findByRoleName(MongoRole.RoleName.USER) } returns mongoRole.toMono()
        every { userMapper.toEntity(requestDto) } returns
            mongoUser.copy(roles = mutableSetOf())
        every {
            userRepository.save(
                mongoUser.copy
                    (roles = mutableSetOf(mongoRole))
            )
        } returns savedUser.toMono() // Save user with role
        every { userMapper.toDto(savedUser) } returns userResponseDto

        // When
        val registeredUser = userService.register(requestDto)

        // Then
        registeredUser.test()
            .expectNext(userResponseDto)
            .verifyComplete()

        verify { roleRepository.findByRoleName(MongoRole.RoleName.USER) }
        verify { userMapper.toEntity(requestDto) }
        verify { userRepository.save(mongoUser.copy(roles = mutableSetOf(mongoRole))) } // Verify save includes role
        verify { userMapper.toDto(savedUser) }
    }

    @Test
    fun `should register a new user with null roles`() {
        // Given
        val requestDto = UserRegistrationRequestDto("John Doe", "john.doe@example.com", "1234567890", "password123")
        val encodedPassword = "encodedPassword"
        val userMongoRole = mongoRole
        val savedUser = mongoUser.copy(id = ObjectId(), roles = null)
        val userWithRoles = savedUser.copy(roles = setOf(userMongoRole))

        // Stubbing
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { roleRepository.findByRoleName(MongoRole.RoleName.USER) } returns userMongoRole.toMono()
        every { userMapper.toEntity(requestDto) } returns savedUser
        every { userRepository.save(userWithRoles) } returns userWithRoles.toMono()
        every { userMapper.toDto(userWithRoles) } returns userResponseDto

        // When
        val registeredUser = userService.register(requestDto)

        // Then
        registeredUser.test()
            .expectNext(userResponseDto)
            .verifyComplete()

        verify { roleRepository.findByRoleName(MongoRole.RoleName.USER) }
        verify { userMapper.toEntity(requestDto) }
        verify { userRepository.save(userWithRoles) }
        verify { userMapper.toDto(userWithRoles) }
    }

    @Test
    fun `should return devices by userId`() {
        // Given
        val userId = ObjectId()
        val deviceId1 = ObjectId()
        val deviceId2 = ObjectId()

        val mongoDevice1 = UserFixture.createDevice().copy(id = deviceId1)
        val mongoDevice2 = UserFixture.createDevice().copy(id = deviceId2)

        val deviceResponseDto1 = UserFixture.createDeviceResponseDto(mongoDevice1)
        val deviceResponseDto2 = UserFixture.createDeviceResponseDto(mongoDevice2)

        val deviceList = listOf(mongoDevice1, mongoDevice2)
        val deviceResponseDtoList = listOf(deviceResponseDto1, deviceResponseDto2)

        // Stubbing
        every { userRepository.findDevicesByUserId(userId.toString()) } returns deviceList.toFlux()
        every { deviceMapper.toDto(mongoDevice1) } returns deviceResponseDto1
        every { deviceMapper.toDto(mongoDevice2) } returns deviceResponseDto2

        // When
        val devices = userService.getDevicesByUserId(userId.toString())

        // Then
        devices.test()
            .expectNextSequence(deviceResponseDtoList)
            .verifyComplete()

        verify { userRepository.findDevicesByUserId(userId.toString()) }
        verify { deviceMapper.toDto(mongoDevice1) }
        verify { deviceMapper.toDto(mongoDevice2) }
    }

    @Test
    fun `should return user by id`() {
        // Given
        val userId = ObjectId()
        val userWithId = mongoUser.copy(id = userId, devices = mutableListOf(ObjectId())) // Use device IDs

        // Stubbing
        every { userRepository.findById(userId.toString()) } returns userWithId.toMono()
        every { userMapper.toDto(userWithId) } returns userResponseDto

        // When
        val foundUser = userService.getUserById(userId.toString())

        // Then
        foundUser.test()
            .expectNext(userResponseDto)
            .verifyComplete()

        verify { userRepository.findById(userId.toString()) }
        verify { userMapper.toDto(userWithId) }
    }

    @Test
    fun `should throw exception when user not found by id`() {
        // Given
        val userId = ObjectId()

        // Stubbing
        every { userRepository.findById(userId.toString()) } returns Mono.empty()

        // When
        val nonExistingUser = userService.getUserById(userId.toString())

        // Then
        nonExistingUser.test()
            .verifyError<EntityNotFoundException>()

        verify { userRepository.findById(userId.toString()) }
    }

    @Test
    fun `should return all users`() {
        // Given
        val mongoUser2 = MongoUser(
            id = ObjectId(),
            name = "Jane Smith",
            email = "jane.smith@example.com",
            phoneNumber = "0987654321",
            userPassword = "encodedPassword",
            roles = mutableSetOf(),
            devices = mutableListOf(ObjectId()) // Use device IDs
        )
        val userResponseDto2 = UserResponseDto(
            name = "Jane Smith",
            email = "jane.smith@example.com",
            phoneNumber = "0987654321",
            devices = emptyList()
        )
        val userList = listOf(mongoUser, mongoUser2)
        val userResponseList = listOf(userResponseDto, userResponseDto2)

        // Stubbing
        every { userRepository.findAll() } returns userList.toFlux()
        every { userMapper.toDto(mongoUser) } returns userResponseDto
        every { userMapper.toDto(mongoUser2) } returns userResponseDto2

        // When
        val users = userService.getAll()

        // Then
        users.test()
            .expectNextSequence(userResponseList)
            .verifyComplete()

        verify { userRepository.findAll() }
        verify { userMapper.toDto(mongoUser) }
        verify { userMapper.toDto(mongoUser2) }
    }

    @Test
    fun `should return user by username`() {
        // Given
        val username = "JohnDoe"
        val userWithUsername = mongoUser.copy(name = username)

        // Stubbing
        every { userRepository.findByUserName(username) } returns userWithUsername.toMono()
        every { userMapper.toDto(userWithUsername) } returns userResponseDto

        // When
        val foundUser = userService.getUserByUsername(username)

        // Then
        foundUser.test()
            .expectNext(userResponseDto)
            .verifyComplete()

        verify { userRepository.findByUserName(username) }
        verify { userMapper.toDto(userWithUsername) }
    }

    @Test
    fun `should update an existing user`() {
        // Given
        val userId = ObjectId()
        val existingMongoUser = mongoUser.copy(id = userId, devices = mutableListOf(ObjectId())) // Use device IDs
        val updatedUserDto = UserUpdateRequestDto(
            name = "John Smith",
            email = "john.smith@example.com",
            phoneNumber = "0987654321",
            userPassword = "newPassword"
        )
        val encodedNewPassword = "encodedNewPassword"
        val updatedUser = existingMongoUser.copy(
            name = updatedUserDto.name,
            email = updatedUserDto.email,
            phoneNumber = updatedUserDto.phoneNumber,
            userPassword = encodedNewPassword,
            devices = existingMongoUser.devices
        )
        val updatedUserResponseDto = UserResponseDto(
            name = updatedUserDto.name,
            email = updatedUserDto.email,
            phoneNumber = updatedUserDto.phoneNumber,
            devices = listOf()
        )

        // Stubbing
        every { userRepository.findById(userId.toString()) } returns existingMongoUser.toMono()
        every { passwordEncoder.encode(updatedUserDto.userPassword) } returns encodedNewPassword
        every { userRepository.save(any()) } returns updatedUser.toMono()
        every { userMapper.toDto(updatedUser) } returns updatedUserResponseDto

        // When
        val updatedUserResult = userService.update(userId.toString(), updatedUserDto)

        // Then
        updatedUserResult.test()
            .expectNext(updatedUserResponseDto)
            .verifyComplete()

        verify { userRepository.findById(userId.toString()) }
        verify { passwordEncoder.encode(updatedUserDto.userPassword) }
        verify { userRepository.save(any()) }
        verify { userMapper.toDto(updatedUser) }
    }

    @Test
    fun `should throw exception when updating user with non-existent id`() {
        // Given
        val userId = ObjectId()
        val requestDto = UserUpdateRequestDto("Updated Name", "updated@example.com", "1234567890", "newPassword")
        every { userRepository.findById(userId.toString()) } returns Mono.empty()

        // When
        val nonExistentUser = userService.update(userId.toString(), requestDto)

        // Then
        nonExistentUser.test()
            .verifyError<EntityNotFoundException>()

        verify { userRepository.findById(userId.toString()) }
    }

    @Test
    fun `should assign a device to a user successfully`() {
        // Given
        val userObjectId = ObjectId()
        val deviceObjectId = ObjectId()

        // Stubbing
        every {
            userRepository.assignDeviceToUser(
                userObjectId.toString(), deviceObjectId.toString()
            )
        } returns true.toMono()

        // When
        val result = userService.assignDeviceToUser(userObjectId.toString(), deviceObjectId.toString())

        // Then
        result.test()
            .assertNext {
                assertTrue(it) { "Expected true when assigning device to the user" }
            }
            .verifyComplete()

        verify { userRepository.assignDeviceToUser(userObjectId.toString(), deviceObjectId.toString()) }
    }
}
