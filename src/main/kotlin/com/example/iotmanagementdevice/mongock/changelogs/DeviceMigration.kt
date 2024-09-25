package com.example.iotmanagementdevice.mongock.changelogs

import com.example.iotmanagementdevice.model.MongoDevice
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.IndexOperations

@ChangeUnit(id = "deviceMigration", order = "001", author = "Taras Yurkovskyi")
class DeviceMigration {

    @Execution
    fun applyDeviceMigration(mongoTemplate: MongoTemplate) {
        if (!mongoTemplate.collectionExists(MongoDevice.COLLECTION_NAME)) {
            mongoTemplate.createCollection(MongoDevice.COLLECTION_NAME)
        }
        createIndexes(mongoTemplate)
    }

    private fun createIndexes(mongoTemplate: MongoTemplate) {
        val indexOps: IndexOperations = mongoTemplate.indexOps(MongoDevice.COLLECTION_NAME)
        indexOps.ensureIndex(Index().on(MongoDevice::name.name, Sort.Direction.ASC).named("name_index"))
    }

    @RollbackExecution
    fun rollbackDeviceMigration(mongoTemplate: MongoTemplate) {
        val indexOps: IndexOperations = mongoTemplate.indexOps(MongoDevice.COLLECTION_NAME)

        if (indexOps.indexInfo.any { it.name == "name_index" }) {
            indexOps.dropIndex("name_index")
        }

        if (mongoTemplate.collectionExists(MongoDevice.COLLECTION_NAME)) {
            mongoTemplate.dropCollection(MongoDevice.COLLECTION_NAME)
        }
    }
}
