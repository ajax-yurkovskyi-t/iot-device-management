package com.example.iot_management_device.beanpostprocessor

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MethodAttemptLimiter(
    val maxAttempts: Int = 5,
    val lockoutDuration: Long = 5 * 60 * 1000, // 5 minutes in milliseconds
)
