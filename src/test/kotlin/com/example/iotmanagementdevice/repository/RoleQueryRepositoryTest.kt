package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RoleQueryRepositoryTest : AbstractMongoTest {

    @Autowired
    private lateinit var roleQueryRepository: RoleQueryRepository

    @Test
    fun `given a role when saved then it can be found by id`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleQueryRepository.save(role)

        // When
        val foundRole = roleQueryRepository.findRoleById(role.id!!)

        // Then
        assertEquals(role, foundRole)
    }

    @Test
    fun `given multiple roles when saved then all can be found`() {
        // Given
        val roleAdmin = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        val roleUser = MongoRole(ObjectId(), MongoRole.RoleName.USER)
        roleQueryRepository.save(roleAdmin)
        roleQueryRepository.save(roleUser)
        // When
        val roles = roleQueryRepository.findAll()

        // Then
        val expectedRoles = listOf(roleAdmin, roleUser)
        assertTrue(roles.containsAll(expectedRoles), "Expected users not found in the repository")
    }

    @Test
    fun `given a role when deleted then it cannot be found`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleQueryRepository.save(role)

        // When
        roleQueryRepository.deleteById(role.id!!)
        val foundRole = roleQueryRepository.findRoleById(role.id!!)

        // Then
        assertNull(foundRole)
    }

    @Test
    fun `given a role when saved then it can be found by role name`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleQueryRepository.save(role)

        // When
        val foundRole = roleQueryRepository.findByRoleName(MongoRole.RoleName.ADMIN)

        // Then
        assertEquals(MongoRole.RoleName.ADMIN, foundRole?.roleName)
    }
}
