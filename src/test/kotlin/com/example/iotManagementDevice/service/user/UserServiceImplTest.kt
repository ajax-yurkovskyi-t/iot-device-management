package com.example.iotManagementDevice.service.user

import com.example.iotManagementDevice.dto.device.response.DeviceResponseDto
import com.example.iotManagementDevice.dto.user.request.UserRegistrationRequestDto
import com.example.iotManagementDevice.dto.user.response.UserResponseDto
import com.example.iotManagementDevice.dto.user.request.UserUpdateRequestDto
import com.example.iotManagementDevice.exception.EntityNotFoundException
import com.example.iotManagementDevice.mapper.UserMapper
import com.example.iotManagementDevice.model.Device
import com.example.iotManagementDevice.model.Role
import com.example.iotManagementDevice.model.User
import com.example.iotManagementDevice.repository.DeviceRepository
import com.example.iotManagementDevice.repository.RoleRepository
import com.example.iotManagementDevice.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceImplTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var userMapper: UserMapper

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var roleRepository: RoleRepository

    @MockK
    private lateinit var deviceRepository: DeviceRepository

    @InjectMockKs
    lateinit var userService: UserServiceImpl

    private lateinit var user: User
    private lateinit var role: Role
    private lateinit var userResponseDto: UserResponseDto
    private lateinit var device: Device
    private lateinit var devices: List<Device>

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        role = Role(id = 1L, roleName = Role.RoleName.USER)

        role = UserFixture.createRole()
        user = UserFixture.createUser()
        device = UserFixture.createDevice(user)
        devices = listOf(device)
        user = user.copy(devices = devices.toMutableList())
        userResponseDto = UserFixture.createUserResponseDto(devices)
    }

    @Test
    fun `should assign device to user`() {
        // Given
        val userId = 1L
        val deviceId = 2L

        // Create a user and device
        val user = UserFixture.createUser().copy(id = userId, devices = mutableListOf())
        val updatedDevice = device.copy(user = user)

        val userResponseDto = UserFixture.createUserResponseDto(listOf(updatedDevice))

        // Stubbing
        every { deviceRepository.findById(deviceId) } returns Optional.of(device)
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { deviceRepository.save(updatedDevice) } returns updatedDevice
        every { userRepository.save(user) } returns user
        every { userMapper.toDto(user) } returns userResponseDto

        // When
        val result = userService.assignDeviceToUser(userId, deviceId)

        // Then
        assertEquals(userResponseDto, result)
        verify {
            deviceRepository.findById(deviceId)
            userRepository.findById(userId)
            deviceRepository.save(updatedDevice)
            userRepository.save(user)
            userMapper.toDto(user)
        }
    }

    @Test
    fun `should register a new user`() {
        // Given
        val requestDto = UserRegistrationRequestDto("John Doe", "john.doe@example.com", "1234567890", "password123")
        val encodedPassword = "encodedPassword"
        val savedUser = user.copy(id = 1L)

        // Stubbing
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { roleRepository.findByRoleName(Role.RoleName.USER) } returns role
        every { userMapper.toEntity(requestDto) } returns user
        every { userRepository.save(user) } returns savedUser
        every { userMapper.toDto(savedUser) } returns userResponseDto

        // When
        val result = userService.register(requestDto)

        // Then
        assertEquals(userResponseDto, result)
        verify { roleRepository.findByRoleName(Role.RoleName.USER) }
        verify { userMapper.toEntity(requestDto) }
        verify { userRepository.save(user) }
        verify { userMapper.toDto(savedUser) }
    }


    @Test
    fun `should return user by id`() {
        // Given
        val userId = 1L
        val userWithId = user.copy(id = userId) // Use shared user data

        // Stubbing
        every { userRepository.findById(userId) } returns Optional.of(userWithId)
        every { userMapper.toDto(userWithId) } returns userResponseDto

        // When
        val result = userService.getUserById(userId)

        // Then
        assertEquals(userResponseDto, result)
        verify { userRepository.findById(userId) }
        verify { userMapper.toDto(userWithId) }
    }

    @Test
    fun `should throw exception when user not found by id`() {
        // Given
        val userId = 999L

        // Stubbing
        every { userRepository.findById(userId) } returns Optional.empty()

        // When
        val exception = assertThrows<EntityNotFoundException> {
            userService.getUserById(userId)
        }

        // Then
        assertEquals("User with id $userId not found", exception.message)
        verify { userRepository.findById(userId) }
    }

    @Test
    fun `should return all users`() {
        // Given
        val user2 = User(
            id = 2L,
            name = "Jane Smith",
            email = "jane.smith@example.com",
            phoneNumber = "0987654321",
            userPassword = "encodedPassword",
            roles = mutableSetOf(),
            devices = mutableListOf()
        )
        val userResponseDto2 = UserResponseDto(
            id = 2L,
            username = "Jane Smith",
            email = "jane.smith@example.com",
            phoneNumber = "0987654321",
            devices = emptyList()
        )
        val userList = listOf(user, user2)
        val userResponseList = listOf(userResponseDto, userResponseDto2)

        // Stubbing
        every { userRepository.findAll() } returns userList
        every { userMapper.toDto(user) } returns userResponseDto
        every { userMapper.toDto(user2) } returns userResponseDto2

        // When
        val result = userService.getAll()

        // Then
        assertEquals(userResponseList, result)
        verify { userRepository.findAll() }
        verify { userMapper.toDto(user) }
        verify { userMapper.toDto(user2) }
    }

    @Test
    fun `should return user by username`() {
        // Given
        val username = "JohnDoe"
        val userWithUsername = user.copy(name = username)

        // Stubbing
        every { userRepository.findByName(username) } returns userWithUsername
        every { userMapper.toDto(userWithUsername) } returns userResponseDto

        // When
        val result = userService.getUserByUsername(username)

        // Then
        assertEquals(userResponseDto, result)
        verify { userRepository.findByName(username) }
        verify { userMapper.toDto(userWithUsername) }
    }


    @Test
    fun `should update an existing user`() {
        // Given
        val userId = 1L
        val existingUser = User(
            id = userId,
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "1234567890",
            userPassword = "oldEncodedPassword",
            roles = mutableSetOf(),
            devices = mutableListOf()
        )
        val updatedUserDto = UserUpdateRequestDto(
            name = "John Smith",
            email = "john.smith@example.com",
            phoneNumber = "0987654321",
            userPassword = "newPassword"
        )
        val encodedNewPassword = "encodedNewPassword"
        val updatedUser = existingUser.copy(
            name = updatedUserDto.name,
            email = updatedUserDto.email,
            phoneNumber = updatedUserDto.phoneNumber,
            userPassword = encodedNewPassword,
            devices = devices.toMutableList()
        )
        val updatedUserResponseDto = UserResponseDto(
            id = userId,
            username = updatedUserDto.name,
            email = updatedUserDto.email,
            phoneNumber = updatedUserDto.phoneNumber,
            devices = devices.map { device ->
                DeviceResponseDto(
                    device.name,
                    device.description,
                    device.type,
                    device.statusType
                )
            }
        )

        // Stubbing
        every { userRepository.findById(userId) } returns Optional.of(existingUser)
        every { passwordEncoder.encode(updatedUserDto.userPassword) } returns encodedNewPassword
        every { userRepository.save(any()) } returns updatedUser // Use `any()` to match any User instance
        every { userMapper.toDto(updatedUser) } returns updatedUserResponseDto

        // When
        val result = userService.update(userId, updatedUserDto)

        // Then
        assertEquals(updatedUserResponseDto, result)
        verify {
            userRepository.findById(userId)
            userRepository.save(any()) // Verify save is called with any User instance
            userMapper.toDto(updatedUser)
        }
    }
}
