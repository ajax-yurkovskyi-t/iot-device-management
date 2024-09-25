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
    fun `should find role by id when saved`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleQueryRepository.save(role)

        // When
        val foundRole = roleQueryRepository.findRoleById(role.id!!)

        // Then
        assertEquals(role, foundRole)
    }

    @Test
    fun `should find all roles when multiple roles are saved`() {
        // Given
        val roleAdmin = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        val roleUser = MongoRole(ObjectId(), MongoRole.RoleName.USER)
        roleQueryRepository.save(roleAdmin)
        roleQueryRepository.save(roleUser)

        // When
        val roles = roleQueryRepository.findAll()

        // Then
        val expectedRoles = listOf(roleAdmin, roleUser)
        assertTrue(roles.containsAll(expectedRoles), "Expected roles not found in the repository")
    }

    @Test
    fun `should not find role when deleted`() {
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
    fun `should find role by role name when saved`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleQueryRepository.save(role)

        // When
        val foundRole = roleQueryRepository.findByRoleName(MongoRole.RoleName.ADMIN)

        // Then
        assertEquals(MongoRole.RoleName.ADMIN, foundRole?.roleName)
    }
}
