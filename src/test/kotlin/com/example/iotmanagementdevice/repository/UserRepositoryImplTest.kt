package com.example.iotmanagementdevice.repository

import UserFixture
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class UserRepositoryImplTest : AbstractMongoTest {

    @Autowired
    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Autowired
    private lateinit var deviceRepositoryImpl: DeviceRepositoryImpl

    @Test
    fun `should find user by id when saved`() {
        // Given
        val user = UserFixture.createUser()
        userRepositoryImpl.save(user).block()

        // When
        val foundUser = userRepositoryImpl.findById(user.id!!.toString())

        // Then
        foundUser.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should return devices when queried by userId`() {
        // Given
        val user = UserFixture.createUser()
        userRepositoryImpl.save(user).block()

        val device1 = UserFixture.createDevice().copy(name = "Device1", userId = user.id)
        val device2 = UserFixture.createDevice().copy(name = "Device2", userId = user.id)

        deviceRepositoryImpl.save(device1).block()
        deviceRepositoryImpl.save(device2).block()

        // Assign devices to user
        userRepositoryImpl.assignDeviceToUser(user.id!!.toString(), device1.id!!.toString()).block()
        userRepositoryImpl.assignDeviceToUser(user.id!!.toString(), device2.id!!.toString()).block()

        // When
        val devices = userRepositoryImpl.findDevicesByUserId(user.id!!.toString()).collectList()

        // Then
        devices.test()
            .expectNextMatches {
                it.contains(device1) && it.contains(device2)
            }
            .verifyComplete()
    }

    @Test
    fun `should return empty list of devices when no devices assigned to user`() {
        // Given
        val user = UserFixture.createUser()
        userRepositoryImpl.save(user)

        // When
        val devices = userRepositoryImpl.findDevicesByUserId(user.id!!.toString()).collectList()

        // Then
        devices.test()
            .expectNextMatches { it.isEmpty() }
            .verifyComplete()
    }

    @Test
    fun `should return false when assigning a device to a non-existent user`() {
        // Given
        val userId = ObjectId() // Non-existent user ID
        val deviceId = UserFixture.createDevice().id!!

        // When
        val result = userRepositoryImpl.assignDeviceToUser(userId.toString(), deviceId.toString())

        // Then
        result.test()
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `should find all users when multiple users are saved`() {
        // Given
        val user1 = UserFixture.createUser().copy(name = "User1", email = "user1@example.com")
        val user2 = UserFixture.createUser().copy(name = "User2", email = "user2@example.com")
        userRepositoryImpl.save(user1).block()
        userRepositoryImpl.save(user2).block()

        // When
        val users = userRepositoryImpl.findAll().collectList()

        // Then
        users.test()
            .expectNextMatches {
                it.contains(user1) && it.contains(user2)
            }
            .verifyComplete()
    }

    @Test
    fun `should reflect the device assignment on the user`() {
        // Given
        val user = UserFixture.createUser()
        val device = UserFixture.createDevice()

        userRepositoryImpl.save(user).block()
        deviceRepositoryImpl.save(device).block()

        // When
        userRepositoryImpl.assignDeviceToUser(user.id!!.toString(), device.id!!.toString()).block()

        // Then
        deviceRepositoryImpl.findById(device.id!!.toString()).test()
            .expectNextMatches { it.userId == user.id }
            .verifyComplete()
    }

    @Test
    fun `should reflect the user update after being updated`() {
        // Given
        val user = UserFixture.createUser()
        userRepositoryImpl.save(user).block()

        val updatedUserDto = UserUpdateRequestDto(
            name = "Updated Name",
            email = "updated@example.com",
            phoneNumber = "123-456-7890",
            userPassword = "newPassword"
        )

        val updatedUser = user.copy(
            name = updatedUserDto.name,
            email = updatedUserDto.email,
            phoneNumber = updatedUserDto.phoneNumber,
            userPassword = updatedUserDto.userPassword
        )

        // When
        userRepositoryImpl.save(updatedUser).block()

        // Then
        userRepositoryImpl.findById(user.id!!.toString()).test()
            .expectNextMatches {
                it.name == updatedUserDto.name &&
                    it.email == updatedUserDto.email &&
                    it.phoneNumber == updatedUserDto.phoneNumber &&
                    it.userPassword == updatedUserDto.userPassword
            }
            .verifyComplete()
    }

    @Test
    fun `should not find a user after it is deleted`() {
        // Given
        val user = UserFixture.createUser()
        userRepositoryImpl.save(user).block()

        // When
        userRepositoryImpl.deleteById(user.id!!.toString()).block()

        // Then
        userRepositoryImpl.findById(user.id!!.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `should find user by username`() {
        // Given
        val user = UserFixture.createUser().copy(name = "findUserName")
        userRepositoryImpl.save(user).block()

        // When
        val foundUser = userRepositoryImpl.findByUserName(user.name!!)

        // Then
        foundUser.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should find user by email`() {
        // Given
        val user = UserFixture.createUser().copy(email = "user@find.com")
        userRepositoryImpl.save(user).block()

        // When
        val foundUser = userRepositoryImpl.findByUserEmail(user.email!!)

        // Then
        foundUser.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should return false when assigning a non-existent device to user`() {
        // Given
        val user = UserFixture.createUser()
        userRepositoryImpl.save(user).block()

        val nonExistentDeviceId = ObjectId() // Non-existent device ID

        // When
        val result = userRepositoryImpl.assignDeviceToUser(user.id!!.toString(), nonExistentDeviceId.toString())

        // Then
        result.test()
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `should delete non-existent user without error`() {
        // Given
        val nonExistentUserId = ObjectId()

        // When
        userRepositoryImpl.deleteById(nonExistentUserId.toString()).block()

        // Then
        userRepositoryImpl.findById(nonExistentUserId.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `should return empty list when querying devices of a non-existent user`() {
        // Given
        val nonExistentUserId = ObjectId() // A user ID that does not exist in the database

        // When
        val devices = userRepositoryImpl.findDevicesByUserId(nonExistentUserId.toString()).collectList()

        // Then
        devices.test()
            .expectNextMatches { it.isEmpty() }
            .verifyComplete()
    }

    @Test
    fun `should return null when querying non-existent user`() {
        // Given
        val user = UserFixture.createUser().copy(devices = null) // Create a non-existent user ID

        // When
        val devices = userRepositoryImpl.findDevicesByUserId(user.id!!.toString()).collectList()

        // Then
        devices.test()
            .expectNextMatches { it.isEmpty() }
            .verifyComplete()
    }
}
