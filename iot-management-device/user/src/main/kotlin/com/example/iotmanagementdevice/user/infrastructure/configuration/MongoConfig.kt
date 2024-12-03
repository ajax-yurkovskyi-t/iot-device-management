package com.example.iotmanagementdevice.user.infrastructure.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager

@Configuration
class MongoConfig {

    @Bean
    fun reactiveTransactionManager(
        reactiveMongoDatabaseFactory: ReactiveMongoDatabaseFactory,
    ): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory)
    }
}
