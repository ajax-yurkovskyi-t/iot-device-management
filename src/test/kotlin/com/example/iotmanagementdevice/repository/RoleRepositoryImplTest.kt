package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class RoleRepositoryImplTest : AbstractMongoTest {

    @Autowired
    private lateinit var roleRepositoryImpl: RoleRepositoryImpl

    @Test
    fun `should find role by id when saved`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleRepositoryImpl.save(role)

        // When
        val foundRole = roleRepositoryImpl.findRoleById(role.id!!.toString())

        // Then
        assertEquals(role, foundRole)
    }

    @Test
    fun `should find all roles when multiple roles are saved`() {
        // Given
        val roleAdmin = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        val roleUser = MongoRole(ObjectId(), MongoRole.RoleName.USER)
        roleRepositoryImpl.save(roleAdmin)
        roleRepositoryImpl.save(roleUser)

        // When
        val roles = roleRepositoryImpl.findAll()

        // Then
        val expectedRoles = listOf(roleAdmin, roleUser)
        assertTrue(roles.containsAll(expectedRoles), "Expected roles not found in the repository")
    }

    @Test
    fun `should not find role when deleted`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleRepositoryImpl.save(role)

        // When
        roleRepositoryImpl.deleteById(role.id!!.toString())
        val foundRole = roleRepositoryImpl.findRoleById(role.id!!.toString())

        // Then
        assertNull(foundRole)
    }

    @Test
    fun `should find role by role name when saved`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleRepositoryImpl.save(role)

        // When
        val foundRole = roleRepositoryImpl.findByRoleName(MongoRole.RoleName.ADMIN)

        // Then
        assertEquals(MongoRole.RoleName.ADMIN, foundRole?.roleName)
    }
}
