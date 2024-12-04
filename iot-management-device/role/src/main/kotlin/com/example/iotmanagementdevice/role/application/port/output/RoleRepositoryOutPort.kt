package com.example.iotmanagementdevice.role.application.port.output

import com.example.iotmanagementdevice.role.domain.CreateRole
import com.example.iotmanagementdevice.role.domain.Role
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RoleRepositoryOutPort {
    fun findById(roleId: String): Mono<Role>

    fun findByRoleName(roleName: Role.RoleName): Mono<Role>

    fun findAll(): Flux<Role>

    fun save(role: Role): Mono<Role>

    fun save(role: CreateRole): Mono<Role>

    fun deleteById(roleId: String): Mono<Unit>
}
