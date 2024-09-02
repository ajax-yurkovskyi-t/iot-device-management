package com.example.iot_management_device.beanpostprocessor

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

data class MethodAttempt(
    val maxAttempts: Int,
    val lockoutDurationMillis: Long,
    val attempts: AtomicInteger = AtomicInteger(0),
    val lockoutEndTimeMillis: AtomicLong = AtomicLong(0),
) {
    fun incrementAttempts() {
        attempts.incrementAndGet()
    }

    fun hasExceededLimit() =
        attempts.get() > maxAttempts

    fun isLockedOut() =
        lockoutEndTimeMillis.get() > System.currentTimeMillis()

    fun lockOut() {
        lockoutEndTimeMillis.set(System.currentTimeMillis() + lockoutDurationMillis)
        attempts.set(0)
    }

    fun getRemainingLockoutTime(): Long {
        val remainingTime = lockoutEndTimeMillis.get() - System.currentTimeMillis()
        return TimeUnit.MILLISECONDS.toSeconds(remainingTime)
    }
}
