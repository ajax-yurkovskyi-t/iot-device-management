package com.example.iot_management_device.beanpostprocessor

object MethodAttemptLimits {
    const val DEFAULT_MAX_ATTEMPTS = 5
    const val DEFAULT_LOCKOUT_DURATION_MILLIS = 5 * 60 * 1000L // 5 minutes in milliseconds
}
