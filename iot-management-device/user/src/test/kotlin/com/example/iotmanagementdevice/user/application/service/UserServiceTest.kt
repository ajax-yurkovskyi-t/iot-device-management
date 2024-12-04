package com.example.iotmanagementdevice.user.application.service

import com.example.core.exception.EntityNotFoundException
import com.example.iotmanagementdevice.role.application.port.output.RoleRepositoryOutPort
import com.example.iotmanagementdevice.role.domain.Role
import com.example.iotmanagementdevice.user.UserFixture.createRole
import com.example.iotmanagementdevice.user.UserFixture.createUser
import com.example.iotmanagementdevice.user.UserFixture.createUserCreate
import com.example.iotmanagementdevice.user.application.port.output.UserRepositoryOutPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    private lateinit var userRepositoryOutPort: UserRepositoryOutPort

    @MockK
    private lateinit var passwordEncoder: PasswordEncoder

    @MockK
    private lateinit var roleRepositoryOutPort: RoleRepositoryOutPort

    @InjectMockKs
    lateinit var userService: UserService

    @Test
    fun `should register a new user`() {
        // Given
        val role = createRole()
        val user = createUser()
        val userCreate = createUserCreate()
        val encodedPassword = "encodedPassword"

        // Stubbing
        every { passwordEncoder.encode(any()) } returns encodedPassword
        every { roleRepositoryOutPort.findByRoleName(Role.RoleName.USER) } returns role.toMono()
        every {
            userRepositoryOutPort.save(
                userCreate.copy
                    (roles = mutableSetOf(role))
            )
        } returns user.toMono()

        // When
        val registeredUser = userService.register(userCreate)

        // Then
        registeredUser.test()
            .expectNext(user)
            .verifyComplete()

        verify { roleRepositoryOutPort.findByRoleName(Role.RoleName.USER) }
        verify { userRepositoryOutPort.save(userCreate.copy(roles = mutableSetOf(role))) } // Verify save includes role
    }

    @Test
    fun `should return user by id`() {
        // Given
        val user = createUser()

        // Stubbing
        every { userRepositoryOutPort.findById(user.id!!) } returns user.toMono()

        // When
        val foundUser = userService.getUserById(user.id!!)

        // Then
        foundUser.test()
            .expectNext(user)
            .verifyComplete()

        verify { userRepositoryOutPort.findById(user.id!!) }
    }

    @Test
    fun `should throw exception when user not found by id`() {
        // Given
        val userId = ObjectId()

        // Stubbing
        every { userRepositoryOutPort.findById(userId.toString()) } returns Mono.empty()

        // When
        val nonExistingUser = userService.getUserById(userId.toString())

        // Then
        nonExistingUser.test()
            .verifyError<EntityNotFoundException>()

        verify { userRepositoryOutPort.findById(userId.toString()) }
    }

    @Test
    fun `should return all users`() {
        // Given
        val user = createUser()

        val userList = listOf(user)

        // Stubbing
        every { userRepositoryOutPort.findAll() } returns userList.toFlux()

        // When
        val users = userService.getAll()

        // Then
        users.test()
            .expectNextSequence(userList)
            .verifyComplete()

        verify { userRepositoryOutPort.findAll() }
    }

    @Test
    fun `should return user by username`() {
        // Given
        val username = "JohnDoe"
        val userWithUsername = createUser().copy(name = username)

        // Stubbing
        every { userRepositoryOutPort.findByUserName(username) } returns userWithUsername.toMono()

        // When
        val foundUser = userService.getUserByUsername(username)

        // Then
        foundUser.test()
            .expectNext(userWithUsername)
            .verifyComplete()

        verify { userRepositoryOutPort.findByUserName(username) }
    }

    @Test
    fun `should update an existing user`() {
        // Given
        val userId = ObjectId()
        val user = createUser()
        val encodedNewPassword = "encodedNewPassword"
        val updatedUser = user.copy(
            name = "Updated name",
            email = "Updated email",
            phoneNumber = "Updated phoneNumber",
            userPassword = encodedNewPassword,
            devices = user.devices
        )

        // Stubbing
        every { userRepositoryOutPort.findById(userId.toString()) } returns user.toMono()
        every { passwordEncoder.encode(any()) } returns encodedNewPassword
        every { userRepositoryOutPort.save(updatedUser) } returns updatedUser.toMono()

        // When
        val updatedUserResult = userService.update(userId.toString(), updatedUser)

        // Then
        updatedUserResult.test()
            .expectNext(updatedUser)
            .verifyComplete()

        verify { userRepositoryOutPort.findById(userId.toString()) }
        verify { passwordEncoder.encode(any()) }
        verify { userRepositoryOutPort.save(updatedUser) }
    }

    @Test
    fun `should throw exception when updating user with non-existent id`() {
        // Given
        val user = createUser()
        every { userRepositoryOutPort.findById(user.id!!) } returns Mono.empty()

        // When
        val nonExistentUser = userService.update(user.id!!, user)

        // Then
        nonExistentUser.test()
            .verifyError<EntityNotFoundException>()

        verify { userRepositoryOutPort.findById(user.id!!) }
    }

    @Test
    fun `should assign a device to a user successfully`() {
        // Given
        val userObjectId = ObjectId()
        val deviceObjectId = ObjectId()

        // Stubbing
        every {
            userRepositoryOutPort.assignDeviceToUser(
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

        verify { userRepositoryOutPort.assignDeviceToUser(userObjectId.toString(), deviceObjectId.toString()) }
    }
}
