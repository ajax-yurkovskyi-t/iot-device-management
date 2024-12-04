package com.example.iotmanagementdevice.role.infrastructure.mongo.repository

import com.example.iotmanagementdevice.role.application.port.output.RoleRepositoryOutPort
import com.example.iotmanagementdevice.role.domain.CreateRole
import com.example.iotmanagementdevice.role.domain.Role
import com.example.iotmanagementdevice.role.infrastructure.mongo.entity.MongoRole
import com.example.iotmanagementdevice.role.infrastructure.mongo.mapper.RoleMapper
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoRoleRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
    private val roleMapper: RoleMapper,
) : RoleRepositoryOutPort {
    override fun findById(roleId: String): Mono<Role> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(roleId))
        return mongoTemplate.findOne(query, MongoRole::class.java)
            .map { roleMapper.toDomain(it) }
    }

    override fun findAll(): Flux<Role> =
        mongoTemplate.findAll(MongoRole::class.java)
            .map { roleMapper.toDomain(it) }

    override fun save(role: Role): Mono<Role> =
        mongoTemplate.save(roleMapper.toEntity(role))
            .map { roleMapper.toDomain(it) }

    override fun save(role: CreateRole): Mono<Role> =
        mongoTemplate.save(roleMapper.toEntity(role))
            .map { roleMapper.toDomain(it) }

    override fun deleteById(roleId: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(roleId))
        return mongoTemplate.remove(query, MongoRole::class.java).thenReturn(Unit)
    }

    override fun findByRoleName(mongoRoleName: Role.RoleName): Mono<Role> {
        val query = Query(Criteria.where(MongoRole::roleName.name).isEqualTo(mongoRoleName))
        return mongoTemplate.findOne(query, MongoRole::class.java)
            .map { roleMapper.toDomain(it) }
    }
}
