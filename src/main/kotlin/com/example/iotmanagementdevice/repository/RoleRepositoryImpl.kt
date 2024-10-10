package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class RoleRepositoryImpl(private val mongoTemplate: ReactiveMongoTemplate) : RoleRepository {
    override fun findRoleById(roleId: String): Mono<MongoRole> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(roleId))
        return mongoTemplate.findOne(query, MongoRole::class.java)
    }

    override fun findAll(): Flux<MongoRole> =
        mongoTemplate.findAll(MongoRole::class.java)

    override fun save(role: MongoRole): Mono<MongoRole> =
        mongoTemplate.save(role)

    override fun deleteById(roleId: String): Mono<Unit> {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(roleId))
        return mongoTemplate.remove(query, MongoRole::class.java).thenReturn(Unit)
    }

    override fun findByRoleName(mongoRoleName: MongoRole.RoleName): Mono<MongoRole> {
        val query = Query(Criteria.where(MongoRole::roleName.name).isEqualTo(mongoRoleName))
        return mongoTemplate.findOne(query, MongoRole::class.java)
    }
}
