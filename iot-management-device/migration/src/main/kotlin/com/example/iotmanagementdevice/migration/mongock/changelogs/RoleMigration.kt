package com.example.iotmanagementdevice.migration.mongock.changelogs

import com.example.iotmanagementdevice.role.infrastructure.mongo.entity.MongoRole
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "roleMigration", order = "001", author = "Taras Yurkovskyi")
class RoleMigration {
    @Execution
    fun applyRoleMigration(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists(MongoRole.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MongoRole.COLLECTION_NAME)
        }
        seedRoles(mongoTemplate)
    }

    private fun seedRoles(mongoTemplate: MongoTemplate) {
        val roles = listOf(
            MongoRole(id = null, roleName = MongoRole.RoleName.USER),
            MongoRole(id = null, roleName = MongoRole.RoleName.ADMIN)
        )
        mongoTemplate.insertAll(roles)
    }

    @RollbackExecution
    fun rollbackDeviceMigration(mongoTemplate: MongoTemplate) {
        mongoTemplate.dropCollection(MongoRole.COLLECTION_NAME)
    }
}
