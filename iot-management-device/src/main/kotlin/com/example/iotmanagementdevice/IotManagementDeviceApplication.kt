package com.example.iotmanagementdevice

import com.example.iotmanagementdevice.config.RedisProperties
import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableMongock
@EnableConfigurationProperties(RedisProperties::class)
class IotManagementDeviceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<IotManagementDeviceApplication>(*args)
}
