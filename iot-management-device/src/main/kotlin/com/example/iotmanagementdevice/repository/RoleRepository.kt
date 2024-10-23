package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoleRepository {
    fun findRoleById(roleId: String): Mono<MongoRole>
    fun findAll(): Flux<MongoRole>
    fun save(role: MongoRole): Mono<MongoRole>
    fun deleteById(roleId: String): Mono<Unit>
    fun findByRoleName(mongoRoleName: MongoRole.RoleName): Mono<MongoRole>
}
