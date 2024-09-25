package com.example.iotmanagementdevice.repository

import UserFixture
import com.example.iotmanagementdevice.dto.user.request.UserUpdateRequestDto
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserQueryRepositoryTest : AbstractMongoTest {

    @Autowired
    private lateinit var userQueryRepository: UserQueryRepository

    @Autowired
    private lateinit var deviceQueryRepository: DeviceQueryRepository

    @Test
    fun `should find user by id when saved`() {
        // Given
        val user = UserFixture.createUser()

        // When
        userQueryRepository.save(user)
        val foundUser = userQueryRepository.findById(user.id!!)

        // Then
        assertEquals(user, foundUser)
    }

    @Test
    fun `should return devices when queried by userId`() {
        // Given
        val user = UserFixture.createUser()
        userQueryRepository.save(user)

        val device1 = UserFixture.createDevice().copy(name = "Device1")
        val device2 = UserFixture.createDevice().copy(name = "Device2")

        deviceQueryRepository.save(device1)
        deviceQueryRepository.save(device2)

        // Assign devices to user
        userQueryRepository.assignDeviceToUser(user.id!!, device1.id!!)
        userQueryRepository.assignDeviceToUser(user.id!!, device2.id!!)

        // When
        val devices = userQueryRepository.findDevicesByUserId(user.id!!)

        // Then
        assertEquals(2, devices.size)
        assertTrue(
            devices.map { it.id }.containsAll(listOf(device1.id, device2.id)),
            "Devices assigned to user not found"
        )
    }

    @Test
    fun `should return empty list of devices when no devices assigned to user`() {
        // Given
        val user = UserFixture.createUser()
        userQueryRepository.save(user)

        // When
        val devices = userQueryRepository.findDevicesByUserId(user.id!!)

        // Then
        assertTrue(devices.isEmpty(), "Expected no devices for the user")
    }

    @Test
    fun `should return false when assigning a device to a non-existent user`() {
        // Given
        val userId = ObjectId() // Non-existent user ID
        val deviceId = UserFixture.createDevice().id!!

        // When
        val result = userQueryRepository.assignDeviceToUser(userId, deviceId)

        // Then
        assertFalse(result, "Expected false when assigning device to non-existent user")
    }

    @Test
    fun `should find all users when multiple users are saved`() {
        // Given
        val user1 = UserFixture.createUser().copy(name = "User1", email = "user1@example.com")
        val user2 = UserFixture.createUser().copy(name = "User2", email = "user2@example.com")
        userQueryRepository.save(user1)
        userQueryRepository.save(user2)

        // When
        val users = userQueryRepository.findAll()

        // Then
        val expectedUsers = listOf(user1, user2)
        assertTrue(users.containsAll(expectedUsers), "Expected users not found in the repository")
    }

    @Test
    fun `should reflect the device assignment on the user`() {
        // Given
        val user = UserFixture.createUser()
        val device = UserFixture.createDevice()

        userQueryRepository.save(user)
        deviceQueryRepository.save(device)

        // When
        userQueryRepository.assignDeviceToUser(user.id!!, device.id!!)

        // Then
        val updatedDevice = deviceQueryRepository.findById(device.id!!)
        assertEquals(user.id, updatedDevice?.userId)
    }

    @Test
    fun `should reflect the user update after being updated`() {
        // Given
        val user = UserFixture.createUser()
        userQueryRepository.save(user)

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
        userQueryRepository.save(updatedUser)

        // Then
        val foundUser = userQueryRepository.findById(user.id!!)
        assertEquals(updatedUserDto.name, foundUser?.name)
        assertEquals(updatedUserDto.email, foundUser?.email)
        assertEquals(updatedUserDto.phoneNumber, foundUser?.phoneNumber)
        assertEquals(updatedUserDto.userPassword, foundUser?.userPassword)
    }

    @Test
    fun `should not find a user after it is deleted`() {
        // Given
        val user = UserFixture.createUser()
        userQueryRepository.save(user)

        // When
        userQueryRepository.deleteById(user.id!!)
        val foundUser = userQueryRepository.findById(user.id!!)

        // Then
        assertNull(foundUser)
    }

    @Test
    fun `should find user by username`() {
        // Given
        val user = UserFixture.createUser().copy(name = "findUserName")
        userQueryRepository.save(user)

        // When
        val foundUser = userQueryRepository.findByUserName(user.name!!)

        // Then
        assertEquals(user, foundUser)
    }

    @Test
    fun `should find user by email`() {
        // Given
        val user = UserFixture.createUser().copy(email = "user@find.com")
        userQueryRepository.save(user)

        // When
        val foundUser = userQueryRepository.findByUserEmail(user.email!!)

        // Then
        assertEquals(user, foundUser)
    }

    @Test
    fun `should return false when assigning a non-existent device to user`() {
        // Given
        val user = UserFixture.createUser()
        userQueryRepository.save(user)

        val nonExistentDeviceId = ObjectId() // Non-existent device ID

        // When
        val result = userQueryRepository.assignDeviceToUser(user.id!!, nonExistentDeviceId)

        // Then
        assertFalse(result, "Expected false when assigning non-existent device to user")
    }

    @Test
    fun `should delete non-existent user without error`() {
        // Given
        val nonExistentUserId = ObjectId()

        // When
        userQueryRepository.deleteById(nonExistentUserId)

        // Then
        val foundUser = userQueryRepository.findById(nonExistentUserId)
        assertNull(foundUser, "No user should be found after attempting to delete non-existent user")
    }

    @Test
    fun `should return empty list when querying devices of a non-existent user`() {
        // Given
        val nonExistentUserId = ObjectId() // A user ID that does not exist in the database

        // When
        val devices = userQueryRepository.findDevicesByUserId(nonExistentUserId)

        // Then
        assertTrue(devices.isEmpty(), "Expected an empty list when the user does not exist")
    }

    @Test
    fun `should return null when querying non-existent user`() {
        // Given
        val user = UserFixture.createUser().copy(devices = null) // Create a non-existent user ID

        // When
        val devices = userQueryRepository.findDevicesByUserId(user.id!!)

        // Then
        assertTrue(devices.isEmpty(), "Expected an empty list when the user does not exist")
    }
}
