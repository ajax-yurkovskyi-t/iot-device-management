package com.example.iotmanagementdevice.migration.mongock.changelogs

import com.example.iotmanagementdevice.user.infrastructure.mongo.entity.MongoUser
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.IndexOperations

@ChangeUnit(id = "userMigration", order = "002", author = "Taras Yurkovskyi")
class UserMigration {

    @Execution
    fun applyDeviceMigration(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists(MongoUser.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MongoUser.COLLECTION_NAME)
        }
        createIndexes(mongoTemplate)
    }

    private fun createIndexes(mongoTemplate: MongoTemplate) {
        val indexOps: IndexOperations = mongoTemplate.indexOps(MongoUser.COLLECTION_NAME)
        indexOps.ensureIndex(Index().on(MongoUser::email.name, Sort.Direction.ASC))
    }

    @RollbackExecution
    fun rollbackDeviceMigration(mongoTemplate: MongoTemplate) {
        if (mongoTemplate.collectionExists(MongoUser.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoUser.COLLECTION_NAME)
        }
    }
}
