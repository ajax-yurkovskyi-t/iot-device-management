package com.example.iotmanagementdevice

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableMongock
class IotManagementDeviceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<IotManagementDeviceApplication>(*args)
}
