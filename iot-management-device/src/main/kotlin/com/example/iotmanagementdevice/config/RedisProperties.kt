package com.example.iotmanagementdevice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "spring.data.redis")
data class RedisProperties(
    val timeout: Duration,
    val ttl: Duration,
    val port: Int,
    val host: String,
)
