package com.example.iotmanagementdevice.role.infrastructure.mongo

import com.example.iotmanagementdevice.role.domain.CreateRole
import com.example.iotmanagementdevice.role.domain.Role
import com.example.iotmanagementdevice.role.infrastructure.mongo.repository.MongoRoleRepository
import com.example.iotmanagementdevice.utils.AbstractMongoTest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class RoleRepositoryImplTest : AbstractMongoTest {

    @Autowired
    private lateinit var roleRepository: MongoRoleRepository

    @Test
    fun `should find role by id when saved`() {
        // Given
        val role = Role(ObjectId().toString(), Role.RoleName.ADMIN)
        roleRepository.save(role).block()

        // When
        val foundRole = roleRepository.findById(role.id!!.toString())

        // Then
        foundRole.test()
            .expectNext(role)
            .verifyComplete()
    }

    @Test
    fun `should find user by id when saved `() {
        // Given
        val createRole = CreateRole(Role.RoleName.USER)
        val expectedRole = Role(
            id = ObjectId().toString(),
            roleName = createRole.roleName
        )

        // When
        val savedUser = roleRepository.save(createRole)

        // Then
        savedUser.test()
            .assertNext { role ->
                assertEquals(
                    expectedRole.copy(id = role.id),
                    role
                )
            }
            .verifyComplete()
    }

    @Test
    fun `should find all roles when multiple roles are saved`() {
        // Given
        val roleAdmin = Role(ObjectId().toString(), Role.RoleName.ADMIN)
        val roleUser = Role(ObjectId().toString(), Role.RoleName.USER)
        roleRepository.save(roleAdmin).block()
        roleRepository.save(roleUser).block()

        // When
        val roles = roleRepository.findAll().collectList()

        // Then
        roles.test()
            .expectNextMatches {
                it.containsAll(listOf(roleAdmin, roleUser))
            }
            .verifyComplete()
    }

    @Test
    fun `should not find role when deleted`() {
        // Given
        val role = Role(ObjectId().toString(), Role.RoleName.ADMIN)
        roleRepository.save(role).block()

        // When
        roleRepository.deleteById(role.id!!.toString()).block()

        // Then
        roleRepository.findById(role.id!!.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `should find role by role name when saved`() {
        // Given
        val role = Role(ObjectId().toString(), Role.RoleName.ADMIN)
        roleRepository.save(role).block()

        // When
        val foundRole = roleRepository.findByRoleName(Role.RoleName.ADMIN)

        // Then
        foundRole.test()
            .expectNextMatches { it.roleName == Role.RoleName.ADMIN }
            .verifyComplete()
    }
}
