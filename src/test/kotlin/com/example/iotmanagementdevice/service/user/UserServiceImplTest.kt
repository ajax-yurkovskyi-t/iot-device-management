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
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertNull

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
        MockKAnnotations.init(this)

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
        val savedUser = mongoUser.copy(id = ObjectId())

        // Stubbing
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { roleRepository.findByRoleName(MongoRole.RoleName.USER) } returns mongoRole
        every { userMapper.toEntity(requestDto) } returns mongoUser.copy(devices = mutableListOf())
        every { userRepository.save(mongoUser) } returns savedUser
        every { userMapper.toDto(savedUser) } returns userResponseDto

        // When
        val result = userService.register(requestDto)

        // Then
        assertEquals(userResponseDto, result)
        verify { roleRepository.findByRoleName(MongoRole.RoleName.USER) }
        verify { userMapper.toEntity(requestDto) }
        verify { userRepository.save(mongoUser) }
        verify { userMapper.toDto(savedUser) }
    }

    @Test
    fun `should register a new user with null roles`() {
        // Given
        val requestDto = UserRegistrationRequestDto("John Doe", "john.doe@example.com", "1234567890", "password123")
        val encodedPassword = "encodedPassword"
        val savedUser = mongoUser.copy(id = ObjectId(), roles = null)

        // Stubbing
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { roleRepository.findByRoleName(MongoRole.RoleName.USER) } returns mongoRole
        every { userMapper.toEntity(requestDto) } returns savedUser
        every { userRepository.save(savedUser) } returns savedUser
        every { userMapper.toDto(savedUser) } returns userResponseDto

        // When
        val result = userService.register(requestDto)

        // Then
        assertEquals(userResponseDto, result)
        verify { roleRepository.findByRoleName(MongoRole.RoleName.USER) }
        verify { userMapper.toEntity(requestDto) }
        verify { userRepository.save(savedUser) }
        verify { userMapper.toDto(savedUser) }

        // Verify that the role was not added to the user
        assertNull(savedUser.roles, "Expected user roles to be null since role was not found")
    }

    @Test
    fun `should register a new user without a role`() {
        // Given
        val requestDto = UserRegistrationRequestDto("John Doe", "john.doe@example.com", "1234567890", "password123")
        val encodedPassword = "encodedPassword"
        val savedUser = mongoUser.copy(id = ObjectId())

        // Stubbing
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { roleRepository.findByRoleName(MongoRole.RoleName.USER) } returns null // No role found
        every { userMapper.toEntity(requestDto) } returns mongoUser.copy(devices = mutableListOf())
        every { userRepository.save(mongoUser) } returns savedUser
        every { userMapper.toDto(savedUser) } returns userResponseDto

        // When
        val result = userService.register(requestDto)

        // Then
        assertEquals(userResponseDto, result)
        verify { roleRepository.findByRoleName(MongoRole.RoleName.USER) }
        verify { userMapper.toEntity(requestDto) }
        verify { userRepository.save(mongoUser) }
        verify { userMapper.toDto(savedUser) }
    }

    @Test
    fun `should return devices by userId`() {
        // Given
        val userId = ObjectId().toString()
        val deviceId1 = ObjectId()
        val deviceId2 = ObjectId()

        val mongoDevice1 = UserFixture.createDevice().copy(id = deviceId1)
        val mongoDevice2 = UserFixture.createDevice().copy(id = deviceId2)

        val deviceResponseDto1 = UserFixture.createDeviceResponseDto(mongoDevice1)
        val deviceResponseDto2 = UserFixture.createDeviceResponseDto(mongoDevice2)

        val deviceList = listOf(mongoDevice1, mongoDevice2)
        val deviceResponseDtoList = listOf(deviceResponseDto1, deviceResponseDto2)

        // Stubbing
        every { userRepository.findDevicesByUserId(ObjectId(userId)) } returns deviceList
        every { deviceMapper.toDto(mongoDevice1) } returns deviceResponseDto1
        every { deviceMapper.toDto(mongoDevice2) } returns deviceResponseDto2

        // When
        val result = userService.getDevicesByUserId(userId)

        // Then
        assertEquals(deviceResponseDtoList, result)
        verify { userRepository.findDevicesByUserId(ObjectId(userId)) }
        verify { deviceMapper.toDto(mongoDevice1) }
        verify { deviceMapper.toDto(mongoDevice2) }
    }

    @Test
    fun `should return user by id`() {
        // Given
        val userId = ObjectId()
        val userWithId = mongoUser.copy(id = userId, devices = mutableListOf(ObjectId())) // Use device IDs

        // Stubbing
        every { userRepository.findById(userId) } returns userWithId
        every { userMapper.toDto(userWithId) } returns userResponseDto

        // When
        val result = userService.getUserById(userId.toString())

        // Then
        assertEquals(userResponseDto, result)
        verify { userRepository.findById(userId) }
        verify { userMapper.toDto(userWithId) }
    }

    @Test
    fun `should throw exception when user not found by id`() {
        // Given
        val userId = ObjectId()

        // Stubbing
        every { userRepository.findById(userId) } returns null

        // When
        val exception = assertThrows<EntityNotFoundException> {
            userService.getUserById(userId.toString())
        }

        // Then
        assertEquals("User with id $userId not found", exception.message)
        verify { userRepository.findById(userId) }
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
        every { userRepository.findAll() } returns userList
        every { userMapper.toDto(mongoUser) } returns userResponseDto
        every { userMapper.toDto(mongoUser2) } returns userResponseDto2

        // When
        val result = userService.getAll()

        // Then
        assertEquals(userResponseList, result)
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
        every { userRepository.findByUserName(username) } returns userWithUsername
        every { userMapper.toDto(userWithUsername) } returns userResponseDto

        // When
        val result = userService.getUserByUsername(username)

        // Then
        assertEquals(userResponseDto, result)
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
        every { userRepository.findById(userId) } returns existingMongoUser
        every { passwordEncoder.encode(updatedUserDto.userPassword) } returns encodedNewPassword
        every { userRepository.save(any()) } returns updatedUser
        every { userMapper.toDto(updatedUser) } returns updatedUserResponseDto

        // When
        val result = userService.update(userId.toString(), updatedUserDto)

        // Then
        assertEquals(updatedUserResponseDto, result)
        verify { userRepository.findById(userId) }
        verify { passwordEncoder.encode(updatedUserDto.userPassword) }
        verify { userRepository.save(any()) }
        verify { userMapper.toDto(updatedUser) }
    }

    @Test
    fun `should throw exception when updating user with non-existent id`() {
        // Given
        val userId = ObjectId()
        val requestDto = UserUpdateRequestDto("Updated Name", "updated@example.com", "1234567890", "newPassword")
        every { userRepository.findById(userId) } returns null // Simulate user not found

        // When
        val exception = assertThrows<EntityNotFoundException> {
            userService.update(userId.toString(), requestDto)
        }

        // Then
        assertEquals("User with id $userId not found", exception.message)
        verify { userRepository.findById(userId) }
    }

    @Test
    fun `should assign a device to a user successfully`() {
        // Given
        val userObjectId = ObjectId()
        val deviceObjectId = ObjectId()

        // Stubbing
        every { userRepository.assignDeviceToUser(userObjectId, deviceObjectId) } returns true

        // When
        val result = userService.assignDeviceToUser(userObjectId.toString(), deviceObjectId.toString())

        // Then
        assertTrue(result, "Expected the device to be assigned to the user successfully")
        verify { userRepository.assignDeviceToUser(userObjectId, deviceObjectId) }
    }
}
