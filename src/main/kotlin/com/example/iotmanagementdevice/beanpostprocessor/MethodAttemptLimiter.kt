package com.example.iotmanagementdevice.beanpostprocessor

import com.example.iotmanagementdevice.beanpostprocessor.MethodAttemptLimits.DEFAULT_LOCKOUT_DURATION_MILLIS
import com.example.iotmanagementdevice.beanpostprocessor.MethodAttemptLimits.DEFAULT_MAX_ATTEMPTS

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class MethodAttemptLimiter(
    val maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
    val lockoutDurationMillis: Long = DEFAULT_LOCKOUT_DURATION_MILLIS,
)
