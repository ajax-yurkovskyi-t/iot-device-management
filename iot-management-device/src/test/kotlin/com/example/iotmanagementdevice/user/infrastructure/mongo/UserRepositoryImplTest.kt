package com.example.iotmanagementdevice.user.infrastructure.mongo

import com.example.iotmanagementdevice.device.DeviceFixture.createDevice
import com.example.iotmanagementdevice.device.infrastructure.mongo.repository.MongoDeviceRepository
import com.example.iotmanagementdevice.user.UserFixture
import com.example.iotmanagementdevice.user.domain.User
import com.example.iotmanagementdevice.user.infrastructure.mongo.repository.MongoUserRepository
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

class UserRepositoryImplTest : AbstractMongoTest {

    @Autowired
    private lateinit var userRepository: MongoUserRepository

    @Autowired
    private lateinit var deviceRepository: MongoDeviceRepository

    @Test
    fun `should find user by id when saved`() {
        // Given
        val user = UserFixture.createUser()
        userRepository.save(user).block()

        // When
        val foundUser = userRepository.findById(user.id!!.toString())

        // Then
        foundUser.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should find user by id when saved `() {
        // Given
        val createUser = UserFixture.createUserCreate()
        val expectedUser = User(
            id = ObjectId().toString(),
            name = createUser.name,
            email = createUser.email,
            phoneNumber = createUser.phoneNumber,
            userPassword = createUser.userPassword,
            roles = createUser.roles,
            devices = mutableListOf()
        )

        // When
        val savedUser = userRepository.save(createUser)

        // Then
        savedUser.test()
            .assertNext { user ->
                assertEquals(
                    expectedUser.copy(id = user.id),
                    user
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should return false when assigning a device to a non-existent user`() {
        // Given
        val userId = ObjectId() // Non-existent user ID
        val deviceId = createDevice().id!!

        // When
        val result = userRepository.assignDeviceToUser(userId.toString(), deviceId.toString())

        // Then
        result.test()
            .verifyError<RuntimeException>()
    }

    @Test
    fun `should find all users when multiple users are saved`() {
        // Given
        val user1 = UserFixture.createUser().copy(name = "User1", email = "user1@example.com")
        val user2 = UserFixture.createUser().copy(name = "User2", email = "user2@example.com")
        userRepository.save(user1).block()
        userRepository.save(user2).block()

        // When
        val users = userRepository.findAll().collectList()

        // Then
        users.test()
            .expectNextMatches {
                it.containsAll(listOf(user1, user2))
            }
            .verifyComplete()
    }

    @Test
    fun `should reflect the device assignment on the user`() {
        // Given
        val user = UserFixture.createUser()
        val device = createDevice()

        userRepository.save(user).block()
        deviceRepository.save(device).block()

        // When
        userRepository.assignDeviceToUser(user.id!!.toString(), device.id!!.toString()).block()

        // Then
        deviceRepository.findById(device.id!!.toString()).test()
            .expectNextMatches { it.userId == user.id }
            .verifyComplete()
    }

    @Test
    fun `should rollback transaction when device assignment fails`() {
        // Given
        val user = UserFixture.createUser()
        userRepository.save(user).block()!!

        val invalidDeviceId = ObjectId() // Non-existent device ID

        // When & Then
        userRepository.assignDeviceToUser(user.id!!, invalidDeviceId.toString()).test()
            .verifyError<RuntimeException>()

        val updatedUser = userRepository.findById(user.id!!.toString()).block()!!

        assertTrue(updatedUser.devices!!.isEmpty()) { "User's devices should remain unchanged" }
    }

    @Test
    fun `should reflect the user update after being updated`() {
        // Given
        val user = UserFixture.createUser()
        userRepository.save(user).block()

        val updatedUser = user.copy(
            name = "Updated Name",
            email = "updated@example.com",
            phoneNumber = "123-456-7890",
            userPassword = "newPassword"
        )

        // When
        userRepository.save(updatedUser).block()

        // Then
        userRepository.findById(user.id!!.toString())
            .test()
            .expectNext(updatedUser)
            .verifyComplete()
    }

    @Test
    fun `should not find a user after it is deleted`() {
        // Given
        val user = UserFixture.createUser()
        userRepository.save(user).block()

        // When
        userRepository.deleteById(user.id!!.toString()).block()

        // Then
        userRepository.findById(user.id!!.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `should find user by username`() {
        // Given
        val user = UserFixture.createUser().copy(name = "findUserName")
        userRepository.save(user).block()

        // When
        val foundUser = userRepository.findByUserName(user.name!!)

        // Then
        foundUser.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should find user by email`() {
        // Given
        val user = UserFixture.createUser().copy(email = "user@find.com")
        userRepository.save(user).block()

        // When
        val foundUser = userRepository.findByUserEmail(user.email!!)

        // Then
        foundUser.test()
            .expectNext(user)
            .verifyComplete()
    }

    @Test
    fun `should return false when assigning a non-existent device to user`() {
        // Given
        val user = UserFixture.createUser()
        userRepository.save(user).block()

        val nonExistentDeviceId = ObjectId() // Non-existent device ID

        // When
        val result = userRepository.assignDeviceToUser(user.id!!.toString(), nonExistentDeviceId.toString())

        // Then
        result.test()
            .assertNext {
                assertFalse(it) { "Expected false when assigning a non-existent device to the user" }
            }
    }

    @Test
    fun `should delete non-existent user without error`() {
        // Given
        val nonExistentUserId = ObjectId()

        // When
        userRepository.deleteById(nonExistentUserId.toString()).block()

        // Then
        userRepository.findById(nonExistentUserId.toString())
            .test()
            .verifyComplete()
    }
}
