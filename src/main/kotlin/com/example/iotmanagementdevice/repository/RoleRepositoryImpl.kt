package com.example.iotmanagementdevice.repository

import com.example.iotmanagementdevice.model.MongoRole
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Fields
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class RoleRepositoryImpl(private val mongoTemplate: MongoTemplate) : RoleRepository {
    override fun findRoleById(roleId: String): MongoRole? {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(roleId))
        return mongoTemplate.findOne(query, MongoRole::class.java)
    }

    override fun findAll(): List<MongoRole> =
        mongoTemplate.findAll(MongoRole::class.java)

    override fun save(role: MongoRole): MongoRole =
        mongoTemplate.save(role)

    override fun deleteById(roleId: String) {
        val query = Query(Criteria.where(Fields.UNDERSCORE_ID).isEqualTo(roleId))
        mongoTemplate.remove(query, MongoRole::class.java)
    }

    override fun findByRoleName(mongoRoleName: MongoRole.RoleName): MongoRole? {
        val query = Query(Criteria.where(MongoRole::roleName.name).isEqualTo(mongoRoleName))
        return mongoTemplate.findOne(query, MongoRole::class.java)
    }
}
