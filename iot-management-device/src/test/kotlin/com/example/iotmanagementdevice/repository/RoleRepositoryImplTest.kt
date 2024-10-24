package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class RoleRepositoryImplTest : AbstractMongoTest {

    @Autowired
    private lateinit var roleRepositoryImpl: RoleRepositoryImpl

    @Test
    fun `should find role by id when saved`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleRepositoryImpl.save(role).block()

        // When
        val foundRole = roleRepositoryImpl.findRoleById(role.id!!.toString())

        // Then
        foundRole.test()
            .expectNext(role)
            .verifyComplete()
    }

    @Test
    fun `should find all roles when multiple roles are saved`() {
        // Given
        val roleAdmin = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        val roleUser = MongoRole(ObjectId(), MongoRole.RoleName.USER)
        roleRepositoryImpl.save(roleAdmin).block()
        roleRepositoryImpl.save(roleUser).block()

        // When
        val roles = roleRepositoryImpl.findAll().collectList()

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
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleRepositoryImpl.save(role).block()

        // When
        roleRepositoryImpl.deleteById(role.id!!.toString()).block()

        // Then
        roleRepositoryImpl.findRoleById(role.id!!.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `should find role by role name when saved`() {
        // Given
        val role = MongoRole(ObjectId(), MongoRole.RoleName.ADMIN)
        roleRepositoryImpl.save(role).block()

        // When
        val foundRole = roleRepositoryImpl.findByRoleName(MongoRole.RoleName.ADMIN)

        // Then
        foundRole.test()
            .expectNextMatches { it.roleName == MongoRole.RoleName.ADMIN }
            .verifyComplete()
    }
}
